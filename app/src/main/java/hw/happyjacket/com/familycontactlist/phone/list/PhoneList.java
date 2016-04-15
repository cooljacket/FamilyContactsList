package hw.happyjacket.com.familycontactlist.phone.list;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.phone.database.DataBase;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneThreadInsert;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-3-26.
 */

/*The basic class used to generate the database and communicate with the local Call log*/
public abstract class PhoneList {

    final Context context;
    protected Uri uri;
    protected int version;
    protected ContentResolver contentResolver;
    protected Cursor cursor;
    protected String db;
    protected String table;
    protected String projection[];
    protected String selection;
    protected String argument[];
    protected String orderby;
    protected String TAG = this.getClass().toString();


    public PhoneList(Context context) {
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
    }

    public PhoneList(Context context, String uri){
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
        this.uri = Uri.parse(uri);
    }

    public PhoneList(Context context, Uri uri){
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
        this.uri = uri;
    }

    public PhoneList(Context context, String uri, String db, int version){
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
        this.uri = Uri.parse(uri);
        this.db = db;
        this.version = version;
    }


    public PhoneList(Context context, String db, int version,String table,String[] projection, String selection, String[] argument,String orderby) {
        this.context = context;
        this.contentResolver = this.context.getContentResolver();
        this.projection = projection;
        this.selection = selection;
        this.argument = argument;
        this.db = db;
        this.version = version;
        this.table = table;
        this.orderby = orderby;
    }

    public PhoneList(Context context, String uri, String db, int version,String table, String[] projection, String selection, String[] argument, String orderby) {
        this.context = context;
        this.uri = Uri.parse(uri);
        this.db = db;
        this.version = version;
        this.table = table;
        this.projection = projection;
        this.selection = selection;
        this.argument = argument;
        this.orderby = orderby;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String[] getProjection() {
        return this.projection;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String[] getArgument() {
        return argument;
    }

    public void setArgument(String[] argument) {
        this.argument = argument;
    }

    public void setProjection(String[] projection) {
        this.projection = projection;

    }
    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }


    public void delete(String condriton,String [] argument){
        DataBase dataBase = null;
        SQLiteDatabase sqLiteDatabase = null;
        try{
            dataBase = new DataBase(context,db,null,version);
            sqLiteDatabase  = dataBase.getWritableDatabase();
            sqLiteDatabase.delete(table,condriton, argument);
            sqLiteDatabase.close();
            dataBase.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void deleteAll(String condition,String [] argument){
        DataBase dataBase = null;
        SQLiteDatabase sqLiteDatabase = null;
        try{
            dataBase = new DataBase(context,db,null,version);
            sqLiteDatabase  = dataBase.getWritableDatabase();
            sqLiteDatabase.delete(table,condition,argument);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            sqLiteDatabase.close();
            dataBase.close();
        }
    }


    public void connectContentResolver()
    {
        try {
            if(this.cursor != null)
                this.cursor.close();
            this.cursor = contentResolver.query(uri,projection,selection,argument,orderby);
        }
        catch (SecurityException e)
        {
            Log.i(TAG,"Read permission denied");
        }
    }


    public void connectDataBase()
    {
        DataBase dataBase = new DataBase(context,db,null,version);
        SQLiteDatabase sqLiteDatabase = dataBase.getReadableDatabase();
        try{
            if(this.cursor != null)
                this.cursor.close();
            this.cursor = sqLiteDatabase.query(table,projection,selection,argument,null,null,orderby,null);
        }
        catch (SecurityException e) {
            Log.i(TAG,"DataBase permission denied");
        }
    }


    public Vector<HashMap<String,String>> getPhoneList() {
        int id[] = new int[projection.length];
        String value;
        Vector<HashMap<String,String>> result = new Vector<HashMap<String, String>>();
        if (cursor.moveToFirst()) {
            for(int i = 0 ; i < projection.length ; i++)
                id[i] = cursor.getColumnIndex(projection[i]);
            do {
                HashMap<String,String> pair = new HashMap<String,String>();
                for (int i = 0 ; i < projection.length ;i++)
                {
                    value = cursor.getString(id[i]);
                    pair.put(projection[i], value);
                }
                result.add(pair);
            } while (cursor.moveToNext());
        }
     /*   Log.i(TAG,result.toString());*/
        return result;
    }



    public void destroyPhoneList()
    {
        if(cursor != null)
            cursor.close();
    }

    public abstract Vector<HashMap<String,String>> Refresh();

    public abstract Vector<HashMap<String,String>> init();

}










