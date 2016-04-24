package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationDBHelper;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;

/**
 * Created by root on 16-4-24.
 */
public class PhoneLocationThread {
    private static boolean errorOfGetingLocation = false;
    private static boolean flag = true;

    //set the flag to ensure that the thread will close if the main thread has closed
    public static void setFlag(boolean flag) {
        PhoneLocationThread.flag = flag;
    }

    public static void CheckLocation(Handler handler,ArrayList<String> phoneNumberList,Context context){
        final Message msg_for_location = handler.obtainMessage();
        msg_for_location.what = 1;
        final PhoneLocationMaster PLMaster = new PhoneLocationMaster(context);
        final Vector<HashMap<String, String> > locations = new Vector<>();
        int ACCESS_NETWORK_COUNT = 0;
        for (final String phoneNumber : phoneNumberList) {
            if(!flag)
                break;
            String[] places = PLMaster.get(phoneNumber);
            if (places != null) {
                HashMap<String,String> tmp = new HashMap<>();
                if(places[1] != null && places[0].equals(places[1]))
                    tmp.put(phoneNumber,places[0]);
                else
                    tmp.put(phoneNumber, places[0] + places[1]);
                locations.add(tmp);
                continue ;
            }

            final String HostURL = "http://webservice.webxml.com.cn";
            String LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
            HttpConnectionUtil.get(LocationURL, new HttpConnectionUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    response = response.replaceAll("<[^>]+>", "");
                    Log.d("hehe-response", phoneNumber + " " + response);
                    int state = PhoneLocationDBHelper.CACHED;

                    // if there is no location for the querying phonenumber
                    if (response.contains("手机号码错误") || response.contains("没有此号码记录")) {
                        response = null;
                        state = PhoneLocationDBHelper.NOSUCH;
                    } else if (response.contains("http")) {
                        state = PhoneLocationDBHelper.ERROR;
                    }
                    PLMaster.add(phoneNumber, response, state);
                    Log.d("hehe-number", PLMaster.get(phoneNumber)[2] + ", " + state);
                    HashMap<String, String> tmp = new HashMap<>();
                    tmp.put(phoneNumber, PLMaster.get(phoneNumber)[2]);
                    locations.add(tmp);
                }

                @Override
                public void onError(Exception e) {
                    errorOfGetingLocation = true;
                }
            });

            if (errorOfGetingLocation) {
                msg_for_location.obj = new Vector<HashMap<String, String> >();
                handler.sendMessage(msg_for_location);
                break;
            }

            // 每次限定最多从网络拿15个归属地，因为免费的api服务有限制，也没必要一下子拿太多
            if (++ACCESS_NETWORK_COUNT > 15)
            {
                break;
            }
        }
        msg_for_location.obj = locations;
        handler.sendMessage(msg_for_location);
    }
}
