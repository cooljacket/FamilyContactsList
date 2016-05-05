package hw.happyjacket.com.familycontactlist.myphonebook;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.BlackListMaster;
import hw.happyjacket.com.familycontactlist.PhoneCallUtis;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.phone.list.PhoneList;
import hw.happyjacket.com.familycontactlist.phone.list.RecordList;

/**
 * Created by root on 16-4-11.
 */
public class Operation {

    private static Context sContext;

    private static PhoneOperation sPhoneOperation;

    private static PhoneCallUtis sPhoneCallUtis;

    private static BlackListMaster sBlackListMaster;

    private static PhoneLocationMaster sPhoneLocationMaster;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
        sPhoneOperation = new PhoneOperation(context);
        sPhoneCallUtis = new PhoneCallUtis(context);
        sBlackListMaster = new BlackListMaster(context);
        sPhoneLocationMaster = new PhoneLocationMaster(context);
    }

    public static void delete(Boolean first,String id,String number) {
        sPhoneOperation.delete(id);
        PhoneRegister.delete(first,id,number);
    }

    public static void deleteAll(String number){
        sPhoneOperation.deleteAll(number);
    }

    public static void deletePhoneTheNewestOne(){
        RecordList recordList  = new RecordList(sContext,new String[]{PhoneDictionary.ID});
        recordList.setOrderby(CallLog.Calls.DEFAULT_SORT_ORDER + " limit 1 ");
        recordList.connectContentResolver();
        Vector<HashMap<String,String>> t = recordList.getPhoneList();
        sPhoneOperation.delete(t.get(0).get(PhoneDictionary.ID));
        recordList.destroyPhoneList();

    }

    public static void call(String number){
        sPhoneOperation.call(number);
    }

    public static void sms(String number) {sPhoneOperation.sms(number);}

    public static void sms(String number, String message) {sPhoneOperation.sms(number,message);}

    public static void endCall(String number){sPhoneCallUtis.endCall(number);}

    public static boolean isBlackList(String number){
        number = number.replace(" ","").replace("+86","").replace("+","");
        return sBlackListMaster.isInBlackList(number);
    }

    public static void addBlackList(String number){
        number = number.replace(" ","").replace("+86","").replace("+","");
        sBlackListMaster.add(number);
    }

    public static void deleteBlackList(String number){
        sBlackListMaster.delete(number);
    }

    public static String getLocation(String number){
        String t[];
        return ((t = sPhoneLocationMaster.get(number)) == null || t.length < 2)? null:t[0].equals(t[1]) ? t[1] : t[0] + t[1];
    }

}

