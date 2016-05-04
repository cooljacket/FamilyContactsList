package hw.happyjacket.com.familycontactlist.myphonebook.show;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.MainAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneLocationThread;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneThreadCheck;


/**
 * Created by root on 16-4-1.
 */
public class MainShow extends PhoneShow {


    public MainShow(Activity context, int table) {
        super(context, table);
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
    public void refresh(Accessory accerssory, String[] pro) {
        Integer t;
        for(int i = 0 ; i < mPhoneListElementList.size() ; i++){
            for (int j = 0; j < pro.length; j++) {
                if(nmapp != null && ((t = nmapp.get(mPhoneListElementList.get(i).get(PhoneDictionary.NUMBER))) != null))
                    mPhoneListElementList.get(i).put(pro[j], accerssory.decorate(pro[j], mPhoneListElementList_backup.get(t).get(pro[j])));
            }
        }
        sPhoneAdapter.notifyDataSetChanged();
    }

    @Override
    public void AddPhoneElement(Accessory accessory, HashMap<String, String> input) {
        HashMap<String,String> t;
        HashMap<String,String> l, v;
        int i = 0;
        boolean hasErase = false;
        String date = PhoneDictionary.DATE;
        String number = PhoneDictionary.NUMBER;
        String num = input.get(number);
        String dates = input.get(date);
        int count = nmapp.containsKey(num)? nmapp.size() : nmapp.size() + 1;
        String location [] = new String[3];
        phoneList.CallLogUpdate(dates, count);
        try {
            t = filter(input);
            location = new PhoneLocationMaster(context).get(num);
            t.put(PhoneDictionary.LOCATION, Operation.getLocation(num));
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
            for (int j = 0; j < mPhoneListElementList_backup.size(); j++) {
                nmapp.put(mPhoneListElementList_backup.get(j).get(number),j);
            }
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
        phoneList.delete(PhoneDictionary.ID + " = ? ", new String[]{id});
        phoneList.InsertTheNewestOne(DataBaseDictionary.CallLog_Projection, PhoneDictionary.PhoneAll, PhoneDictionary.NUMBER, number);
        phoneList.connectDataBase();
        mPhoneListElementList_backup = phoneList.getPhoneList();
        for(HashMap<String,String> i : mPhoneListElementList_backup)
            i.put(PhoneDictionary.LOCATION,Operation.getLocation(i.get(PhoneDictionary.NUMBER)));
        ElementCopy();
        sPhoneAdapter.notifyDataSetChanged();
    }

    @Override
    public void DeletePhoneElement(Boolean first,String id, String number) {
        if(first)
           DeleteTheNewestOne(id,number);
    }

    public void DeletePhoneElementAll(String number) {
        phoneList.deleteAll(PhoneDictionary.NUMBER + " = ? " ,new String[]{number});
        mPhoneListElementList.remove(nmapp.get(number));
        sPhoneAdapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(Vector<Integer> pos){
        if(pos == null || pos.size() == 0)
            return;
        mPhoneListElementList.removeAllElements();
        for(int i = 0 ; i < pos.size(); ++i){
                mPhoneListElementList.add(new HashMap<>(mPhoneListElementList_backup.get(pos.get(i))));
        }
        mDecorate.decorate(mPhoneListElementList);
        sPhoneAdapter.notifyDataSetChanged();
    }


    @Override
    public void InitAdapter(Accessory accessory, String[] projection, String selection, String[] argument, String orderBy)
    {
        Vector<HashMap<String, String>> t = phoneList.init();
        phoneList.setProjection(projection);
        phoneList.setSelection(selection);
        phoneList.setArgument(argument);
        phoneList.setOrderby(orderBy);
        if(t == null) {
            phoneList.connectDataBase();
            mPhoneListElementList_backup = phoneList.getPhoneList();
        }
        else {
            for(HashMap<String,String> i : t)
                mPhoneListElementList_backup.add(new HashMap<String, String>(i));
        }
        mDecorate = new Decorate(accessory);
        ElementCopy();
        for(int i = 0 ; i < mPhoneListElementList_backup.size() ; ++i){
            mPhoneListElementList_backup.get(i).put(PhoneDictionary.LOCATION,"");
            mPhoneListElementList.get(i).put(PhoneDictionary.LOCATION,"");
            Log.i(TAG,mPhoneListElementList.get(i).get(PhoneDictionary.NUMBER) + " " + mPhoneListElementList.get(i).get(PhoneDictionary.NAME));
        }

        sPhoneAdapter = new MainAdapter(context, table, mPhoneListElementList,index);

        final Vector<String> phoneNumberList = new Vector<>();
        for (HashMap<String,String> i : mPhoneListElementList)
           phoneNumberList.add(i.get(PhoneDictionary.NUMBER));

        new Thread(new Runnable() {
            @Override
            public void run() {
                PhoneLocationThread.setFlag(true);
                PhoneLocationThread.CheckLocation(handler,phoneNumberList,context);
            }
        }).start();

        new Thread(new PhoneThreadCheck(context,mPhoneListElementList,handler)).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            if (msg.what == -1) {
                sPhoneAdapter.notifyDataSetChanged();
            } else {
                /*Vector<HashMap<String, String>> t = (Vector<HashMap<String, String>>) msg.obj;
                for (HashMap<String, String> i : t) {
                    for (Map.Entry<String, String> j : i.entrySet()) {
                        Log.i(TAG, j.getKey() + " " + nmapp.get(j.getKey()));
                        int indexs = nmapp.get(j.getKey());
                        if ("".equals(j.getValue()) || j.getValue() == null) {
                            mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION, "");
                            mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, "");
                        } else {
                            mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                            mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                        }
                        ;
                    }
                }*/
                HashMap<String,String> t = (HashMap<String,String>) msg.obj;
                //for(Map.Entry<String,String> i : t.entrySet())
                for (Map.Entry<String, String> j : t.entrySet()) {
                    Log.i(TAG, j.getKey() + " " + nmapp.get(j.getKey()));
                    int indexs = nmapp.get(j.getKey());
                    if ("".equals(j.getValue()) || j.getValue() == null) {
                        mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION, "");
                        mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, "");
                    } else {
                        mPhoneListElementList.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                        mPhoneListElementList_backup.get(indexs).put(PhoneDictionary.LOCATION, j.getValue() + " ");
                    }
                    ;
                }
                sPhoneAdapter.notifyDataSetChanged();

            }

        }

    };
}
