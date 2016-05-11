package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import hw.happyjacket.com.familycontactlist.MainActivity;
import hw.happyjacket.com.familycontactlist.PhoneCallUtis;
import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.list.RecordList;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

import java.util.HashMap;

/**
 * Created by root on 16-3-31.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    private static int status = 0;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("boom PhoneStateListener", status + "");
        String action = intent.getAction();
        if ("android.intent.action.PHONE_STATE".equals(action)) {
            String phoneNumber = intent.getStringExtra("incoming_number");
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    if(phoneNumber == null)
                        break;
                    else if(Operation.isBlackList(phoneNumber)) {
                        Operation.endCall(phoneNumber);
                        status = 2;
                    }
                    else
                        status = 1;
                    Log.i("hg", "电话状态……RINGING" + status);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    status = 1;
                    Log.i("hg", "电话状态……OFFHOOK" + status);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                   switch (status){
                       case 1:
                           HashMap<String,String> newGuy = new RecordList(context).addTheNewOne(DataBaseDictionary.CallLog_Projection, PhoneDictionary.PhoneAll,PhoneDictionary.NUMBER);
                           PhoneRegister.add(new XiaoMiAccessory(), newGuy);
                           status = 0;
                           break;
                       case 2:
                           Operation.deletePhoneTheNewestOne();
                           status = 0;
                           break;
                       default:
                           status = 0;
                           break;
                    }
                    Log.i("hg", "电话状态……IDLE" + status);
                    break;
            }

        }
    }


}
