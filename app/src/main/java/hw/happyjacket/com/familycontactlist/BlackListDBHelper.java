package hw.happyjacket.com.familycontactlist;

/**
 * Created by jacket on 2016/3/30.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class BlackListDBHelper extends SQLiteOpenHelper {

    private static SQLiteOpenHelper mInstance;

    private final static String name = "blackList.db";

    public static SQLiteOpenHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new BlackListDBHelper(context, name, null, 1);
        }
        return mInstance;
    }

    private BlackListDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blackList(id integer primary key autoincrement, phoneNumber text)");
//        db.execSQL("CREATE INDEX PN ON blackList(phoneNumber)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}