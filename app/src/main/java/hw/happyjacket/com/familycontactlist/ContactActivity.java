package hw.happyjacket.com.familycontactlist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.widget.Toolbar;

//import android.support.v7.widget.Toolbar;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.ContentActivity;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.myphonebook.show.ContactShow;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-13.
 */
public class ContactActivity extends AppCompatActivity{

    private String name;
    private String number;
    private int contactID;
    private ScrollListView ContactListView;
    private ContactShow mContactShow;
    private CollapsingToolbarLayout head;
    private HashMap<String,Object> data;
    private int uid;
    private Bitmap picture;
    private DBHelper mDBHelper;
    private android.support.v7.widget.Toolbar mToolbar;


    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.contact_main);
        ContactListView = (ScrollListView) findViewById(R.id.contact_number_and_detail);
        head = (CollapsingToolbarLayout) findViewById(R.id.contact_toolbar_layout);
        final Intent intent = getIntent();
        data = (HashMap)intent.getSerializableExtra("data");
        mDBHelper = new DBHelper(this);
        showDetail();

    }

    private void showDetail(){
        name = (String)data.get("contactName");
        contactID = (int)data.get("contactID");
        Bitmap bitmap = (Bitmap) data.get("contactPhoto");
        mToolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(mToolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        head.setTitle(name);
        uid = (int) data.get("UserID");

        mContactShow = new ContactShow(this, R.layout.call_log_list, name);
        mContactShow.getPhoneList().setDb("contact");
        mContactShow.getPhoneList().setTable("user");
        mContactShow.InitAdapter(new XiaoMiAccessory(), new String[]{"mobilephone"}, "uid" + " = ? ", new String[]{"" + uid}, null);
        ContactListView.setAdapter(mContactShow.getPhoneAdapter());
        ContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Operation.call(mContactShow.getNumber());
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent intent1 = new Intent(ContactActivity.this, ChangePeopleDetail.class);
                        intent1.putExtra("data", data);
                        startActivityForResult(intent1, PhoneDictionary.CONTACT_REQUEST_CODE);
                        break;
                    case 3:
                        BlackListMaster blackListMaster = new BlackListMaster(ContactActivity.this);
                        if(mContactShow.isInBlackList())
                            blackListMaster.delete(mContactShow.getNumber());
                        else
                            blackListMaster.add(mContactShow.getNumber());
                        mContactShow.setInBlackList(!mContactShow.isInBlackList());
                        mContactShow.notifyDataSetChanged();
                        break;
                    case 4:
                        User user = new User();
                        user.uid = uid;
                        mDBHelper.deleteUser(user);
                        Intent intent = new Intent();
                        intent.putExtra(TabContactsFragment.DELETE,true);
                        intent.putExtra(TabContactsFragment.POS,(int)data.get(TabContactsFragment.POS));
                        setResult(PhoneDictionary.CONTACT_DELETE, intent);//不管有没修改都要更新数据
                        finish();
                        break;
                    case 5:

                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent datas = new Intent();
        HashMap<String,Object> newdata = new HashMap<String,Object>();
        newdata.put("contactName", name);
        newdata.put("contactPhoto", picture);
        newdata.put("contactSortname", this.data.get("contactSortname"));
        newdata.put(PhoneDictionary.Photo,data.get(PhoneDictionary.Photo));
        datas.putExtra(PhoneDictionary.OTHER, newdata);
        setResult(PhoneDictionary.CONTAT_ACTION_START, datas);//不管有没修改都要更新数据
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {



        mContactShow.destroy();
        PhoneRegister.unRegister(mContactShow.getIndex());
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent datas) {

        switch (resultCode){//处理结果码
            case PhoneDictionary.CONTACT_REQUEST_CODE://有修改
                HashMap<String,Object> t = (HashMap<String,Object>) datas.getSerializableExtra(PhoneDictionary.OTHER);
                name = (String) t.get(PhoneDictionary.NAME);
                head.setTitle(name);
                mContactShow.setNumber(number = (String) t.get(PhoneDictionary.NUMBER));
                HashMap<String,String> theFirstLine = mContactShow.getPhoneListElementList().get(0);
                theFirstLine.put(PhoneDictionary.DATE, number);
                theFirstLine.put(PhoneDictionary.NUMBER, mContactShow.getPhoneListElementList().get(0).get(PhoneDictionary.NUMBER));
                mContactShow.getPhoneListElementList().set(0, theFirstLine);
                mContactShow.notifyDataSetChanged();
                picture = (Bitmap) t.get(PhoneDictionary.Picture);
                Object tmp;
                for(Map.Entry<String,Object> i : data.entrySet()){
                    if((tmp = t.get(i.getKey()))!= null){
                        data.put(i.getKey(),tmp);
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, datas);
    }

    public static void actionStart(Activity context,HashMap<String,Object> map){
        Intent intent = new Intent(context,ContactActivity.class);
        intent.putExtra("data",map);
        context.startActivityForResult(intent, PhoneDictionary.CONTAT_ACTION_START);
    }

}