package hw.happyjacket.com.familycontactlist.phone.phonelistener;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;

/**
 * Created by root on 16-4-2.
 */
public class PhoneOnLongClickListener implements View.OnLongClickListener {

    private static PhoneOnLongClickListener sPhoneOnLongClickListener;
    private static Context sContext;
    private static boolean setUp = false;

    @Override
    public boolean onLongClick(View v) {
        PhoneButton phoneButton = (PhoneButton) v;
        String number = phoneButton.getNumber();
        Toast.makeText(sContext,number,Toast.LENGTH_SHORT).show();
        return false;
    }

    public static PhoneOnLongClickListener getInstance()
    {
        return sPhoneOnLongClickListener == null ? sPhoneOnLongClickListener = new PhoneOnLongClickListener() : sPhoneOnLongClickListener;
    }

    public static void setContext(Context context)
    {
        sContext = context;
    }

    public static boolean isSetUp() {
        return setUp;
    }

    public static void setSetUp(boolean setUp) {
        PhoneOnLongClickListener.setUp = setUp;
    }
}
