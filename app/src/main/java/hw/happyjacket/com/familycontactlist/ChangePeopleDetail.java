package hw.happyjacket.com.familycontactlist;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.widget.ViewSwitcher;


import hw.happyjacket.com.familycontactlist.myphonebook.DefaultPicture;
import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PeopleInfoAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

//import com.example.menu.MyLetterListView.OnTouchingLetterChangedListener;

/**
 * Created by leo on 2016/3/30.
 */
public class ChangePeopleDetail extends AppCompatActivity {

    private static String TAG = ChangePeopleDetail.class.toString();
    private ImageButton btn_img;
    private User user;
    private int imagePosition;
    private EditText et_familyName;
    private EditText et_name;
    private Button add_info;
    private Button btn_save;
    private Button btn_return;
    private Button group_name_button;
    private TextView groupName;
    private RelativeLayout group;
    private Toolbar toolbar;
    private int imageP;//头像序号
    private Bitmap imagePic;//头像Rid
    private String oldGroupName;//旧的群组名
    private String number;
    private String name;
    private int imagePP;
    private Vector<String[]> info;// = new Vector<>();
    private ScrollListView mListView;
    private PeopleInfoAdapter mPeopleInfoAdapter;
    private DBHelper mDBHelper;
    private String have[];
    private Vector<String> AllGroup;//存可选的群组
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 102:
                    StringBuffer stringBuffer = new StringBuffer();
                    Vector<Boolean> t = (Vector<Boolean>)msg.obj;
                    for(int i = 0 ; i < t.size(); ++i)
                        if (t.get(i))
                            stringBuffer.append(AllGroup.get(i) + "/");
                    if(stringBuffer.length() == 0) {
                        stringBuffer.append("无");
                        have = null;
                    }
                    else {
                        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                        have = stringBuffer.toString().split("/");
                    }
                    groupName.setText(stringBuffer.toString());
                    break;

                case 100:
                    imagePic = DefaultPicture.ImagePicture;
                    user.photo = DefaultPicture.ImagePosition;
                    break;
                case 104:
                    info.add(new String[]{PhoneDictionary.PhoneCallChoices[msg.arg1], ""});
                    mPeopleInfoAdapter.notifyDataSetChanged();
                    break;
                case 105:


                default:
                    int tmp = msg.what;
                    if(tmp>=0 && tmp < info.size()){
                        info.get(tmp)[0] = PhoneDictionary.PhoneCallChoices[msg.arg1];
                        mPeopleInfoAdapter.notifyDataSetChanged();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){//处理请求

            case PhoneDictionary.IMAGE_REQUEST_CODE:
                if(data != null)
                    PhotoZoom.startPhotoZoom(ChangePeopleDetail.this,data.getData());
                break;
            case PhoneDictionary.RESULT_REQUEST_CODE:
                if(data != null){
                    imagePic = PhotoZoom.getImageToView(ChangePeopleDetail.this, data);
                    imagePic = PhotoZoom.createCircleImage(imagePic, imagePic.getWidth(), imagePic.getHeight());
                    DefaultPicture.ImageP = DefaultPicture.ImagePosition = -1;
                    btn_img.setImageBitmap(imagePic);
                    user.photo = -1;
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), requestCode, Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_people_detail);
        final Intent intent = getIntent();
        user = new User();
        mDBHelper = new DBHelper(this);
        group = (RelativeLayout) findViewById(R.id.edit_group);
        AllGroup = mDBHelper.getGroup();
        groupName = (TextView)findViewById(R.id.group_name);
        group_name_button = (Button) findViewById(R.id.group_name_button);
        toolbar = (Toolbar) findViewById(R.id.change_people_toolbar);
        btn_img = (ImageButton) findViewById(R.id.btn_img);
        mListView = (ScrollListView) findViewById(R.id.people_info);
        et_name = (EditText) findViewById(R.id.et_name);
        et_familyName=(EditText)findViewById(R.id.et_familyName);
        add_info = (Button)findViewById(R.id.add_info);
        btn_return=(Button)findViewById(R.id.btn_return);
        btn_save=(Button)findViewById(R.id.btn_save);
        info = new Vector<>();

