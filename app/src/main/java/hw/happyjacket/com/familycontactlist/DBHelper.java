package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, int version) {
        super(context, DB_NAME, null, VERSION);
    }

    public DBHelper(Context context){
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        openDatabase();
    }

    public SQLiteDatabase openDatabase() {
        if(db == null){
            db = this.getWritableDatabase();
        }

        return db;
    }

    public ContentValues User2Contents(User user) {
        ContentValues values = new ContentValues();
        values.put("cid", user.cid);
        values.put("name", user.name);
        values.put("sortname", user.sortname);
        values.put("mobilephone", user.mobilephone.replace(" ", ""));
        values.put("photo", user.photo);
        values.put("groupname", user.groupname);
        values.put("info", user.info);

        return values;
    }

    public boolean isInList(String phoneNumber) {
        Log.d("hehe-look", phoneNumber);
        Cursor cursor = db.query(DB_TABLENAME, null, "mobilephone=?", new String[]{phoneNumber}, null, null, null, null);
        if (cursor == null)
            return false;
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    public void insertFromStrings(String[] data) {
        if (isInList(data[2])) {
            Log.d("hehehihi", "inlist " + data[2]);
            return ;
        }

        // 0: name, 1: sortname, 2: mobilephone, 3: photo, 4: groupname, 5: info
        User user = new User();
        user.name = data[0];
        user.sortname = data[1];
        user.mobilephone = data[2];
        user.photo = Integer.parseInt(data[3]);
        user.groupname = data[4];
        user.info = data[5];
        insertAUser(user);
    }

    public void insertAUser(User user) {//初始化数据库，新建联系人
        ContentValues values = User2Contents(user);
        try{
            db.insert("user", null, values);
        }catch (SQLiteConstraintException e){
            e.getStackTrace();
        }
    }

    public void changeUser(User user){
        int uid = user.uid;
        ContentValues values = User2Contents(user);
        db.update("user", values, "uid = "+uid, null);
    }

    public void deleteUser(User user){
        ContentValues values = new ContentValues();
        int uid = user.uid;
        db.delete("user", "uid = "+uid, null);
    }

    public void initGroup(Group group){//初始化数据库，或者新建组群
//        openDatabase();

        ContentValues values = new ContentValues();
        values.put("groupname",group.groupname);
        values.put("groupnum",group.groupnum);
        try{
            db.insert("grouptable", null, values);
        }catch (SQLiteConstraintException e){
            e.getStackTrace();
        }
    }

    public void changeGroup(Group group){
        ContentValues values = new ContentValues();
        String groupname = group.groupname;
        values.put("groupnum",group.groupnum);
        db.update("grouptable",values,"groupname = "+groupname,null);
    }

    public void deleteGroup(Group group){
        ContentValues values = new ContentValues();
        String groupname = group.groupname;
        db.delete("grouptable","groupname = "+groupname,null);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer tableCreate = new StringBuffer();
        tableCreate.append("create table user ( uid integer primary key autoincrement,")//contactid0
                .append("cid integer default 0,")//1
                .append("name text,")//2
                .append("sortname text ,")//3
                .append("mobilephone text default '0',")//4
                .append("photo integer,")//头像编号5
                .append("groupname text default 'NO',")//群组，默认no6
                .append("info text);");//7
        db.execSQL(tableCreate.toString());

        StringBuffer tableCreate2 = new StringBuffer();
        tableCreate2.append("create table grouptable ( gid integer primary key autoincrement,")//contactid
                .append("groupname text not null,")
                .append("groupnum int not null);");
        db.execSQL(tableCreate2.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//版本号不一样就重新创建
        String sql = "drop table if exists user";
        db.execSQL(sql);

        String sql1 = "drop table if exists grouptable";
        db.execSQL(sql1);
        onCreate(db);
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}