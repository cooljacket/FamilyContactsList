package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacket on 2016/3/30.
 */
public class BlackListHandler {
    private SQLiteOpenHelper mOpenHelper;

    public BlackListHandler(Context context) {
        mOpenHelper = BlackListDBHelper.getInstance(context);
    }

    // add to the black list
    public boolean add(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (!db.isOpen())
            return false;

        ContentValues values = new ContentValues();
        values.put(BlackListDBHelper.PN_COLUMN_NAME, phoneNumber);
        long result = db.insert(BlackListDBHelper.BL_TABLE_NAME, BlackListDBHelper.ID_COLUMN_NAME, values);
        db.close();
        return result != -1;
    }

    public boolean delete(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (!db.isOpen())
            return false;

        long result = db.delete(BlackListDBHelper.BL_TABLE_NAME, String.format("%s = ?", BlackListDBHelper.PN_COLUMN_NAME), new String[]{phoneNumber});
        db.close();
        return result != -1;
    }

    public boolean isInBlackList(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if (!db.isOpen())
            return false;

        Cursor cursor = db.query(BlackListDBHelper.BL_TABLE_NAME, null, String.format("%s = ?", BlackListDBHelper.PN_COLUMN_NAME), new String[]{phoneNumber}, null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public List<String> getTheBlackList() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        List<String> list = new ArrayList<>();
        if (!db.isOpen())
           return list;

        Cursor cursor = db.query(BlackListDBHelper.BL_TABLE_NAME, new String[]{BlackListDBHelper.PN_COLUMN_NAME}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(BlackListDBHelper.PN_COLUMN_NAME)));
        }
        db.close();
        return list;
    }
}
