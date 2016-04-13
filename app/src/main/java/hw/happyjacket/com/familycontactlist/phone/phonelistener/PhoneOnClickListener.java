package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.Context;
import android.view.View;

import hw.happyjacket.com.familycontactlist.myphonebook.ContentActivity;
import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;

/**
 * Created by root on 16-4-1.
 */
public class PhoneOnClickListener implements View.OnClickListener {

    private static PhoneOnClickListener instance = null;
    private static Context sContext;
    private static boolean setUp = false;

    public static void setContext(Context context) {
        sContext = context;
    }

    public static PhoneOnClickListener getInstance()
    {
        return instance == null? (instance = new PhoneOnClickListener()) : instance;
    }

    public static boolean isSetUp() {
        return setUp;
    }

    public static void setSetUp(boolean setUp) {
        PhoneOnClickListener.setUp = setUp;
    }

    @Override
    public void onClick(View v) {

        PhoneButton t = (PhoneButton) v;
        int index = t.getIndex();
        String number = t.getNumber();
        String name = t.getName();
        String location = t.getLocation();
        ContentActivity.actionStart(sContext,number,name,location);
    }
}
