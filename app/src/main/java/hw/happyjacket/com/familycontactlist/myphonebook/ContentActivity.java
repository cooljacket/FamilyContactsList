package hw.happyjacket.com.familycontactlist.myphonebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.ChangePeopleDetail;
import hw.happyjacket.com.familycontactlist.CreatePeopleDetail;
import hw.happyjacket.com.familycontactlist.TabContactsFragment;
import hw.happyjacket.com.familycontactlist.User;
import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.myphonebook.show.ContentShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.R;

/**
 * Created by root on 16-3-29.
 */
public class ContentActivity extends AppCompatActivity {

    private String TAG = this.getClass().toString();
    private String number;
    private String backup;
    private String name;
    private String location;
    private TextView head;
    private ScrollListView mListView;
    private TextView returns;
    private ContentShow mContentShow;
    private PhoneDialog contentDialog1, contentDialog2;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    final String defaultName = "陌生联系人";
    final String condition = PhoneDictionary.NUMBER + " = ? ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
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

    public static void actionStart(Context context, String number, String name, String location)
    {
        Intent intent = new Intent(context,ContentActivity.class);
        intent.putExtra(PhoneDictionary.NUMBER, number);
        intent.putExtra(PhoneDictionary.NAME, name);
        intent.putExtra(PhoneDictionary.LOCATION, location);
        context.startActivity(intent);
    }


    public void init()
    {
        final Intent intent = getIntent();
        number = intent.getStringExtra(PhoneDictionary.NUMBER);
        backup = intent.getStringExtra(PhoneDictionary.NAME);
        location = intent.getStringExtra(PhoneDictionary.LOCATION);
        name = backup == null ? defaultName : new String(backup);
        toolbar = (Toolbar) findViewById(R.id.content_top_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationContentDescription(name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.content_head);
        collapsingToolbarLayout.setTitle(name);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);


        mListView = (ScrollListView) findViewById(R.id.content_number_and_detail);
        mContentShow = new ContentShow(this,R.layout.call_log_list,number,backup);
        mContentShow.setDefaultNumber(location);
        mContentShow.InitAdapter(new XiaoMiAccessory(), PhoneDictionary.PhoneCallLog, condition, new String[]{number}, CallLog.Calls.DEFAULT_SORT_ORDER);
        contentDialog1 = DialogFactory.getPhoneDialog(ContentActivity.this,R.layout.main_option,R.id.main_list,R.style.Menu,mContentShow.getIndex(),PhoneDictionary.ContentItems1,PhoneDictionary.CONTENT_OPTIONS1);
        contentDialog2 = DialogFactory.getPhoneDialog(ContentActivity.this,R.layout.main_option,R.id.main_list,R.style.Menu,mContentShow.getIndex(),PhoneDictionary.ContentItems2,PhoneDictionary.CONTENT_OPTIONS2);

        mListView.setAdapter(mContentShow.getPhoneAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        if (backup == null)
                            CreatePeopleDetail.actionStart(ContentActivity.this, number);
                        else
                            ChangePeopleDetail.actionStart(ContentActivity.this, number, name);
                        break;
                    case 2:
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
                boolean pass = true;
                switch (position) {
                    case 0:
                        contentDialog1.setPos(position);
                        contentDialog1.show();
                        break;
                    case 1:
                        pass = false;
                        break;
                    case 2:
                        if (mContentShow.getStatus() == PhoneDictionary.CONTENT1) {
                            contentDialog2.setPos(position);
                            contentDialog2.show();
                        } else
                            pass = false;
                        break;
                    default:
                        contentDialog2.setPos(position);
                        contentDialog2.show();
                        break;
                }
                return pass;
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String num;
        switch (resultCode){

            case PhoneDictionary.CREATE_PEOPLE_CODE:
                num = data.getStringExtra(TabContactsFragment.NUMBER);
                if (!number.equals(num))
                    break;
                String name_tmp = data.getStringExtra(TabContactsFragment.NAME);
                collapsingToolbarLayout.setTitle(name_tmp);
                backup = name_tmp;
                name = name_tmp;
                mContentShow.getPhoneListElementList().get(1).put(PhoneDictionary.DATE,"编辑联系人");
                mContentShow.getPhoneAdapter().notifyDataSetChanged();
                break;
            case PhoneDictionary.CONTACT_REQUEST_CODE:
                num = data.getStringExtra(TabContactsFragment.NUMBER);
                if(!num.equals(number)) {
                    collapsingToolbarLayout.setTitle(name = defaultName);
                    backup = null;
                    mContentShow.getPhoneListElementList().get(1).put(PhoneDictionary.DATE,"新建联系人");
                    mContentShow.getPhoneAdapter().notifyDataSetChanged();
                    break;
                }
                name = data.getStringExtra(TabContactsFragment.NAME);
                collapsingToolbarLayout.setTitle(name);
                break;
            default:
                break;
        }
    }
}
