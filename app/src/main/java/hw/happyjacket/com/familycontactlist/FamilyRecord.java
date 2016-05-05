package hw.happyjacket.com.familycontactlist;

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
}