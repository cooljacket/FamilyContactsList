package hw.happyjacket.com.familycontactlist.phone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

/**
 * Created by root on 16-3-27.
 */

/*
*Some basic operation of the phonelist.
*For exampe, the call and delete function
*/
public class PhoneOperation {

    final Context context;
    private String TAG = this.getClass().toString();

    public PhoneOperation(Context context) {
        this.context = context;
    }

    public void call(String phoneNumber)
    {
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));
        try {
            context.startActivity(intent);
        }
        catch (SecurityException e)
        {
            Log.i(TAG,"Call permission denied");
        }
    }


    public void delete(String id)
    {
        try {
            Log.i("context",context + "");
            context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, PhoneDictionary.ID + " = ? ", new String[]{id});
        }
        catch (SecurityException e)
        {
            Log.i(TAG, "Delete permission denied");
        }
    }

    public void sms(String number){
        try {
            Uri uri = Uri.parse("smsto:" + number);
            Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,uri);
            context.startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sms(String phoneNumber, String message){
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO,uri);
        intent.putExtra("sms_body",message);
        try {
            context.startActivity(intent);
        }
        catch (SecurityException e)
        {
            Log.i(TAG, "Call permission denied");
        }
    }

    public void deleteAll(String number)
    {
        try{
            context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,PhoneDictionary.NUMBER + " = ? ", new String[]{number});
        }
        catch (SecurityException e) {
            Log.i(TAG,"DeleteAll permission denied");
        }
    }


}
