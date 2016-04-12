package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.show.ContentShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.R;

/**
 * Created by root on 16-3-29.
 */
public class ContentActivity extends Activity {

    private String TAG = this.getClass().toString();
    private String number;
    private String backup;
    private String name;
    private TextView head;
    private ListView mListView;
    private TextView returns;
    private ContentShow mContentShow;
    private AlertDialog.Builder mBuilder;
    final static String key = PhoneDictionary.DATE;
    final String defaultName = "陌生联系人";
    final String condition = PhoneDictionary.NUMBER + " = ? ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        returns = (TextView) findViewById(R.id.button_return);
        returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mContentShow.destroy();
        PhoneRegister.unRegister(mContentShow.getIndex());
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    public static void actionStart(Context context, String number, String name)
    {
        Intent intent = new Intent(context,ContentActivity.class);
        intent.putExtra(PhoneDictionary.NUMBER, number);
        intent.putExtra(PhoneDictionary.NAME, name);
        context.startActivity(intent);
    }


    public void init()
    {
        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setIcon(R.mipmap.ic_launcher);
        mBuilder.setNegativeButton("取消", null);
        Intent intent = getIntent();
        number = intent.getStringExtra(PhoneDictionary.NUMBER);
        backup = intent.getStringExtra(PhoneDictionary.NAME);
        name = backup == null ? defaultName : new String(backup);
        head = (TextView) findViewById(R.id.content_name);
        head.setText(name);
        mListView = (ListView) findViewById(R.id.content_number_and_detail);
        mContentShow = new ContentShow(this,R.layout.call_log_list,number);
        mContentShow.InitAdapter(new XiaoMiAccessory(), PhoneDictionary.PhoneCallLog, condition, new String[]{number}, CallLog.Calls.DEFAULT_SORT_ORDER);
        mListView.setAdapter(mContentShow.getPhoneAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        if (mContentShow.getStatus() == PhoneDictionary.CONTENT1)
                            new PhoneOperation(ContentActivity.this).call(number);
                        else
                            CallLogActivity.actionStart(ContentActivity.this, number, name);
                        break;
                    default:
                        new PhoneOperation(ContentActivity.this).call(number);
                        break;
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final HashMap<String, String> t = mContentShow.getPhoneListElementList().get(position);
                boolean pass = true;
                mBuilder.setTitle(t.get(PhoneDictionary.NUMBER));
                switch (position) {
                    case 0:
                        mBuilder.setItems(PhoneDictionary.ContentItems1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        new PhoneOperation(ContentActivity.this).call(t.get(PhoneDictionary.NUMBER));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        break;

                    default:
                        if (mContentShow.getStatus() == PhoneDictionary.CONTENT1) {
                            mBuilder.setItems(PhoneDictionary.ContentItems2, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            new PhoneOperation(ContentActivity.this).delete(t.get(PhoneDictionary.ID));
                                            PhoneRegister.delete(t.get(PhoneDictionary.ID), t.get(PhoneDictionary.NUMBER));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                        } else
                            pass = false;
                        break;

                }
                if(pass)
                    mBuilder.show();
                return pass;
            }

        });

    }


}
