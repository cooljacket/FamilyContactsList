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
    private String defaultList = "通话记录";
    private String defaultNumber = "电话";
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

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {
        refresh();
        sPhoneAdapter.notifyDataSetChanged();
    }


    @Override
    public void DeletePhoneElement(Boolean first,String id,String number) {
        refresh();
        sPhoneAdapter.notifyDataSetChanged();
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

    public boolean refresh()
    {
        phoneList.connectContentResolver();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        if(mPhoneListElementList_backup.size() == 0)
            return false;
        if(mPhoneListElementList_backup.size() <= bound) {
            status = PhoneDictionary.CONTENT1;
            HashMap<String,String> num = new HashMap<>();
            num.put(PhoneDictionary.DATE, number);
            num.put(PhoneDictionary.NUMBER,defaultNumber);
            mPhoneListElementList.removeAllElements();
            for (HashMap<String,String> i : mPhoneListElementList_backup)
                mPhoneListElementList.add(new HashMap<String, String>(i));
            mPhoneListElementList = mDecorate.decorate(mPhoneListElementList);
            mPhoneListElementList.insertElementAt(num, 0);

        }
        else {
            status = PhoneDictionary.CONTENT2;
            mPhoneListElementList.removeAllElements();
            mPhoneListElementList.addAll(getDefaultData());
        }
        return true;
    }




    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderby) {
        mDecorate = new Decorate(accessory);
        phoneList.setProjection(projection);
        phoneList.setSelection(selection);
        phoneList.setArgument(argument);
        phoneList.setOrderby(orderby);
        refresh();
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index,true);
    }

}
