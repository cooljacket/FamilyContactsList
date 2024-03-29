package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.extention.Decorate;
import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.CallLogShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.R;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-4-1.
 */
public class CallLogActivity extends Activity {

    private  final String TAG =CallLogActivity.class.toString() ;
    private String number;
    private String name;
    private ListView mListView;
    private Vector<HashMap<String,String>> data;
    private CallLogShow callLogShow;
    private PhoneDialog callLogDialog;
    Decorate decorate;
    final String condition = PhoneDictionary.NUMBER + " = ? ";
    private AlertDialog.Builder mPhoneBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log);
        init(new XiaoMiAccessory());

    }

    @Override
    public void onStart()
    {
        super.onStart();
        callLogShow.refresh(new XiaoMiAccessory(),new String[]{PhoneDictionary.DATE});
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        callLogShow.destroy();
        PhoneRegister.unRegister(callLogShow.getIndex());
    }

    public static void actionStart(Context context, String number, String name)
    {
        Intent intent = new Intent(context,CallLogActivity.class);
        intent.putExtra(PhoneDictionary.NUMBER, number);
        intent.putExtra(PhoneDictionary.NAME, name);
        context.startActivity(intent);
    }

    public void init(Accessory accessory)
    {
        try {
            Intent intent = getIntent();
            decorate = new Decorate(accessory);

            number = intent.getStringExtra(PhoneDictionary.NUMBER);
            name = intent.getStringExtra(PhoneDictionary.NAME);
            mListView = (ListView) findViewById(R.id.call_log_content);
            callLogShow = new CallLogShow(CallLogActivity.this,R.layout.call_log_list);
            callLogShow.InitAdapter(new XiaoMiAccessory(),PhoneDictionary.PhoneCallLog,condition,new String[]{number},CallLog.Calls.DEFAULT_SORT_ORDER);
            callLogDialog = DialogFactory.getPhoneDialog(this,R.layout.main_option,R.id.main_list,R.style.Menu,callLogShow.getIndex(),PhoneDictionary.CallLogItems,PhoneDictionary.CALLLOG_OPTIONS);
            mListView.setAdapter(callLogShow.getPhoneAdapter());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new PhoneOperation(CallLogActivity.this).call(number);
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    callLogDialog.setPos(position);
                    callLogDialog.show();
                    return true;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
