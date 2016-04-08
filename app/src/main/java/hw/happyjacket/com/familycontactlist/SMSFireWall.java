package hw.happyjacket.com.familycontactlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by jacket on 2016/3/31.
 */
public class SMSFireWall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BlackListHandler handler = new BlackListHandler(context);
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu:pdus){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdu);
            String body = smsMessage.getDisplayMessageBody();
            String fromWhom = smsMessage.getDisplayOriginatingAddress();

            Log.i("SMSFireWall",  body + ", " + fromWhom);
            if (handler.isInBlackList(fromWhom)) {
                abortBroadcast();
                Log.i("SMSFireWall-bad", body + ", " + fromWhom);
            }
            else
                Log.i("SMSFireWall-good", body + ", " + fromWhom);
        }
    }
}