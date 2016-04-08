package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CallLogAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-4-2.
 */
public class ContentShow extends PhoneShow {

    private int status;
    private String number;
    final int bound = 5;
    final String defaultList = "通话记录";
    public ContentShow(Context context, int table) {
        super(context, table);
    }

    public ContentShow(Context context, int table, String number) {
        super(context, table);
        this.number = number;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {
        phoneList.connectContentResolver();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        if(mPhoneListElementList_backup.size() <= bound)
        {
            Decorate decorate = new Decorate(accessory);
            HashMap<String,String> num = new HashMap<>();
            num.put(PhoneDictionary.DATE, number);
            mPhoneListElementList.removeAllElements();
            for (HashMap<String,String> i : mPhoneListElementList_backup)
                mPhoneListElementList.add(new HashMap<String, String>(i));
            mPhoneListElementList = decorate.decorate(mPhoneListElementList);
            mPhoneListElementList.insertElementAt(num, 0);
            Log.i(TAG, mPhoneListElementList.toString());
            sPhoneAdapter.notifyDataSetChanged();
        }
        else
        {
            mPhoneListElementList.removeAllElements();
            mPhoneListElementList.addAll(getDefaultData());
            sPhoneAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void DeletePhoneElement(String id,String number) {

    }


    public Vector<HashMap<String,String>> getDefaultData()
    {
        Vector<HashMap<String,String>> data = new Vector<>(2);
        HashMap<String,String> numbers = new HashMap<>();
        numbers.put(PhoneDictionary.DATE, number);
        data.add(numbers);
        HashMap<String,String> point = new HashMap<>();
        point.put(PhoneDictionary.DATE, defaultList);
        data.add(point);
        return data;
    }




    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderby) {
        phoneList.setProjection(projection);
        phoneList.setSelection(selection);
        phoneList.setArgument(argument);
        phoneList.setOrderby(orderby);
        phoneList.connectContentResolver();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        if(mPhoneListElementList_backup.size() <= bound)
        {
            status = PhoneDictionary.CONTENT1;
            Decorate decorate = new Decorate(accessory);
            HashMap<String,String> num = new HashMap<>();
            num.put(PhoneDictionary.DATE, number);
            mPhoneListElementList.removeAllElements();
            for (HashMap<String,String> i : mPhoneListElementList_backup)
                mPhoneListElementList.add(new HashMap<String, String>(i));
            mPhoneListElementList = decorate.decorate(mPhoneListElementList);
            mPhoneListElementList.insertElementAt(num, 0);
            Log.i(TAG, mPhoneListElementList.toString());
            sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);
        }
        else
        {
            status = PhoneDictionary.CONTENT2;
            mPhoneListElementList = getDefaultData();
            sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);
        }

    }

}
