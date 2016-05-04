package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PhoneAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.list.PhoneList;
import hw.happyjacket.com.familycontactlist.phone.list.RecordList;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneLocationThread;

/**
 * Created by root on 16-3-27.
 */

//connect the data between listview and phonelist
public abstract class PhoneShow {

    final Activity context;
    final int index;
    final static int CACHE_SIZE = 200;
    final static String TAG = PhoneShow.class.toString();
    protected int table;
    protected RecordList phoneList;
    protected Vector<HashMap<String,String>> mPhoneListElementList = new Vector<>(CACHE_SIZE);
    protected Vector<HashMap<String,String>> mPhoneListElementList_backup = new Vector<>(CACHE_SIZE);
    protected PhoneAdapter sPhoneAdapter;
    protected Decorate mDecorate;
    protected HashMap<String,Integer> nmapp = new HashMap<>(); //the map between number and position

    public PhoneShow(Activity context,int table){
        this.context = context;
        this.table = table;
        phoneList = new RecordList(context);
        index = PhoneRegister.register(this);
    }


    public  List<HashMap<String,String>> getPhoneListElementList() {
        return mPhoneListElementList;
    }

    public  void setPhoneListElementList(Vector<HashMap<String,String>> phoneListElementList) {
        mPhoneListElementList = phoneListElementList;
    }

    public Vector<HashMap<String, String>> getPhoneListElementList_backup() {
        return mPhoneListElementList_backup;
    }

    public void setPhoneListElementList_backup(Vector<HashMap<String, String>> phoneListElementList_backup) {
        mPhoneListElementList_backup = phoneListElementList_backup;
    }

    public  PhoneAdapter getPhoneAdapter() {
        return sPhoneAdapter;
    }

    public  void setPhoneAdapter(PhoneAdapter phoneAdapter) {
        sPhoneAdapter = phoneAdapter;
    }

    public int getIndex() {
        return index;
    }

    public PhoneList getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(RecordList phoneList) {
        this.phoneList = phoneList;
    }

    public HashMap<String, Integer> getNmapp() {
        return nmapp;
    }

    public void setNmapp(HashMap<String, Integer> nmapp) {
        this.nmapp = nmapp;
    }

    public HashMap<String,String> filter(HashMap<String,String> input) {
        String content[] = phoneList.getProjection();
        if(content == null || content.length == 0)
            return null;
        HashMap<String,String> result = new HashMap<>();
        for (int i = 0; i < content.length; i++) {
            result.put(content[i],input.get(content[i]));
        }
        return result;
    }


    public void refresh(Accessory accerssory,String pro[]){
        Integer t;
        for(int i = 0 ; i < mPhoneListElementList.size() ; i++){
            for (int j = 0; j < pro.length; j++) {
                if(nmapp != null && ((t = nmapp.get(mPhoneListElementList.get(i).get(PhoneDictionary.NUMBER))) != null))
                    mPhoneListElementList.get(i).put(pro[j], accerssory.decorate(pro[j], mPhoneListElementList_backup.get(t).get(pro[j])));
                else
                    mPhoneListElementList.get(i).put(pro[j], accerssory.decorate(pro[j], mPhoneListElementList_backup.get(j).get(pro[j])));
            }
        }
        sPhoneAdapter.notifyDataSetChanged();
    }

    public void copy() {}

    public void destroy()
    {
        phoneList.destroyPhoneList();
        PhoneLocationThread.setFlag(false);
    }

    public abstract void AddPhoneElement(Accessory accessory, HashMap<String,String> input);

    public abstract void DeletePhoneElement(Boolean first,String id, String number);

    public abstract void InitAdapter(Accessory accessory,String projection[],String selection,String argument[],String orderby);

}
