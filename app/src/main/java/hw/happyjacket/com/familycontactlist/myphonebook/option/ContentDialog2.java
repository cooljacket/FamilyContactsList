package hw.happyjacket.com.familycontactlist.myphonebook.option;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-12.
 */
public class ContentDialog2 extends PhoneDialog {


    public ContentDialog2(Context context) {
        super(context);
    }

    public ContentDialog2(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ContentDialog2(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ContentDialog2(Context context, int themeResId, int index) {
        super(context, themeResId, index);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> t = PhoneRegister.get(getIndex()).getPhoneListElementList().get(getPos());
        switch (position){
            case 0:
                Operation.call(t.get(PhoneDictionary.NUMBER));
                break;
            case 1:
                Operation.delete(getPos() == 1,t.get(PhoneDictionary.ID),t.get(PhoneDictionary.NUMBER));
                break;
            default:
                break;
        }
        dismiss();
    }
}
