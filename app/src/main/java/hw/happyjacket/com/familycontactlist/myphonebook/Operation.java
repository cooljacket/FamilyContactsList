package hw.happyjacket.com.familycontactlist.myphonebook;

import android.content.Context;

import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;

/**
 * Created by root on 16-4-11.
 */
public class Operation {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static void delete(String id,String number) {
        new PhoneOperation(sContext).delete(id);
        PhoneRegister.delete(id,number);
    }

    public static void call(String number){
        new PhoneOperation(sContext).call(number);
    }

}

