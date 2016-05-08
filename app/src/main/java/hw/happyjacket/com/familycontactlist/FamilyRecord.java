package hw.happyjacket.com.familycontactlist;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.list.PhoneList;
import hw.happyjacket.com.familycontactlist.phone.list.RecordList;

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
        if(time < 0)
            return "无";
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
        RecordList recordList = new RecordList(context);
        Vector<User> families = dbHelper.getFamilies();
        String arg[] = new String[families.size()];
        for (int i = 0 ; i < families.size() ; ++i)
            arg[i] = families.get(i).mobilephone;
        HashMap<String,Long> result = recordList.getDate(arg);
        List<FamilyRecord> records = new ArrayList<>();
        Long tDate;

        Long now = Calendar.getInstance().getTimeInMillis();
        Date lastCall;
        for (User user : families) {
            if ((tDate = result.get(user.mobilephone)) != null)
                records.add(new FamilyRecord(user.name, (int)((now - tDate)/1000.0)));
            else
                records.add(new FamilyRecord(user.name, -1));
        }

        Collections.sort(records, new Comparator<FamilyRecord>() {
            @Override
            public int compare(FamilyRecord lhs, FamilyRecord rhs) {
                if (lhs.time == -1)
                    return -1;
                else if (rhs.time == -1)
                    return 1;
                return rhs.time - lhs.time;
            }
        });

        return records;
    }
}