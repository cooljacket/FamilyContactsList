package hw.happyjacket.com.familycontactlist.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by root on 16-3-30.
 */
public class Launch extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Launch", "This is the begining");

    }
}
