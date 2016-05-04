package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.database.DataBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by root on 16-4-4.
 */


public class PhoneThreadInsert implements Runnable {

    private Vector<HashMap<String,String>> data;
    private String table;
    private int version;
    private Context context;
    private String db;

    public PhoneThreadInsert(Vector<HashMap<String, String>> data, String table, int version, Context context, String db) {
        this.data = data;
        this.table = table;
        this.version = version;
        this.context = context;
        this.db = db;
    }

    @Override
    public void run() {
        DataBase dataBase = new DataBase(context,db,null,version);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            sqLiteDatabase.delete(table,null,null);
            for(HashMap<String,String> i : data) {
                contentValues.clear();
                for(Map.Entry<String,String> j : i.entrySet())
                    contentValues.put(j.getKey(), j.getValue());
                sqLiteDatabase.insert(table, null, contentValues);
            }
            sqLiteDatabase.close();
            dataBase.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
