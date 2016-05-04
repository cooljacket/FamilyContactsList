package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationDBHelper;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-8.
 */
public class InitService extends Service {

    private PhoneLocationMaster PLMaster = new PhoneLocationMaster(this);

    private boolean errorOfGetingLocation = false;

    private int index;

    private MyBinder mMyBinder = new MyBinder();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final ArrayList<String> phoneNumberList = (ArrayList<String>) msg.obj;
                    final Message msg_for_location = new Message();
                    final Vector<HashMap<String, String> > locations = new Vector<>();
                    msg_for_location.what = 1;
                    errorOfGetingLocation = false;
                    int ACCESS_NETWORK_COUNT = 0;
                    for (final String phoneNumber : phoneNumberList) {
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
                                int state = PhoneLocationDBHelper.CACHED;

                                // if there is no location for the querying phonenumber
                                if (response.contains("手机号码错误") || response.contains("没有此号码记录")) {
                                    response = null;
                                    state = PhoneLocationDBHelper.NOSUCH;
                                } else if (response.contains("http")) {
                                    state = PhoneLocationDBHelper.ERROR;
                                }

                                PLMaster.add(phoneNumber, response, state);
                                String[] result = PLMaster.get(phoneNumber);
                                if (result == null || result.length < 3)
                                    return ;
                                Log.d("init-service", result[0] + ", " + state);
                                HashMap<String, String> tmp = new HashMap<>();
                                tmp.put(phoneNumber, result[0] + " " + result[1]);
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
                        if (++ACCESS_NETWORK_COUNT > 15) {
                            break;
                        }
                     }
                    msg_for_location.obj = locations;
                    handler.sendMessage(msg_for_location);
                    break;

                case 1:
                    final Vector<HashMap<String, String> > t = (Vector<HashMap<String, String> >) msg.obj;
                    PhoneShow p = PhoneRegister.get(index);
                    for(HashMap<String,String> i : t){
                        for(Map.Entry<String,String> j : i.entrySet()) {
                            int indexs = p.getNmapp().get(j.getKey());
                            if("".equals(j.getValue()) || j.getValue() == null ){
                                p.getPhoneListElementList().get(indexs).put(PhoneDictionary.LOCATION,  "");
                                p.getPhoneListElementList().get(indexs).put(PhoneDictionary.LOCATION, "");
                            }
                            else {
                                p.getPhoneListElementList().get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                                p.getPhoneListElementList().get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                            };
                        }}
                    p.getPhoneAdapter().notifyDataSetChanged();
                    break;
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }


    public class MyBinder extends Binder{
        public void start(int ind, ArrayList<String> number){
            index = ind;
            Message message = handler.obtainMessage();
            message.what = 0;
            message.obj = number;
            handler.sendMessage(message);
        }

        public void stop(){
            stopSelf();
        }
    }




}
