package hw.happyjacket.com.familycontactlist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FamilyMoney extends AppCompatActivity {
    public static final String FM_SF = "family_money_sf", FM_STATE_KEY = "fm_state";
    public static final double MOST_MONEY = 30.0;   // 暂定设置超过30天就提醒要多和家人联系

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_money);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Switch open_or_not = (Switch) findViewById(R.id.control_fm_func);
        open_or_not.setChecked(GetFMState(FamilyMoney.this) != null);
        open_or_not.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    StopAllAlarms(FamilyMoney.this);
                } else {
                    SetAllAlarms(FamilyMoney.this);
                }
            }
        });


        List<FamilyRecord> data = FamilyRecord.GetAllFamilies(FamilyMoney.this);
        FamilyRecordAdapter adapter = new FamilyRecordAdapter(FamilyMoney.this, R.layout.family_record_item, data);
        ListView fmList = (ListView) findViewById(R.id.families);
        fmList.setAdapter(adapter);
    }


    public static void SetAllAlarms(Context context) {
        SetAlarm(context, 9, "morning");
        SetAlarm(context, 21, "evening");
    }


    public static void SetAlarm(Context context, int hour, String action) {
        Set<String> alarms = GetFMState(context);
        if (alarms != null && alarms.contains(action))
            return ;

        Intent intent = new Intent(context, FMAlarmReceiver.class);
        intent.setAction(action);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 设置每天早上的9点和晚上的9点检测亲情余额
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

//        final int INTERVAL = 1000 * 3600 * 24;
        final int INTERVAL = 1000 * 6;

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL, sender);

        AddAlarm(context, action);
    }


    public static void StopAllAlarms(Context context) {
        if (GetFMState(context) == null)
            return ;

        Set<String> alarms = GetFMState(context);
        for (String alarm : alarms) {
            Intent intent = new Intent(context, FMAlarmReceiver.class);
            intent.setAction(alarm);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(sender);
            Log.d("hehe-remove", alarm);
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(FM_SF, MODE_PRIVATE).edit();
        editor.putStringSet(FM_STATE_KEY, null);
        editor.apply();
    }


    public static Set<String> GetFMState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(FM_SF, MODE_PRIVATE);
        return pref.getStringSet(FM_STATE_KEY, null);
    }


    public static void AddAlarm(Context context, String action) {
        SharedPreferences.Editor editor = context.getSharedPreferences(FM_SF, MODE_PRIVATE).edit();
        Set<String> alarms = GetFMState(context);
        if (alarms == null)
            alarms = new HashSet<>();
        alarms.add(action);
        editor.putStringSet(FM_STATE_KEY, alarms);
        editor.apply();

        Log.d("hehe-add", action + ", " + alarms.size());
    }
}