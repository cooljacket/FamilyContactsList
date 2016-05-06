package hw.happyjacket.com.familycontactlist;

/**
 * Created by jacket on 2016/5/6.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.List;

/**
 * Created by jacket on 2016/5/6.
 */
public class FMAlarmReceiver extends BroadcastReceiver {
    private static final int N_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        List<FamilyRecord> records = FamilyRecord.GetAllFamilies(context);
        if (records == null || records.size() == 0)
            return ;

        double sum = 0.0;
        for (FamilyRecord record : records) {
            sum += record.time / FamilyRecord.DAY;
        }

        int DayNoCommunicate = (int) sum / records.size();
        if (DayNoCommunicate > FamilyMoney.MOST_MONEY) {
            SendNotify(context, "你已平均" + DayNoCommunicate + "天没跟家里人联系了");
        }
    }

    public static void SendNotify(Context context, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("要记得多和家人联系哦")//设置通知栏标题
                .setContentText(content)
                .setContentIntent(getDefalutIntent(context)) //设置通知栏点击意图
                .setAutoCancel(true)
                .setTicker("亲情余额不足") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.family_icon);//设置通知小ICON

        manager.notify(N_ID, mBuilder.build());
    }


    public static PendingIntent getDefalutIntent(Context context) {
        PendingIntent pendingIntent= PendingIntent.getActivity(context, N_ID, new Intent(context, FamilyMoney.class), PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}