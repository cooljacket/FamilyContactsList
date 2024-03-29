package hw.happyjacket.com.familycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jacket on 2016/4/11.
 */
public class PhoneLocationMaster {
    private SQLiteOpenHelper mOpenHelper;

    public PhoneLocationMaster(Context context) {
        mOpenHelper = PhoneLocationDBHelper.getInstance(context);
    }

    // data is a string like "18819461579：广东 广州 广东移动全球通卡"
    public boolean add(String phoneNumber, String data, int state) {
        if (state == PhoneLocationDBHelper.ERROR)
            return false;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (!db.isOpen())
            return false;

        ContentValues values = addHelper(phoneNumber, data, state);
        db.update(PhoneLocationDBHelper.TABLE_NAME, values, "phoneNumber=?", new String[]{phoneNumber});
        long result = db.insert(PhoneLocationDBHelper.TABLE_NAME, null, values);

        return result != -1;
    }

    public ContentValues addHelper(String phoneNumber, String data, int state) {
        String province = "", city = "", card_type = "";

        if (data != null) {
            String[] locations = data.split(" ");
            if (locations.length < 3)
                return null;
            int divider = locations[0].indexOf("：");
            province = locations[0].substring(divider + 1);
            city = locations[1];
            card_type = locations[2];
        }

        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        values.put("province", province);
        values.put("city", city);
        values.put("card_type", card_type);
        values.put("state", state);

        return values;
    }

    public String[] get(String phoneNumber) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        if (!db.isOpen())
            return null;

        Cursor cursor = db.query(PhoneLocationDBHelper.TABLE_NAME, null, "phoneNumber=?", new String[]{phoneNumber}, null, null, null);
        if (!cursor.moveToFirst())
            return null;

        // 如果上次出错了，那么就重新获取一遍
        if (cursor.getInt(cursor.getColumnIndex("state")) == PhoneLocationDBHelper.ERROR)
            return null;

        String province = cursor.getString(cursor.getColumnIndex("province"));
        String city = cursor.getString(cursor.getColumnIndex("city"));
        String card_type = cursor.getString(cursor.getColumnIndex("card_type"));
        cursor.close();
        return new String[]{province, city, card_type};
    }
}