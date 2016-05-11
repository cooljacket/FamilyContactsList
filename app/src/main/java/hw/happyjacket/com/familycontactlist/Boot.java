package hw.happyjacket.com.familycontactlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import hw.happyjacket.com.familycontactlist.myphonebook.InitService;

/**
 * Created by root on 16-5-11.
 */
public class Boot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("boom","boom");
        Toast.makeText(context,"boom",Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(context, MainActivity.class);
        context.startActivity(intent1);
    }
}
