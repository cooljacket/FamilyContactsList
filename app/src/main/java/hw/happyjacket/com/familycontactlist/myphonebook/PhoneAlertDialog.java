package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by root on 16-4-7.
 */
public class PhoneAlertDialog extends AlertDialog {

    private int pos;

    protected PhoneAlertDialog(Context context) {
        super(context);
    }

    protected PhoneAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected PhoneAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
