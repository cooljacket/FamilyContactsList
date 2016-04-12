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
    public final static String DB_FILE_NAME = "blackList.db", BL_TABLE_NAME = "blackList", ID_COLUMN_NAME = "id", PN_COLUMN_NAME = "phoneNumber";
    public final static String CREATE_BLACKLIST = String.format("create table %s(%s text primary key)", BL_TABLE_NAME, PN_COLUMN_NAME);

    public static SQLiteOpenHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new BlackListDBHelper(context, DB_FILE_NAME, null, 1);
        }
        return mInstance;
    }

    private BlackListDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLACKLIST);
//        db.execSQL(String.format("CREATE INDEX PN ON %s(%s)", DB_FILE_NAME, PN_COLUMN_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}