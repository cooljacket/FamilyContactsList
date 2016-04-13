package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CallLogAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-13.
 */
public class ContactShow extends PhoneShow {


    private String number;
    private String defaultList = "详细信息";
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

    public String getDefaultList() {
        return defaultList;
    }

    public void setDefaultList(String defaultList) {
        if(defaultList != null && !defaultList.equals(""))
            this.defaultList = defaultList;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {

    }


    @Override
    public void DeletePhoneElement(String id,String number) {

    }


    public Vector<HashMap<String,String>> getDefaultData()
    {
        Vector<HashMap<String,String>> data = new Vector<>(2);
        HashMap<String,String> numbers = new HashMap<>();
        numbers.put(PhoneDictionary.DATE, number);
        numbers.put(PhoneDictionary.NUMBER,defaultNumber);
        data.add(numbers);
        HashMap<String,String> point = new HashMap<>();
        point.put(PhoneDictionary.DATE, defaultList);
        data.add(point);
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
        mPhoneListElementList = getDefaultData();
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);
    }

}
