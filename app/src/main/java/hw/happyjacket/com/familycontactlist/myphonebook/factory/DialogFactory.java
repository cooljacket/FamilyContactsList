package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.option.MainDialog;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-11.
 */
public class DialogFactory {

    public static PhoneDialog getPhoneDialog(Context context, int layout, int style, final int index, String content[], int status){
        View viewForOption = LayoutInflater.from(context).inflate(R.layout.main_option,null);
        ListView OptionListView = (ListView)viewForOption.findViewById(R.id.main_list);
        PhoneDialog option = null;
        switch (status){
            case 0:
                option = new MainDialog(context,style,index);
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
        Vector<String> data = new Vector<>();
        for(String i : content)
            data.add(i);
        ArrayAdapter<String> t = new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,data);
        OptionListView.setAdapter(t);
        OptionListView.setOnItemClickListener(option);
        option.setContentView(viewForOption);
        Window window = option.getWindow();
        window.setGravity(Gravity.BOTTOM);
        return option;
    }
}
