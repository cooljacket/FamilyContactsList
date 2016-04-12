package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.CalendarView;
import android.widget.Toast;

/**
 * Created by leo on 2016/4/10.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final   String DB_NAME="contact";
    public final static int VERSION = 1;
    public static final   String DB_TABLENAME="user";
    public SQLiteDatabase db;
    private Context context;
    //    private MyDBHelper
    private static DBHelper instance = null;
//    private  SQLiteDatabase db;



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }
    public DBHelper(Context context,String name,int version){
        super(context, DB_NAME, null, VERSION);
    }
    public DBHelper(Context context){
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    public SQLiteDatabase openDatabase() {
        if(db == null){
            db = this.getWritableDatabase();
        }

        return db;
    }

    public void init(User user){
//        openDatabase();

        ContentValues values = new ContentValues();
        values.put("uid", user._id);
        try{
            db.insert("user", null, values);
        }catch (SQLiteConstraintException e){
//            e.getStackTrace();
            e.getStackTrace();
        }



    }

    public void change(User user){
//        openDatabase();
        ContentValues values = new ContentValues();
        int cid = user._id;
        values.put("photo",user.photo);
        values.put("family",user.family);
        values.put("groupname",user.group);
        values.put("familyName", user.familyName);
        db.update("user",values,"uid = "+cid,null);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer tableCreate = new StringBuffer();
        tableCreate.append("create table user ( uid integer primary key,")//contactid
//                .append("uid integer not null,")
                .append("family boolean default false,")//是不是家人，默认不是
                .append("photo integer default 0,")//头像编号
                .append("groupname text default 'UNKNOWN',")//群组，默认UNKNOWN
                .append("familyName text default 'NO')");//家人称呼
        db.execSQL(tableCreate.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//版本号不一样就重新创建
        String sql = "drop table if exists user";
        db.execSQL(sql);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}