        initUser();
        getUser();
        DefaultPicture.ImageP = user.photo;
        DefaultPicture.ImagePosition = DefaultPicture.ImageP;


        mPeopleInfoAdapter = new PeopleInfoAdapter(this,R.layout.change_people_detail,info);
        toolbar.setTitle("编辑联系人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        String group_tmp = groupName.getText().toString();
        if(!group_tmp.equals("无")) {
            String t = groupName.getText().toString();
            have = t.split("/");
        }

        group_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getCheckBoxDialog(ChangePeopleDetail.this, R.style.Menu, AllGroup, have, mHandler, 102).show();
            }
        });



        mListView.setAdapter(mPeopleInfoAdapter);
        Log.i(TAG,user.photo +  "");
        DefaultPicture.ImagePicture = (imagePic = PhotoZoom.getBitmap(user.mobilephone, user.photo, TabContactsFragment.circleImage));
        btn_img.setImageBitmap(imagePic);
        btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getPhotoDialog(ChangePeopleDetail.this, "请选择方法", new String[]{"从相库中选取", "从默认图片选取"}, new MyViewFactory(ChangePeopleDetail.this), btn_img, mHandler).show();
            }
        });

        et_name.setText(user.name);

//        family = (boolean)map.get("contactFamily");
//        if(family){
//            et_family.setChecked(true);
//            et_familyName.setEnabled(true);
//            et_familyName.setText(map.get("contactFamilyname").toString());
//            et_group.setEnabled(false);
//        }
//        else {
//            et_family.setChecked(false);
//            et_familyName.setEnabled(false);
//            et_familyName.setText("NO");
//            et_group.setEnabled(true);
//        }

//        et_family.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    family=true;
//                    et_familyName.setEnabled(true);
//                    et_group.setText("FAMILY");
//                    et_group.setEnabled(false);
//                }
//                else {
//                    family=false;
//                    et_familyName.setEnabled(false);
//                    et_group.setEnabled(true);
//                    et_group.setText("UNKNOWN");
//                }
//            }
//        });

        add_info.setOnClickListener(new OnClickListener() {//创建新项目；更新adapter
            @Override
            public void onClick(View v) {



                DialogFactory.getRadioDialog(ChangePeopleDetail.this,R.style.Menu,PhoneDictionary.PhoneCallChoices,mHandler,104).show();


//                mPeopleInfoAdapter.notifyDataSetChanged();
//                mPeopleInfoAdapter = new PeopleInfoAdapter(this,R.layout.change_people_detail,info);
//        Toast.makeText(this,mPeopleInfoAdapter.getItem(1)[1],Toast.LENGTH_SHORT).show();
                //mPeopleInfoAdapter.notifyDataSetChanged();
//                mListView.setAdapter(mPeopleInfoAdapter);
            }
        });

        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*changePhoneContact();*/
