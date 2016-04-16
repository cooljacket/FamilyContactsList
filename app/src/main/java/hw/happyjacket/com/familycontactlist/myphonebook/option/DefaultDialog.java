package hw.happyjacket.com.familycontactlist.myphonebook.option;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;

/**
 * Created by root on 16-4-15.
 */
public class DefaultDialog extends PhoneDialog {
    public DefaultDialog(Context context) {
        super(context);
    }

    public DefaultDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DefaultDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DefaultDialog(Context context, int themeResId, int index) {
        super(context, themeResId, index);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
