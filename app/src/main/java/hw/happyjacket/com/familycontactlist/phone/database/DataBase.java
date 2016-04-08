package hw.happyjacket.com.familycontactlist.phone.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 16-3-30.
 */
public class DataBase extends SQLiteOpenHelper{

    private static String CREATE[] = DataBaseDictionary.CREATE;
    private Context mContext;
    private String TAG = this.getClass().toString();

    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        for (int i = 0 ; i < CREATE.length ; i++)
        {
            db.execSQL(CREATE[i]);
        }
        Log.i(TAG,"Create successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
