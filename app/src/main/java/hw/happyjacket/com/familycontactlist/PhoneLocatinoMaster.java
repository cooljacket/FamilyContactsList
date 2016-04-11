package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jacket on 2016/4/11.
 */
public class PhoneLocatinoMaster {
    private SQLiteOpenHelper mOpenHelper;

    public PhoneLocatinoMaster(Context context) {
        mOpenHelper = PhoneLocationDBHelper.getInstance(context);
    }

    // a string like "18819461579：广东 广州 广东移动全球通卡"
    public boolean add(String data) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (!db.isOpen())
            return false;

        String[] locations = data.split(" ");
        int divider = locations[0].indexOf("：");
        ContentValues values = new ContentValues();
        values.put("phoneNumber", locations[0].substring(0, divider));
        values.put("province", locations[0].substring(divider + 1));
        values.put("city", locations[1]);
        values.put("card_type", locations[2]);

        long result = db.insert(PhoneLocationDBHelper.TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public boolean isCached(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if (!db.isOpen())
            return false;

        Cursor cursor = db.query(PhoneLocationDBHelper.TABLE_NAME, new String[]{"phoneNumber"}, "phoneNumber=?", new String[]{phoneNumber}, null, null, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public String[] get(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if (!db.isOpen())
            return null;

        Cursor cursor = db.query(PhoneLocationDBHelper.TABLE_NAME, null, "phoneNumber=?", new String[]{phoneNumber}, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        String province = cursor.getString(cursor.getColumnIndex("province"));
        String city = cursor.getString(cursor.getColumnIndex("city"));
        String card_type = cursor.getString(cursor.getColumnIndex("card_type"));
        return new String[]{province, city, card_type};
    }
}