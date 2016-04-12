package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.Context;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CallLogAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.HashMap;

/**
 * Created by root on 16-4-2.
 */
public class CallLogShow extends PhoneShow {

    public CallLogShow(Context context, int table) {
        super(context,table);
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {
        HashMap<String, String> t;
        HashMap<String, String> l,v;
        String date = PhoneDictionary.DATE;
        String type = PhoneDictionary.TYPE;
        try {
            t = filter(input);
            mPhoneListElementList_backup.insertElementAt(new HashMap<String, String>(t), 0);
            mPhoneListElementList.insertElementAt(t, 0);
            for (int i = 0; i < mPhoneListElementList.size(); i++) {
                l = mPhoneListElementList.get(i);
                v = mPhoneListElementList_backup.get(i);
                l.put(date, accessory.decorate(date, v.get(date)));
                l.put(type, accessory.decorate(type, v.get(type)));
            }
            sPhoneAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void DeletePhoneElement(String id, String number) {
        try {
            int count = 0;
            for (HashMap<String, String> i : mPhoneListElementList) {
                if (i.get(PhoneDictionary.ID).equals(id)) {
                    mPhoneListElementList.remove(count);
                    mPhoneListElementList_backup.remove(count);
                    break;
                }
                count++;
            }
            sPhoneAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderby) {
        phoneList.setProjection(projection);
        phoneList.setSelection(selection);
        phoneList.setArgument(argument);
        phoneList.setOrderby(orderby);
        phoneList.connectContentResolver();
        mDecorate = new Decorate(accessory);
        mPhoneListElementList.removeAllElements();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        for (HashMap<String,String> i : mPhoneListElementList_backup)
            mPhoneListElementList.add(new HashMap<String, String>(i));
        mPhoneListElementList = mDecorate.decorate(mPhoneListElementList);
        sPhoneAdapter = new CallLogAdapter(context, table, mPhoneListElementList,index);

    }


}
