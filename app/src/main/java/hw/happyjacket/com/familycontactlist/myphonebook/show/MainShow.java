package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.MainAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

/**
 * Created by root on 16-4-1.
 */
public class MainShow extends PhoneShow {
    private PhoneLocationMaster PLMaster;
    private boolean errorOfGetingLocation = false;
    HashMap<String,Integer> nmapp = new HashMap<>(); //the map between number and position

    public MainShow(Context context, int table) {
        super(context,table);
        PLMaster = new PhoneLocationMaster(context);
    }

    public void ElementCopy() {
        int count = 0;
        mPhoneListElementList.removeAllElements();
        nmapp.clear();
        for (HashMap<String,String> i : mPhoneListElementList_backup) {
            mPhoneListElementList.add(new HashMap<String, String>(i));
            nmapp.put(i.get(PhoneDictionary.NUMBER), count++);
        }
        mPhoneListElementList = mDecorate.decorate(mPhoneListElementList);
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {
        HashMap<String,String> t;
        HashMap<String,String> l, v;
        int i = 0;
        boolean hasErase = false;
        String date = PhoneDictionary.DATE;
        String number = PhoneDictionary.NUMBER;
        try {
            t = filter(input);
            while (i < mPhoneListElementList.size()) {
                l = mPhoneListElementList.get(i);
                v = mPhoneListElementList_backup.get(i);
                if (!hasErase && l.get(number).equals(input.get(number))) {
                    mPhoneListElementList.remove(i);
                    mPhoneListElementList_backup.remove(i);
                    hasErase = true;
                    continue;
                }
                l.put(date, accessory.decorate(date, v.get(date)));
                ++i;
            }
            mPhoneListElementList_backup.insertElementAt(new HashMap<String, String>(t), 0);
            t.put(date, accessory.decorate(date, t.get(date)));
            mPhoneListElementList.insertElementAt(t, 0);
            sPhoneAdapter.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void DeleteTheNewestOne(String id, String number) {
        phoneList.delete(id);
        phoneList.findTheNewestOne(DataBaseDictionary.CallLog_Projection, PhoneDictionary.PhoneAll, PhoneDictionary.NUMBER, number);
        phoneList.connectDataBase();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        ElementCopy();
        sPhoneAdapter.notifyDataSetChanged();
    }

    @Override
    public void DeletePhoneElement(String id, String number) {
       if(nmapp.containsKey(number))
           DeleteTheNewestOne(id,number);
    }

    public void DeletePhoneElementAll(String number) {
        phoneList.deleteAll(number);
        mPhoneListElementList.remove(nmapp.get(number));
        sPhoneAdapter.notifyDataSetChanged();
    }

    public void getLocation(String phoneNumber) {

    }

    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderBy)
    {
        Vector<HashMap<String, String>> t = phoneList.init();
        int count = 0;
        if(t == null)
        {
            phoneList.setProjection(projection);
            phoneList.setSelection(selection);
            phoneList.setArgument(argument);
            phoneList.setOrderby(orderBy);
            phoneList.connectDataBase();
            mPhoneListElementList_backup = phoneList.getPhoneList();
        }
        else
        {
            for(HashMap<String,String> i : t)
                mPhoneListElementList_backup.add(new HashMap<String, String>(i));
        }


        mDecorate = new Decorate(accessory);
        ElementCopy();
        for(int i = 0 ; i < mPhoneListElementList_backup.size() ; ++i){
            mPhoneListElementList_backup.get(i).put(PhoneDictionary.LOCATION,"");
            mPhoneListElementList.get(i).put(PhoneDictionary.LOCATION,"");
        }

        sPhoneAdapter = new MainAdapter(context, table, mPhoneListElementList,index);

        ArrayList<String> phoneNumberList = new ArrayList<>();
        for (HashMap<String,String> i : mPhoneListElementList)
           phoneNumberList.add(i.get(PhoneDictionary.NUMBER));

        Message msg = new Message();
        msg.obj = phoneNumberList;
        msg.what = 0;
       // handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    final Message msg_for_location = new Message();
                    msg_for_location.what = 1;

                    ArrayList<String> phoneNumberList = (ArrayList<String>) msg.obj;
                    final Vector<HashMap<String, String> > locations = new Vector<>();

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
                                Log.d("hehe-response", phoneNumber + " " + response);
                                // if there is no location for the querying phonenumber
                                if (response.contains("http") || response.contains("没有")) {
//                                    HashMap<String,String> tmp = new HashMap<>();
//                                    tmp.put(phoneNumber, "");
//                                    locations.add(tmp);
//                                    return ;
                                    response = null;
                                }

                                PLMaster.add(phoneNumber, response);
                                Log.d("hehe-number", PLMaster.get(phoneNumber)[2]);
                                HashMap<String,String> tmp = new HashMap<>();
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
                    }

                    msg_for_location.obj = locations;
                    handler.sendMessage(msg_for_location);
                    break;

                case 1:
                    Vector<HashMap<String, String> > t = (Vector<HashMap<String, String> >) msg.obj;
                    for(HashMap<String,String> i : t){
                        for(Map.Entry<String,String> j : i.entrySet()) {
                            int indexs = nmapp.get(j.getKey());
                            if("".equals(j.getValue()) || j.getValue() == null ){
                                mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION,  "");
                                mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, "");
                            }
                            else {
                                mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                                mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                            };
                        }
                    }
                    sPhoneAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
}
