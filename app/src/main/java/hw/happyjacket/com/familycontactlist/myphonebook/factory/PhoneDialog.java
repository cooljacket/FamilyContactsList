package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;

/**
 * Created by root on 16-4-12.
 */
public abstract class PhoneDialog extends Dialog implements AdapterView.OnItemClickListener{

    private int pos;
    private int index;
    private int additon;
    public PhoneDialog(Context context) {
        super(context);
    }

    public PhoneDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PhoneDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public PhoneDialog(Context context, int themeResId, int index) {
        super(context,themeResId);
        this.index = index;
    }



    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAdditon() {
        return additon;
    }
    public void setAdditon(int additon) {
        this.additon = additon;
    }
}
