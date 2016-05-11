package hw.happyjacket.com.familycontactlist.myphonebook.option;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.BlackListMaster;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-12.
 */
public class MainDialog extends PhoneDialog {

    public MainDialog(Context context) {
        super(context);
    }

    public MainDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MainDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public MainDialog(Context context, int themeResId, int index) {
        super(context, themeResId, index);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> data = PhoneRegister.get(getIndex()).getPhoneListElementList().get(getPos());
        switch (position){
            case 0:
                Operation.call(data.get(PhoneDictionary.NUMBER));
                break;
            case 1:
                Operation.delete(true,data.get(PhoneDictionary.ID),data.get(PhoneDictionary.NUMBER));
                break;
            case 2:
                Operation.addBlackList(data.get(PhoneDictionary.NUMBER));
                break;
            case 3:
                Operation.deleteBlackList(data.get(PhoneDictionary.NUMBER));
                break;
            default:
                break;
        }
        dismiss();
    }
}
