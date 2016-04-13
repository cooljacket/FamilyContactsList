package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PhoneAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneList;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by root on 16-3-27.
 */

//connect the data between listview and phonelist
public abstract class PhoneShow {

    final Context context;
    final int index;
    final static int CACHE_SIZE = 200;
    final static String TAG = PhoneShow.class.toString();
    protected int table;
    protected PhoneList phoneList;
    protected Vector<HashMap<String,String>> mPhoneListElementList = new Vector<>(CACHE_SIZE);
    protected Vector<HashMap<String,String>> mPhoneListElementList_backup = new Vector<>(CACHE_SIZE);
    protected PhoneAdapter sPhoneAdapter;
    protected Decorate mDecorate;

    public PhoneShow(Context context,int table){
        this.context = context;
        this.table = table;
        phoneList = new PhoneList(context);
        index = PhoneRegister.register(this);
        Log.i(TAG, index + "");
    }


    public  List<HashMap<String,String>> getPhoneListElementList() {
        return mPhoneListElementList;
    }

    public  void setPhoneListElementList(Vector<HashMap<String,String>> phoneListElementList) {
        mPhoneListElementList = phoneListElementList;
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

    public HashMap<String,String> filter(HashMap<String,String> input) {
        String content[] = phoneList.getProjection();
        HashMap<String,String> result = new HashMap<>();
        for (int i = 0; i < content.length; i++) {
            result.put(content[i],input.get(content[i]));
        }
        return result;
    }


    public void refresh(Accessory accerssory,String pro[]){
        for(int i = 0 ; i < mPhoneListElementList.size() ; i++){
            for (int j = 0; j < pro.length; j++) {
             mPhoneListElementList.get(i).put(pro[j], accerssory.decorate(pro[j], mPhoneListElementList_backup.get(i).get(pro[j])));
            }
        }
        sPhoneAdapter.notifyDataSetChanged();
    }

    public void copy() {

    }

    public void destroy()
    {
        phoneList.destroyPhoneList();
    }

    public abstract void AddPhoneElement(Accessory accessory, HashMap<String,String> input);

    public abstract void DeletePhoneElement(String id, String number);

    public abstract void InitAdapter(Accessory accessory,String projection[],String selection,String argument[],String orderby);

}