//
//                for(int i=0;i<5;i++){
//                    if(PhoneDictionary.ImageID[i]== DialogFactory.getImagePP()){
//                        DialogFactory.setImageP(i);
//                        break;
//                    }
//                }
//                Toast.makeText(getApplicationContext(),"image1  "+imageP
//                        +"image2  "+imagePosition, Toast.LENGTH_SHORT).show();
//                imagePic= btn_img.();
                name = et_name.getText().toString();
                number = info.get(0)[1];
                if (name == null || name.equals("")) {
                    DialogFactory.WarningDialog(ChangePeopleDetail.this, "姓名不能为空").show();
                    return;
                }
                if (number == null || number.equals("") ){
                    DialogFactory.WarningDialog(ChangePeopleDetail.this,"号码不能为空").show();
                    return;
                }
                Changed();
                MainActivity.changePeopleDetail(user, imagePic);
                int tmp = user.photo;
                if(tmp == -1)
                    PhotoZoom.saveBitmap(user.mobilephone, imagePic);
                Intent intent1 = new Intent();
                intent1.putExtra(TabContactsFragment.NAME,user.name);
                intent1.putExtra(TabContactsFragment.NUMBER,user.mobilephone);
                setResult(PhoneDictionary.CONTACT_REQUEST_CODE,intent1);
                finish();

            }
        });
        btn_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });





    }

    @Override
    public void onStart(){
        super.onStart();
        mPeopleInfoAdapter = new PeopleInfoAdapter(this,R.layout.change_people_detail,info,mHandler);
        mListView.setAdapter(mPeopleInfoAdapter);
    }



    private void updateUser(HashMap<String,Object> map){
        DBHelper helper =new DBHelper(ChangePeopleDetail.this.getApplicationContext());

        User user = new User();
        user.uid = (int) map.get("UserID");
        user.photo = DefaultPicture.ImageP;
//        String[] s1 =(String[]) mListView.getItemAtPosition(0);
        //sb.append(ss[0]).append(";").append(ss[1]).append(";");
        user.mobilephone=(String) map.get("phoneNumber");

        user.name=et_name.getText().toString();
        user.info=(String) map.get("info");
        user.sortname = CommonSettingsAndFuncs.convertToShortPinyin(this, user.name);

//        String[] s2 =(String[]) mListView.getItemAtPosition(mListView.getCount()-1);
//        mPeopleInfoAdapter.notifyDataSetChanged();
//        String s2 = mPeopleInfoAdapter.getItem(mListView.getCount()-1)[1];
        user.nickname = (String) map.get("nickName");
        user.groupname = groupName.getText().toString();
        Group group = new Group();
        group.groupname=user.groupname;
        group.groupnum = helper.getGroupNum(group);
        group.groupnum++;
        helper.changeGroup(group);
        Group group1 = new Group();
        group1.groupname=oldGroupName;
        group1.groupnum=helper.getGroupNum(group1);
        group1.groupnum--;
        helper.changeGroup(group1);

//                user.group=mListView.getItemAtPosition(5);
//                user.family = family;
//                if(family){
//                    user.familyName=et_familyName.getText().toString();
//                    user.group="FAMILY";
//                }
//                else {
//                    user.familyName="NO";
//                    user.group="";
//                }
        //DBHelper.change(user);
        //!!!!这里！！！！

        helper.changeUser(user);
//        helper.changeGroup(group);

        /*boolean hadgroup=false;
        for(int i=0;i<AllGroup.size();i++){
            if(user.groupname==AllGroup.get(i)){
                hadgroup=true;
            }
        }
//        DBHelper dbHelper = null;
        if(!hadgroup){//新加入一个group给grouptable
            Cursor cursor;


            cursor = db.query("grouptable",null,"groupname='"+user.groupname+"'",null,null,null,null);
            if(cursor==null) {//若没有这个组
                Group group = new Group();
                group.groupname=user.groupname;
                group.groupnum=1;
                helper.initGroup(group);

            }
            else{
                if(cursor.moveToFirst()){
                    Group group=new Group();
                    group.groupname=user.groupname;
                    group.groupnum= cursor.getInt(1) + 1;
                    helper.changeGroup(group);
                }
            }
        }
        helper.close();*/
    }


    private void initUser(){
        Intent intent = getIntent();
        user.name = intent.getStringExtra(TabContactsFragment.NAME);
        user.mobilephone = intent.getStringExtra(TabContactsFragment.NUMBER);
    }


    private void getUser(){
        try {
            DBHelper helper = new DBHelper(ChangePeopleDetail.this.getApplicationContext());
            SQLiteDatabase db = helper.openDatabase();
            Cursor cursor = db.query(DBHelper.DB_TABLENAME, new String[]{DBHelper.ID,DBHelper.SORTNAME, DBHelper.PHOTO, DBHelper.GROUPNAME, DBHelper.INFO, DBHelper.NICKNAME}, DBHelper.NUMBER + " = " + user.mobilephone, null, null, null, null, "1");
//        Toast.makeText(getApplicationContext(),"get"+Userid, Toast.LENGTH_SHORT).show();
            String groupname = null, infos = null, nickName = null;
            if (cursor.moveToFirst()) {
                user.uid = cursor.getInt(0);
                user.sortname = cursor.getString(1);
                user.photo = cursor.getInt(2);
                groupname = user.groupname = cursor.getString(3);
                infos = user.info = cursor.getString(4);
                nickName = user.nickname = cursor.getString(5);
            }
            cursor.close();
            info.add(new String[]{"手机", user.mobilephone});
            String pname = "", parameter = "";
            if (infos != null && !infos.equals("")) {
                String[] infoTmp = infos.split("&&");
                for (int i = 0; i < infoTmp.length; i += 2) {
                    info.add(new String[]{infoTmp[i], infoTmp[i + 1]});
                }
            }

            groupName.setText(groupname == null ? "无" : groupname);
            oldGroupName = groupName.getText().toString();
            et_familyName.setText(nickName == null ? "" : nickName);

//        groupName.setText("aaaaaaaaaa");
//        list1.add(groupname);
//        list2.add("组群");
//        info.add(new String[]{"组群",groupname});
//        int size = list1.size();
//        param = (String[])list1.toArray(new String[size]);
//        paramName=(String[])list2.toArray(new String[size]);
//
//        info = new String[param.length][2];
//
//        for(int i = 0 ; i < param.length ; i++){
//            info[i][0] = paramName[i];
//            info[i][1] = param[i];
//        }
            helper.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//
//    private void changePhoneContact(){
////        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//        String contactid = map.get("contactID").toString();
//        ContentValues values ;
//        ContentResolver resolver = this.getApplicationContext().getContentResolver();
//        Toast.makeText(getApplicationContext(),"改改改！！！！", Toast.LENGTH_SHORT).show();
//
//        // 更新Display_name
//
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, et_name.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "
//                , new String[]{contactid, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
//
//        // 更新homePhone
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_home.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
//                , new String[]{contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)});
//
//        // 更新workPhone
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_work.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
//                , new String[]{contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)});
//
//        // 更新mobilePhone
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_phone.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? " + " AND "
//                        + ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
//                , new String[]{contactid, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)});
//
//        // 更新email
////        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
////                .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND "
////                                + ContactsContract.Data.MIMETYPE + "=?" + " AND "
////                                + ContactsContract.CommonDataKinds.Email.TYPE + "=?",
////                        new String[]{contactid, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK)})
////                .withValue(ContactsContract.CommonDataKinds.Email.DATA, et_email.getText())
////                .build());
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.Email.DATA, et_email.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=?"
//                , new String[]{contactid, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE});
//
//        // 更新remark
//        values = new ContentValues();
//        values.put(ContactsContract.CommonDataKinds.Note.NOTE, et_remark.getText().toString());
//        resolver.update(ContactsContract.Data.CONTENT_URI, values,
//                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                        + ContactsContract.Data.MIMETYPE + "=? "
//                , new String[]{contactid, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE});
//
//
//    }
//

    private void Changed(){
        user.name =  et_name.getText().toString();
        user.sortname = CommonSettingsAndFuncs.convertToShortPinyin(this,user.name);
        user.photo  = DefaultPicture.ImagePosition;
        user.mobilephone = info.get(0)[1];
        user.nickname = et_familyName.getText().toString();
        StringBuffer infoTmp = new StringBuffer();
        String[] tt;
        for(int i = 1 ; i < info.size(); ++i){
            tt = info.get(i);
            if(tt[1] == null || tt[1].equals(""))
                continue;
            if(i > 1)
                infoTmp.append("&&");
            infoTmp.append(tt[0]).append("&&").append(tt[1]);
        }
        user.info = infoTmp.toString();
        user.groupname = groupName.getText().toString();
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.changeUser(user);
        dbHelper.close();
    }



    public class MyViewFactory implements ViewSwitcher.ViewFactory {
        private Context context;

        public MyViewFactory(Context context){
            this.context=context;
        }

        @Override
        public View makeView(){
            ImageView iv = new ImageView(context);
            iv.setLayoutParams(new ImageSwitcher.LayoutParams(250,250));
            return iv;
        }
    }







}

