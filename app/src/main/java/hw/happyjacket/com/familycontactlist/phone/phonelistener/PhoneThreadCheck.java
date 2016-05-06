package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.DBHelper;
import hw.happyjacket.com.familycontactlist.TabContactsFragment;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.list.RecordList;

/**
 * Created by root on 16-5-1.
 */
public class PhoneThreadCheck implements Runnable {


    Vector<HashMap<String,String> > data;
    DBHelper mDBHelper;
    RecordList mRecordList;
    Handler mHandler;

    public PhoneThreadCheck(Context context, Vector<HashMap<String, String>> data, Handler handler) {
        this.data = data;
        mDBHelper = new DBHelper(context);
        mRecordList = new RecordList(context);
        mHandler = handler;
    }

    @Override
    public void run() {

            Cursor t = null;
            String number = PhoneDictionary.NUMBER;
            String name = PhoneDictionary.NAME;
            String realName;
            String realNumber;
            String NULL = null;
            ContentValues contentValues = new ContentValues();
            synchronized (TabContactsFragment.DataBaseLock) {
            try {
                for (HashMap<String, String> i : data) {
                    realNumber = i.get(PhoneDictionary.NUMBER).replace(" ","").replace("+86","").replace("+","");
                    t = mDBHelper.getUser(new String[]{"name"}, "mobilephone = ?", new String[]{realNumber}, "limit 1");
                    if (t.moveToFirst()) {
                            realName = t.getString(0);
                            if(!realName.equals(i.get(name))) {
                                contentValues.clear();
                                contentValues.put(name, realName);
                                mRecordList.update(contentValues, number + " = ?", new String[]{realNumber});
                                i.put(name, realName);
                            }
                    }
                    else{
                        if(i.get(name) != null) {
                            i.remove(name);
                            contentValues.clear();
                            contentValues.put(PhoneDictionary.NAME,NULL);
                            mRecordList.update(contentValues,PhoneDictionary.NUMBER + " = ?",new String[]{realNumber});
                        }
                    }
                }
                Message message = mHandler.obtainMessage();
                message.what = -1;
                mHandler.sendMessage(message);
                t.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
