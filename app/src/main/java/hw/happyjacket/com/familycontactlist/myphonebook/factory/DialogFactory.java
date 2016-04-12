package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.option.CallLogDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog2;
import hw.happyjacket.com.familycontactlist.myphonebook.option.MainDialog;

/**
 * Created by root on 16-4-11.
 */
public class DialogFactory {

    public static PhoneDialog getPhoneDialog(Context context, int layout, int id,int style, final int index, String content[], int status){
        View viewForOption = LayoutInflater.from(context).inflate(layout,null);
        ListView OptionListView = (ListView)viewForOption.findViewById(id);
        PhoneDialog option = null;
        switch (status){
            case 0:
                option = new MainDialog(context,style,index);
                break;
            case 1:
                option = new ContentDialog(context,style,index);
                break;
            case 2:
                option = new ContentDialog2(context,style,index);
                break;
            case 3:
                option = new CallLogDialog(context,style,index);
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
