package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;
import android.os.AsyncTask;
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

/**
 * Created by root on 16-4-13.
 */
public class ContactShow extends PhoneShow {


    private String number;
    private String [] defaultList = new String[]{"查找中...","编辑联系人","加入黑名单","从黑名单中移除","删除联系人"};
    private String defaultNumber = "电话";
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
        phoneList.connectContentResolver();
        number = phoneList.getPhoneList().get(1).get(ContactsContract.CommonDataKinds.Phone.NUMBER);
        number = number.replace(" ", "");
        mPhoneListElementList = getDefaultData();
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);
        PhoneLocationMaster phoneLocationMaster = new PhoneLocationMaster(context);
        String location [] = phoneLocationMaster.get(number);
//        String location [] = phoneLocationMaster.get("18819461579");
        Log.i("ccchehe-after", number);
        if(location != null)
            new GetWeather(location[1]).execute();
        else
            mPhoneListElementList.get(1).put(PhoneDictionary.DATE,"");
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
