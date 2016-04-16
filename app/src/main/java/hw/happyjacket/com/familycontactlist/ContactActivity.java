package hw.happyjacket.com.familycontactlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.myphonebook.show.ContactShow;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-13.
 */
public class ContactActivity extends Activity{

    private String name;
    private int contactID;
    private ScrollListView ContactListView;
    private ContactShow mContactShow;
    private TextView returns;
    private CollapsingToolbarLayout head;
    private HashMap data;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.contact_main);
        /*returns = (TextView) findViewById(R.id.button_return);
        returns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/


        final Intent intent = getIntent();
        data = (HashMap)intent.getSerializableExtra("data");
        name = (String)data.get("contactName");
        contactID = (int)data.get("contactID");
        ContactListView = (ScrollListView) findViewById(R.id.contact_number_and_detail);
        head = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        head.setTitle(name);
        mContactShow = new ContactShow(this,R.layout.call_log_list);
        mContactShow.getPhoneList().setUri(ContactsContract.Data.CONTENT_URI);
        mContactShow.InitAdapter(new XiaoMiAccessory(), new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.Data.CONTACT_ID + " = ? ", new String[]{"" + contactID}, null);
        ContactListView.setAdapter(mContactShow.getPhoneAdapter());
        ContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Operation.call(mContactShow.getNumber());
                        break;
                    case 1:
                        Intent intent1 = new Intent(ContactActivity.this,PeopleDetail.class);
                        intent1.putExtra("data",data);
                        startActivity(intent1);
                        break;
                    default:
                        break;
                }
            }
        });



    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactShow.destroy();
        PhoneRegister.unRegister(mContactShow.getIndex());
    }

    public static void actionStart(Context context,HashMap map){
        Intent intent = new Intent(context,ContactActivity.class);
        intent.putExtra("data",map);
        context.startActivity(intent);
    }
}
