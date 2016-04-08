package hw.happyjacket.com.familycontactlist.myphonebook;

import android.content.Context;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneList;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-3-29.
 */

/*Used to create the detail of a call record*/
public class PhoneContent {
    final Context context;
    private String name;
    private String number;
    private String project[];
    private String selection;
    private String argument[];
    private String TAG = this.getClass().toString();


    public PhoneContent(Context context, String name, String number) {
        this.context = context;
        this.name = name;
        this.number = number;
        project = new String[]{PhoneDictionary.DATE,PhoneDictionary.DURATION,PhoneDictionary.TYPE};
        selection = PhoneDictionary.NUMBER + "= ?";
        argument = new String[]{number};
    }

    public PhoneContent(Context context, String name, String number, String[] project, String selection, String[] argument) {
        this.context = context;
        this.name = name;
        this.number = number;
        this.project = project;
        this.selection = selection;
        this.argument = argument;
    }

    public Vector<HashMap<String,String>> record()
    {
        PhoneList phoneList = new PhoneList(context,project,selection,argument);
        phoneList.connectContentResolver();
        return phoneList.getPhoneList();
    }




}
