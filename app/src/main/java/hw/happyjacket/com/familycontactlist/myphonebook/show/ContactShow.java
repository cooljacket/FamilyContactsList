package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.BlackListMaster;
import hw.happyjacket.com.familycontactlist.DBHelper;
import hw.happyjacket.com.familycontactlist.CommonUtils;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CallLogAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneLocationThread;

/**
 * Created by root on 16-4-13.
 */
public class ContactShow extends PhoneShow {


    private String number;
    private String name;
    private String [] defaultList = new String[]{"查找中...","编辑联系人","加入黑名单","删除联系人"};
    private String [] BlackOption = new String[]{"加入黑名单","从黑名单中移除"};
    private String defaultNumber = "电话";
    private String location;
    private boolean inBlackList;

    private Thread mThread;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if(msg.obj == null || "".equals((location = (String)msg.obj))) {
                        mPhoneListElementList.get(1).put(PhoneDictionary.WEATHERINFO, "无信息");
                        sPhoneAdapter.notifyDataSetChanged();
                        break;
                    }
                     location = (String) msg.obj;
                     Log.i("getWeather", location);
                     new GetWeather(location).execute();
                    break;
                default:
                    break;
            }
        }
    };

    public ContactShow(Activity context, int table) {
        super(context, table);
    }

    public ContactShow(Activity context, int table, String name) {
        super(context, table);
        this.name = name;
    }

    public ContactShow(Activity context, int table, String name, String number) {
        super(context, table);
        this.name = name;
        this.number = number;
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

    public boolean isInBlackList() {
        return inBlackList;
    }

    public void setInBlackList(boolean inBlackList) {
        this.inBlackList = inBlackList;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void notifyDataSetChanged(){
        if(inBlackList)
            defaultList[2] = BlackOption[1];
        else
            defaultList[2] = BlackOption[0];
        mPhoneListElementList.get(3).put(PhoneDictionary.DATE,defaultList[2]);
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
        String t;
        if(inBlackList)
            defaultList[2] = BlackOption[1];
        else
            defaultList[2] = BlackOption[0];
        Vector<HashMap<String,String>> data = new Vector<>(1 + defaultList.length);
        HashMap<String,String> numbers = new HashMap<>();
        numbers.put(PhoneDictionary.DATE, number);

        if ((t = Operation.getLocation(number))!= null && t.length() > 0)
            numbers.put(PhoneDictionary.NUMBER, t);
        else
            numbers.put(PhoneDictionary.NUMBER, defaultNumber);

        data.add(numbers);

        HashMap<String,String> tt = new HashMap<>();
        tt.put (PhoneDictionary.WEATHERINFO,defaultList[0]);
        data.add(tt);
        for(int i = 1 ; i < defaultList.length ; i++){
            HashMap<String,String> point = new HashMap<>();
            point.put(PhoneDictionary.DATE, defaultList[i]);
            data.add(point);
        }
        return data;
    }


    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderby) {
        mDecorate = new Decorate(accessory);
        inBlackList = Operation.isBlackList(number);
        Log.d("lalala-find", number + ", " + inBlackList);
        mPhoneListElementList = getDefaultData();
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index,1,number);
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PhoneLocationThread.CheckLocation(mHandler, number, context);
            }
        });
        mThread.start();

    }

     class GetWeather extends AsyncTask<Void, Void, Void> {
         private String weatherInfo, weather, location;
         public GetWeather (String loc) {
            location = loc;
        }

         @Override
         protected void onPreExecute() {
             Log.d("hehehe-start", location);
         }

         @Override
         protected Void doInBackground(Void... params) {
            String weatherURL = CommonUtils.W_Host + String.format(CommonUtils.GetWeatherURLFormat,
                    CommonUtils.changeCharset(location, "UTF-8"));
            Log.d("hehehe", weatherURL + ", " + location);
            HttpConnectionUtil.getIt(weatherURL, null, new HttpConnectionUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response, String arg) {
                    Log.i("weather", response);
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
            ((CallLogAdapter)sPhoneAdapter).setMessage("");
            if(weatherInfo == null)
                return;
            try {
                String[] result = CommonUtils.ParseWeatherXML(new ByteArrayInputStream(weatherInfo.getBytes()));
                if (result != null) {
                    weatherInfo =  result[2] + "\n" + result[3].replace("/","~");
                    weather = result[result.length - 1];
                    ((CallLogAdapter) sPhoneAdapter).setMessage(String.format(result[0], name));
                }
                else {
                    weatherInfo = "无信息";
                }
            } catch (XmlPullParserException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } catch (IOException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } finally {
                ((CallLogAdapter) sPhoneAdapter).setWeather(1);
                mPhoneListElementList.get(1).put(PhoneDictionary.DATE,"");
                mPhoneListElementList.get(1).put(PhoneDictionary.WEATHERINFO, weatherInfo);
                mPhoneListElementList.get(1).put(PhoneDictionary.WEATHER, weather);
                sPhoneAdapter.notifyDataSetChanged();

            }
        }
    }

}
