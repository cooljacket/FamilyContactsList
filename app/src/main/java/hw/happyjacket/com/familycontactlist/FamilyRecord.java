package hw.happyjacket.com.familycontactlist;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by jacket on 2016/5/4.
 */
public class FamilyRecord {
    public String name;
    public int time;
    public static final int MINUTE = 60, HOUR = MINUTE * 60, DAY = HOUR * 24;

    public FamilyRecord(String name, int time) {
        this.name = name;
        this.time = time;
    }

    public String timeToStr() {
        String str = new String();
        if (time < MINUTE)
            return String.format("%d秒前", time);
        else if (time < HOUR)
            return String.format("%d分钟前", time / MINUTE);
        else if (time < DAY)
            return String.format("%d小时前", time / HOUR);
        else
            return String.format("%d天前", time / DAY);
    }

    public static List<FamilyRecord> GetAllFamilies(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        Vector<User> families = dbHelper.getFamilies();
        List<FamilyRecord> records = new ArrayList<>();

        for (User user : families) {
            records.add(new FamilyRecord(user.name, FamilyRecord.DAY * 40));
        }

        return records;
    }
}