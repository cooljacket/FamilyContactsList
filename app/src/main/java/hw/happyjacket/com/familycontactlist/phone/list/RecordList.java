package hw.happyjacket.com.familycontactlist.phone.list;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.database.DataBase;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneThreadInsert;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-3-26.
 */

/*The basic class used to generate the database and communicate with the local Call log*/
public class RecordList extends PhoneList {


    private String orderby = CallLog.Calls.DEFAULT_SORT_ORDER;
    private String TAG = this.getClass().toString();


    public RecordList(Context context) {
        super(context);
        this.projection = new String[]{PhoneDictionary.ID,PhoneDictionary.NUMBER,PhoneDictionary.NAME,PhoneDictionary.DATE};
        this.selection = null;
        this.argument = null;
        this.db = PhoneDictionary.DEFAULT_DB;
        this.table = PhoneDictionary.DEFAULT_TABLE;
        this.uri = CallLog.Calls.CONTENT_URI;
        this.version = DataBaseDictionary.databaseVersion;
    }

    public RecordList(Context context, String projection[])
    {
        super(context);
        this.projection = projection;
        this.selection = null;
        this.argument = null;
        this.db = PhoneDictionary.DEFAULT_DB;
        this.table = PhoneDictionary.DEFAULT_TABLE;
        this.uri = CallLog.Calls.CONTENT_URI;
        this.version = DataBaseDictionary.databaseVersion;
    }

    public RecordList(Context context, String[] projection, String selection, String[] argument) {
        super(context);
        this.projection = projection;
        this.selection = selection;
        this.argument = argument;
        this.db = PhoneDictionary.DEFAULT_DB;
        this.table = PhoneDictionary.DEFAULT_TABLE;
        this.uri = CallLog.Calls.CONTENT_URI;
        this.version = DataBaseDictionary.databaseVersion;
    }

    public RecordList(Context context, String db, String table, String[] projection, String[] argument, String selection, String orderby) {
        super(context,db,DataBaseDictionary.databaseVersion,table,projection,selection,argument,orderby);
        this.uri = CallLog.Calls.CONTENT_URI;
    }



    public RecordList(Context context, Uri uri, String db, String table, String[] projection, String selection, String[] argument, String orderby) {
        super(context,uri.toString(),db,DataBaseDictionary.databaseVersion,table,projection,selection,argument,orderby);
    }


    private boolean CallLogChanged()
    {
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        int size1,size2;
        String date1,date2;
        boolean changed = true;
        Cursor p = null;
        try{
            pref = context.getSharedPreferences(PhoneDictionary.SHARE_PREFERANCE_NAME, Context.MODE_PRIVATE);
            editor = pref.edit();
            size1 = pref.getInt("count", -1);
            size2 = -2;
            date1 = pref.getString("date", "");
            date2 = "-1";
            p = contentResolver.query(uri, new String[]{PhoneDictionary.DATE}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (p.moveToFirst()) {
                size2 = p.getCount();
                date2 = p.getString(0);
                if (size1 == size2 && date1.equals(date2)) {
                    changed = false;
                }
            }
            editor.putString("date", date2);
            editor.putInt("count", size2);
            editor.commit();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally {
            p.close();
        }
        return changed;

    }

    @Override
    public Vector<HashMap<String,String>> Refresh()
    {

        final int size = 50;
        int number_id;
        String pro[] = DataBaseDictionary.CallLog_Projection;
        HashMap<String,Boolean> container = new HashMap<>(size);
        HashMap<String,String> tmp;
        Vector<HashMap<String,String>> result = new Vector<>(size);
        Cursor t = null;
        try {
            t = contentResolver.query(uri, pro, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if(t.moveToFirst()) {
                number_id = t.getColumnIndex(PhoneDictionary.NUMBER);
                do{
                    if(container.containsKey(t.getString(number_id)))
                        continue;
                    container.put(t.getString(number_id), (Boolean.TRUE));
                    tmp = new HashMap<>();
                    for (int i = 0 ; i < pro.length ; i++) {
                        tmp.put(pro[i],t.getString(i));
                    }
                    result.add(tmp);
                } while (t.moveToNext());
            }
            t.close();
        }
        catch (SecurityException e) {
            Log.i(TAG, "Read permission denied");
        }
        return result;
    }

    @Override
    public Vector<HashMap<String,String>> init()
    {
        Vector<HashMap<String, String>> t = null;
        try {

            if (!CallLogChanged())
                return null;
            t = Refresh();
            PhoneThreadInsert phoneThreadInsert = new PhoneThreadInsert(t, DataBaseDictionary.CallLog, DataBaseDictionary.databaseVersion, context, DataBaseDictionary.CALLLOG);
            new Thread(phoneThreadInsert).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return t;
    }




    public HashMap<String,String> addTheNewOne(String pro[], String all[],String number)
    {
        int version = DataBaseDictionary.databaseVersion;
        int id;
        DataBase dataBase = null;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues;
        HashMap<String,String> result = new HashMap<>(1);
        Cursor t = null;
        try {
            dataBase =  new DataBase(context,db,null,version);
            sqLiteDatabase  = dataBase.getWritableDatabase();
            t = contentResolver.query(uri, all, null, null, CallLog.Calls.DEFAULT_SORT_ORDER + " limit 1");
            if(t.moveToFirst())
            {
                contentValues = new ContentValues();
                for (int i = 0 ; i < pro.length ; i++) {
                    id = t.getColumnIndex(pro[i]);
                    contentValues.put(pro[i],t.getString(id));
                }
                for (int i = 0; i < all.length; i++) {
                    result.put(all[i],t.getString(i));
                }
                sqLiteDatabase.update(table,contentValues,number + " = ? ",new String[]{contentValues.getAsString(number)});
                sqLiteDatabase.insert(table, null, contentValues);
            }
            t.close();
            sqLiteDatabase.close();
            dataBase.close();
        }
        catch (SecurityException e)
        {
            Log.i(TAG, "Read permission denied");
        }
        return result;
    }

    public HashMap<String,String> findTheNewestOne(String pro[], String all[],String number, String num)
    {
        int version = DataBaseDictionary.databaseVersion;
        int id;
        DataBase dataBase = null;
        SQLiteDatabase sqLiteDatabase = null;
        ContentValues contentValues;
        HashMap<String,String> result = new HashMap<>(1);
        Cursor t = null;
        try {
            dataBase =  new DataBase(context,db,null,version);
            sqLiteDatabase  = dataBase.getWritableDatabase();
            t = contentResolver.query(uri, all, number + " = ? ", new String[]{num}, CallLog.Calls.DEFAULT_SORT_ORDER + " limit 1 ");
            if(t.moveToFirst()) {
                contentValues = new ContentValues();
                for (int i = 0 ; i < pro.length ; i++) {
                    id = t.getColumnIndex(pro[i]);
                    contentValues.put(pro[i],t.getString(id));
                }
                for (int i = 0; i < all.length; i++) {
                    result.put(all[i], t.getString(i));
                }
                sqLiteDatabase.update(table,contentValues,number + " = ? ",new String[]{num});
                sqLiteDatabase.insert(table, null, contentValues);
            }
            t.close();
            sqLiteDatabase.close();
            dataBase.close();
        }
        catch (SecurityException e)
        {
            Log.i(TAG, "Read permission denied");
        }
        return result;
    }




}









