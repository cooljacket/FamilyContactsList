package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.CommonSettingsAndFuncs;
import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CallLogAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneLocationThread;

/**
 * Created by root on 16-4-13.
 */
public class ContactShow extends PhoneShow {


    private String number;
    private String [] defaultList = new String[]{"查找中...","编辑联系人","加入黑名单","从黑名单中移除","删除联系人"};
    private String defaultNumber = "电话";
    private String location;

    private Thread mThread;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(msg.obj == null || "".equals((location = (String)msg.obj))) {
                        mPhoneListElementList.get(1).put(PhoneDictionary.DATE,"无信息");
                        sPhoneAdapter.notifyDataSetChanged();
                        break;
                    }
                     location = (String) msg.obj;
                     Log.i("getWeather",location);
                     new GetWeather(location).execute();
                    break;
                default:
                    break;

            }
        }
    };

    public ContactShow(Context context, int table) {
        super(context, table);
    }







    public String getDefaultNumber() {
        return defaultNumber;
    }

    public void setDefaultNumber(String defaultNumber) {
        if(defaultNumber != null && !defaultNumber.equals(""))
            this.defaultNumber = defaultNumber;
    }

    public String [] getDefaultList() {
        return defaultList;
    }

    public void setDefaultList(String [] defaultList) {
        if(defaultList != null)
            this.defaultList = defaultList;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void notifyDataSetChanged(){
        sPhoneAdapter.notifyDataSetChanged();
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {

    }


    @Override
    public void DeletePhoneElement(Boolean first,String id,String number) {

    }


    public Vector<HashMap<String,String>> getDefaultData()
    {
        Vector<HashMap<String,String>> data = new Vector<>(1 + defaultList.length);
        HashMap<String,String> numbers = new HashMap<>();
        numbers.put(PhoneDictionary.DATE, number);
        numbers.put(PhoneDictionary.NUMBER,defaultNumber);
        data.add(numbers);
        for(int i = 0 ; i < defaultList.length ; i++){
            HashMap<String,String> point = new HashMap<>();
            point.put(PhoneDictionary.DATE, defaultList[i]);
            data.add(point);
        }
        return data;
    }


    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderby) {
        mDecorate = new Decorate(accessory);
        phoneList.setProjection(projection);
        phoneList.setSelection(selection);
        phoneList.setArgument(argument);
        phoneList.setOrderby(orderby);
        phoneList.connectDataBase();
        number = phoneList.getPhoneList().get(0).get("mobilephone");
        number = number.replace(" ", "").replace("+86","").replace("+", "");
        mPhoneListElementList = getDefaultData();
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PhoneLocationThread.CheckLocation(mHandler,number,context);
            }
        });
        mThread.start();

    }

     class GetWeather extends AsyncTask<Void, Void, Void> {

        private String weatherInfo, location;

        public GetWeather (String loc) {
            location = loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String weatherURL = CommonSettingsAndFuncs.HostURL + String.format(CommonSettingsAndFuncs.GetWeatherURLFormat,
                    CommonSettingsAndFuncs.changeCharset(location, "UTF-8"));
            HttpConnectionUtil.getIt(weatherURL, new HttpConnectionUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.i("weather",response);
                    weatherInfo = response;
                }

                @Override
                public void onError(Exception e) {
                    weatherInfo = null;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(weatherInfo == null) {

            }
            try {
                weatherInfo = CommonSettingsAndFuncs.ParseWeatherXML(new ByteArrayInputStream(weatherInfo.getBytes()));
            } catch (XmlPullParserException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } catch (IOException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } finally {
               mPhoneListElementList.get(1).put(PhoneDictionary.DATE,weatherInfo);
                sPhoneAdapter.notifyDataSetChanged();
            }
        }
    }

}
