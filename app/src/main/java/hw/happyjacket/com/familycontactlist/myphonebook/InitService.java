package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationDBHelper;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneStateReceiver;

/*
 * Created by root on 16-4-8.
*/

public class InitService extends Service {
    private PhoneLocationMaster PLMaster = new PhoneLocationMaster(this);
    private boolean errorOfGetingLocation = false;
    private int index;
    private IntentFilter intentFilter;
    private MyBinder mMyBinder = new MyBinder();
    private PhoneStateReceiver psReceiver;
    static public boolean isStart = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Operation.setContext(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
//        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CAL");
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        psReceiver = new PhoneStateReceiver();
        registerReceiver(psReceiver, intentFilter);
        isStart = true;
        Log.i("boom", "init service");
       /* Toast.makeText(this, "boom init service", Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onDestroy() {
        isStart = false;
        unregisterReceiver(psReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }


    public class MyBinder extends Binder{
        public void stop(){
            stopSelf();
        }
    }
}
