package hw.happyjacket.com.familycontactlist;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Toolbar;

//import android.support.v7.widget.Toolbar;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

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
    private int contactID;
    private ScrollListView ContactListView;
    private ContactShow mContactShow;
    private CollapsingToolbarLayout head;
    private HashMap data;




    private android.support.v7.widget.Toolbar mToolbar;
    String contactHome= new String();
    String contactWork= new String();
    String contactRemark= new String();
    String contactEmail= new String();
    String contactPhone = new String();


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


        ContactListView = (ScrollListView) findViewById(R.id.contact_number_and_detail);
        head = (CollapsingToolbarLayout) findViewById(R.id.contact_toolbar_layout);
        final Intent intent = getIntent();
        data = (HashMap)intent.getSerializableExtra("data");
        showDetail();

    }

    private void showDetail(){
//        getOtherDetail();
        name = (String)data.get("contactName");
        contactID = (int)data.get("contactID");

        Toast.makeText(getApplicationContext(), ""+contactID, Toast.LENGTH_SHORT).show();

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        head.setTitle(name);


//要改！！！！！！！！！！！！！！！！！！！
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
                        break;
                    case 2:
                        Intent intent1 = new Intent(ContactActivity.this, ChangePeopleDetail.class);
                        intent1.putExtra("data", data);
                        startActivityForResult(intent1, PhoneDictionary.CONTACT_REQUEST_CODE);
                        break;
                    case 3:
                        new BlackListMaster(ContactActivity.this).add(mContactShow.getNumber());
                        break;
                    case 4:
                        new BlackListMaster(ContactActivity.this).delete(mContactShow.getNumber());
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }
            }
        });
    }

//    private void getOtherDetail(){//从id查其他数据
//        int contactid = (int)data.get("contactID");
//        ContentResolver resolver1 = this.getApplicationContext().getContentResolver();
//
//        //mobile
//        // 根据contact_ID取得MobilePhone号码
//        Cursor mobilePhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                new String[]{""+contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)}, null);
//        if(mobilePhoneCur.moveToFirst()){
//            contactPhone=mobilePhoneCur.getString(mobilePhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//        }
//        mobilePhoneCur.close();
//
//
//        //home
//        Cursor homePhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                        + ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                new String[]{""+contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)}, null);
//
//        if (homePhoneCur.moveToFirst()) {
//            contactHome = homePhoneCur.getString(homePhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//        }
//        homePhoneCur.close();
//        // work
//        Cursor workPhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                new String[]{""+contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)}, null);
//        if(workPhoneCur.moveToFirst()){
//            contactWork=workPhoneCur.getString(workPhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//        }
//        workPhoneCur.close();
//
//        //email
//
//        Cursor dataCursor = resolver1.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactid, null, null);
//        while (dataCursor.moveToNext()){
//            contactEmail = dataCursor.getString(dataCursor.getColumnIndex(
//                    ContactsContract.CommonDataKinds.Email.DATA));
//            break;
//        }
//        dataCursor.close();
//        //remark
//        String noteWhere =
//                ContactsContract.Data.CONTACT_ID + " = ? AND " +
//                        ContactsContract.Data.MIMETYPE + " = ?";
//
//        String[] noteWhereParams = new String[]{
//                ""+contactid,
//                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//
//        Cursor noteCursor = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                null,
//                noteWhere,
//                noteWhereParams,
//                null);
//        if (noteCursor.moveToFirst()) {
//            contactRemark = noteCursor.getString(noteCursor.getColumnIndex(
//                    ContactsContract.CommonDataKinds.Note.NOTE));
//        }
//
//        noteCursor.close();
//
//
//
//        //补充进map
//        data.put("contactPhone",contactPhone);
//        data.put("contactHome",contactHome);
//        data.put("contactWork", contactWork);
//        data.put("contactRemark", contactRemark);
//        data.put("contactEmail", contactEmail);
//    }


    @Override
    public void onBackPressed() {

        Intent data = new Intent();
//        this.data = getChanged();

        HashMap newdata = new HashMap();
        newdata.put("contactName",this.data.get("contactName"));
        newdata.put("contactPhoto",this.data.get("contactPhoto"));
        newdata.put("contactID",this.data.get("contactID"));
        newdata.put("UserID",this.data.get("UserID"));
        newdata.put("contactSortname", this.data.get("contactSortname"));
        data.putExtra("newdata", newdata);
        setResult(1, data);//不管有没修改都要更新数据
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactShow.destroy();
        PhoneRegister.unRegister(mContactShow.getIndex());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){//处理结果码
            case PhoneDictionary.CONTACT_REQUEST_CODE://有修改
                name = data.getStringExtra(PhoneDictionary.NAME);
                Toast.makeText(ContactActivity.this,name,Toast.LENGTH_SHORT).show();
                head.setTitle(name);
                mContactShow.setNumber(data.getStringExtra(PhoneDictionary.NUMBER));
                HashMap<String,String> theFirstLine = mContactShow.getPhoneListElementList().get(0);
                theFirstLine.put(PhoneDictionary.NUMBER,data.getStringExtra(PhoneDictionary.NUMBER));
                mContactShow.getPhoneListElementList().set(0,theFirstLine);
                mContactShow.notifyDataSetChanged();


//                imagePic = (int) map.get("contactPhoto");
//                isfamily=(boolean)map.get("contactFamily");

//                flag=true;
/*                showDetail();*/

                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void actionStart(Activity context,HashMap map){
        Intent intent = new Intent(context,ContactActivity.class);
        intent.putExtra("data",map);
        context.startActivity(intent);
    }

}