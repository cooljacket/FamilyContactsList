package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.OperationService;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.MainAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-4-1.
 */
public class MainShow extends PhoneShow {

    HashMap<String,Integer> nmapp = new HashMap<>(); //the map between number and position


    public MainShow(Context context, int table) {
        super(context,table);
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

    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderBy)
    {
        Vector<HashMap<String,String>> t = phoneList.init();
        int count = 0;
        if(t == null)
        {
            phoneList.setProjection(projection);
            phoneList.setSelection(selection);
            phoneList.setArgument(argument);
            phoneList.setOrderby(orderBy);
            phoneList.connectDataBase();
            mDecorate = new Decorate(accessory);
            mPhoneListElementList_backup = phoneList.getPhoneList();
            ElementCopy();
            sPhoneAdapter = new MainAdapter(context, table, mPhoneListElementList,index);
        }
        else
        {
            mPhoneListElementList_backup = t;
            mDecorate = new Decorate(accessory);
            ElementCopy();
            sPhoneAdapter = new MainAdapter(context,table,mPhoneListElementList,index);
        }
    }
}
