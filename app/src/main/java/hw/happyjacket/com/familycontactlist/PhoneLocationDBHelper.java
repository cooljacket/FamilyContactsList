package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jacket on 2016/4/11.
 */
public class PhoneLocationDBHelper extends SQLiteOpenHelper {
    private static SQLiteOpenHelper mInstance;
    public final static String DB_FILE_NAME = "phoneLocation.db", TABLE_NAME = "phoneLocation";
    public final static String CREATE_LOCATION = "create table phoneLocation(\n" +
            "    phoneNumber text primary key,\n" +
            "    province text,\n" +
            "    city text,\n" +
            "    card_type text\n" +
            ");";

    public static SQLiteOpenHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new PhoneLocationDBHelper(context, DB_FILE_NAME, null, 1);
        }
        return mInstance;
    }

    private PhoneLocationDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
