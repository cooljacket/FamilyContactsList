package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CheckBoxAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.RadioAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.option.CallLogDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog2;
import hw.happyjacket.com.familycontactlist.myphonebook.option.DefaultDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.MainDialog;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

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

    public static PhoneDialog getRadioDialog(Context context, int style, String content[], final Handler handler){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option,null);
        final RadioAdapter radioAdapter = new RadioAdapter(context,R.layout.dialog_radio,content);
        ListView listView = (ListView) view.findViewById(R.id.dialog_option_listview);
        listView.setAdapter(radioAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                radioAdapter.setIndex(position);
                radioAdapter.notifyDataSetChanged();
            }
        });
        final DefaultDialog defaultDialog = new DefaultDialog(context,style,0);
        defaultDialog.setContentView(view);
        Button positive = (Button) view.findViewById(R.id.dialog_option_positive);
        Button negative = (Button) view.findViewById(R.id.dialog_option_negative);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                message.what = PhoneDictionary.RADIO_OPTION;
                message.arg1 = radioAdapter.getIndex();
                Log.i("haha",message.arg1 + "");
                handler.sendMessage(message);
                defaultDialog.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultDialog.dismiss();
            }
        });
        return defaultDialog;
    }

    public static PhoneDialog getCheckBoxDialog(Context context, int style, String content[], final Handler handler){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option,null);
        final CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(context,R.layout.dialog_checkbox,content);
        ListView listView = (ListView) view.findViewById(R.id.dialog_option_listview);
        listView.setAdapter(checkBoxAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkBoxAdapter.changeCheck(position);
                checkBoxAdapter.notifyDataSetChanged();
            }
        });
        final DefaultDialog defaultDialog = new DefaultDialog(context,style,0);
        defaultDialog.setContentView(view);
        Button positive = (Button) view.findViewById(R.id.dialog_option_positive);
        Button negative = (Button) view.findViewById(R.id.dialog_option_negative);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                message.what = PhoneDictionary.CHECKBOX_OPTION;
                message.obj = checkBoxAdapter.getIsChecked();
                handler.sendMessage(message);
                defaultDialog.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultDialog.dismiss();
            }
        });
        return defaultDialog;

    }


    public static AlertDialog getPhotoDialog(final Activity context, String title, final String [] content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        context.startActivityForResult(intent, PhoneDictionary.IMAGE_REQUEST_CODE);
                        break;
                    default:
                        break;

                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

     return builder.create();


    }
}
