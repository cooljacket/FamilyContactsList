package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneList;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

import java.util.HashMap;

/**
 * Created by root on 16-3-31.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static boolean status = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("PhoneStateListener",status + "");
        String action = intent.getAction();
        if ("android.intent.action.PHONE_STATE".equals(action)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    status = true;
                    Log.i("hg", "电话状态……RINGING" + status);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    status = true;
                    Log.i("hg", "电话状态……OFFHOOK" + status);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if(status) {
                        HashMap<String,String> newGuy = new PhoneList(context).addTheNewOne(DataBaseDictionary.CallLog_Projection, PhoneDictionary.PhoneAll,PhoneDictionary.NUMBER);
                        PhoneRegister.add(new XiaoMiAccessory(), newGuy);
                        status = false;
                    }
                    Log.i("hg", "电话状态……IDLE" + status);
                    break;
            }

        }
    }


}
