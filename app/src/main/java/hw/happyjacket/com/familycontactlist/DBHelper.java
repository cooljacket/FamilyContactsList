package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Vector;

/**
 * Created by leo on 2016/4/10.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME="contact";
    public final static int VERSION = 1;
    public static final String DB_TABLENAME="user";
    public static final String GROUPTABLE = "grouptable";
    public static final String GROUPNAME = "groupname";
    public static final String GROUPID = "gid";
    public static final String GROUPNUM = "groupnum";
    public static final String ID = "uid";
    public static final String NAME = "name";
    public static final String SORTNAME = "sortname";
    public static final String NUMBER = "mobilephone";
    public static final String PHOTO = "photo";
    public static final String INFO = "info";
    public static final String NICKNAME = "nickname";

    public SQLiteDatabase db;
    private Context context;
    private static DBHelper instance = null;



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        openDatabase();
    }

    public DBHelper(Context context, String name, int version) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
        openDatabase();
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
        values.put("nickname", user.nickname);
        return values;
    }

    public boolean isInList(String phoneNumber) {
        Cursor cursor = db.query(DB_TABLENAME, null, "mobilephone = ?", new String[]{phoneNumber}, null, null, null, null);
        if (cursor == null)
            return false;
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }


    public Cursor getUser(String [] projection,String selection, String[] argument,String orderby){
        return db.query(DB_TABLENAME,projection,selection,argument,null,null,null,null);
    }

    public boolean insertAUser(User user) {//初始化数据库，新建联系人
        // 不允许多个重复的号码？吗
        if (isInList(user.mobilephone))
            return false;

        ContentValues values = User2Contents(user);
        boolean result = true;
        try{
            db.insert("user", null, values);
        }catch (SQLiteConstraintException e){
            e.getStackTrace();
            result = false;
        }

        return result;
    }

    public void changeUser(User user) {
        int uid = user.uid;
        ContentValues values = User2Contents(user);
        db.update("user", values, "uid = "+uid, null);
    }


    public void deleteUser(User user) {

        int uid = user.uid;
        db.delete("user", "uid = " + uid, null);
    }

    public void initGroup(Group group){//初始化数据库，或者新建组群
        Cursor t = db.query(GROUPTABLE, new String[]{GROUPNAME}, null, null, null, null, null, null);
        boolean flag=false;
        if(t.moveToFirst()){
            do {
                if(t.getString(0)==group.groupname){
                    flag = true;
                    break;
                }
            }while (t.moveToNext());
        }
        if(!flag){
            ContentValues values = new ContentValues();
            values.put("groupname",group.groupname);
            values.put("groupnum", group.groupnum);
            try{
                db.insert("grouptable", null, values);
            }catch (SQLiteConstraintException e){
                e.getStackTrace();
            }
        }

    }

    public void changeGroup(Group group) {
        ContentValues values = new ContentValues();
        String groupname = group.groupname;
        values.put("groupnum",group.groupnum);
        db.update("grouptable", values, "groupname = '" + groupname+"'", null);
    }

    public void deleteGroup(Group group) {
        ContentValues values = new ContentValues();
        String groupname = group.groupname;
        db.delete("grouptable", "groupname = '" + groupname+"'", null);
    }


    public Vector<String> getGroup() {
        Cursor t = db.query(GROUPTABLE, new String[]{GROUPNAME}, null, null, null, null, null, null);
        Vector<String> result = new Vector<>();
        if(t.moveToFirst()){
            do {
                result.add(t.getString(0));
            }while (t.moveToNext());
        }
        t.close();
        return result;
    }

    public int getGroupNum(Group group){
        Cursor t = db.query("grouptable",null,"groupname='"+group.groupname+"'",null,null,null,null);
        int num=0;
        if(t.moveToFirst()){
            num = t.getInt(1);
        }
        return num;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer tableCreate = new StringBuffer();
        tableCreate.append("create table user ( uid integer primary key autoincrement,")//userid0
                .append("cid integer not null,")//1contactid
                .append("name text,")//2
                .append("sortname text ,")//3
                .append("mobilephone text default '0',")//4
                .append("photo integer,")//头像编号5
                .append("groupname text default '无',")//群组，默认no6
                .append("info text,")//7
                .append("nickname text);");//8
        db.execSQL(tableCreate.toString());

        StringBuffer tableCreate2 = new StringBuffer();
        tableCreate2.append("create table grouptable ( ")//
                .append("groupname text primary key not null,")//0
                .append("groupnum int not null);");//1
        db.execSQL(tableCreate2.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(GROUPNAME,"家庭");
        contentValues.put(GROUPNUM,0);
        db.insert(GROUPTABLE,null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本号不一样就重新创建
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