package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
    private static Message msg_for_location ;
    private static String phoneNumber;
    private static String result;
    final static String HostURL = "http://webservice.webxml.com.cn";

    //set the flag to ensure that the thread will close if the main thread has closed
    public static void setFlag(boolean flag) {
        PhoneLocationThread.flag = flag;
    }

    public static void CheckLocation(Handler handler,Vector<String> phoneNumberList,Context context){
        msg_for_location = handler.obtainMessage();
        msg_for_location.what = 1;
        final PhoneLocationMaster PLMaster = new PhoneLocationMaster(context);
        final Vector<HashMap<String, String> > locations = new Vector<>();
        int ACCESS_NETWORK_COUNT = 0;

        for ( int y = 0 ; y < phoneNumberList.size() ; y++) {
            if(!flag)
                break;
            phoneNumber = phoneNumberList.get(y).replace(" ","").replace("+86","").replace("+","");
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

            String LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
            HttpConnectionUtil.get(LocationURL, new HttpConnectionUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    response = response.replaceAll("<[^>]+>", "");
                    int state = PhoneLocationDBHelper.CACHED;

                    // if there is no location for the querying phonenumber
                    if (response.contains("手机号码错误") || response.contains("没有此号码记录")) {
                        response = null;
                        state = PhoneLocationDBHelper.NOSUCH;
                    } else if (response.contains("http")) {
                        state = PhoneLocationDBHelper.ERROR;
                    }
                    Log.i("locationm", response);
                    PLMaster.add(phoneNumber, response, state);
                    String[] cache = PLMaster.get(phoneNumber);
                    if (cache == null || cache.length < 3)
                        return ;
                    HashMap<String, String> tmp = new HashMap<>();
                    tmp.put(phoneNumber, cache[2]);
                    locations.add(tmp);
                }

                @Override
                public void onError(Exception e) {
                    errorOfGetingLocation = true;
                }
            });

            if (errorOfGetingLocation) {
                locations.add(new HashMap<String, String>());
                continue;
            }
            // 每次限定最多从网络拿15个归属地，因为免费的api服务有限制，也没必要一下子拿太多
            if (++ACCESS_NETWORK_COUNT > 15)
            {
                break;
            }
        }
        msg_for_location.obj = locations;
        Message msg;
        if(handler.obtainMessage(msg_for_location.what, msg_for_location.obj) != null){
             msg = new Message();
             msg.what = msg_for_location.what;
             msg.obj= msg_for_location.obj;
             handler.sendMessage(msg);
        }
        else
            handler.sendMessage(msg_for_location);
    }

    public static void CheckLocation(Handler handler,final String phoneNumber,Context context) {
        errorOfGetingLocation = false;
        msg_for_location = handler.obtainMessage();
        msg_for_location.what = 0;

        final PhoneLocationMaster PLMaster = new PhoneLocationMaster(context);
        final HashMap<String, String>  location = new HashMap<>();

        String[] places = PLMaster.get(phoneNumber);
        if (places != null) {
            result = places[1];
            msg_for_location.obj = result;
            handler.sendMessage(msg_for_location);
            return;
        }

        String LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
        HttpConnectionUtil.get(LocationURL, new HttpConnectionUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                response = response.replaceAll("<[^>]+>", "");
                int state = PhoneLocationDBHelper.CACHED;
                if (response.contains("手机号码错误") || response.contains("没有此号码记录")) {
                    response = null;
                    state = PhoneLocationDBHelper.NOSUCH;
                } else if (response.contains("http")) {
                    state = PhoneLocationDBHelper.ERROR;
                }
                PLMaster.add(phoneNumber, response, state);
                result = PLMaster.get(phoneNumber)[1];
            }

            @Override
            public void onError(Exception e) {
                errorOfGetingLocation = true;
            }
        });

        if (errorOfGetingLocation) {
            handler.sendMessage(msg_for_location);
            return;
        }
        msg_for_location.obj = result;
        handler.sendMessage(msg_for_location);
    }
}
