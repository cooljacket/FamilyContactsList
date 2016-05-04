package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.myphonebook.DefaultPicture;
import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PeopleInfoAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-5-4.
 */
public class CreatePeopleDetail extends AppCompatActivity {


    private String TAG = this.getClass().toString();
    private ImageButton btn_img;
    private User mUser;
    private String number;
    int imagePosition;
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
                    mUser.photo = DefaultPicture.ImagePosition;
                    break;
                case 103:
                    final  String tt = (String)msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mDBHelper.initGroup(new Group(tt,0));
                        }
                    }).start();

                    break;

                case 104:
                    info.add(new String[]{PhoneDictionary.PhoneCallChoices[msg.arg1],""});
                    mPeopleInfoAdapter.notifyDataSetChanged();
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
                    PhotoZoom.startPhotoZoom(CreatePeopleDetail.this, data.getData());
                break;
            case PhoneDictionary.RESULT_REQUEST_CODE:
                if(data != null){
                    imagePic = PhotoZoom.getImageToView(CreatePeopleDetail.this, data);
                    imagePic = PhotoZoom.createCircleImage(imagePic, imagePic.getWidth(), imagePic.getHeight());
                    DefaultPicture.ImagePosition = DefaultPicture.ImageP = -1;
                    btn_img.setImageBitmap(imagePic);
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


        DefaultPicture.ImagePosition = Math.abs(new Random().nextInt()) % DefaultPicture.ImageID.length;
        DefaultPicture.ImageP = DefaultPicture.ImagePosition;
        DefaultPicture.ImagePicture = imagePic = TabContactsFragment.circleImage[DefaultPicture.ImagePosition];


        groupName.setText("无");
        et_familyName.setText("");
        et_name.setText("");


        toolbar.setTitle("编辑联系人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        group_name_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getCheckBoxDialog(CreatePeopleDetail.this, R.style.Menu, AllGroup, have, mHandler, 102, 103).show();
            }
        });


        mUser = new User();
        number = getIntent().getStringExtra(PhoneDictionary.NUMBER);
        info = new Vector<>();
        info.add(new String[]{"手机",number});
        mPeopleInfoAdapter = new PeopleInfoAdapter(this,R.layout.change_people_detail,info);
        mListView.setAdapter(mPeopleInfoAdapter);

        btn_img.setImageBitmap(imagePic);
        btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getPhotoDialog(CreatePeopleDetail.this, "请选择方法", new String[]{"从相库中选取", "从默认图片选取"}, new MyViewFactory(CreatePeopleDetail.this), btn_img, mHandler).show();
            }
        });


        add_info.setOnClickListener(new View.OnClickListener() {//创建新项目；更新adapter
            @Override
            public void onClick(View v) {
                DialogFactory.getRadioDialog(CreatePeopleDetail.this,R.style.Menu,PhoneDictionary.PhoneCallChoices,mHandler,104).show();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Changed();
                if(mUser.name == null || mUser.name.length() == 0){
                    Toast.makeText(CreatePeopleDetail.this,"名字不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mUser.mobilephone == null || mUser.mobilephone.length() == 0){
                    Toast.makeText(CreatePeopleDetail.this,"号码不可为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mUser.photo == -1)
                    PhotoZoom.saveBitmap(number, imagePic);
                Intent intent = new Intent();
                intent.putExtra(PhoneDictionary.OTHER, mUser);
                setResult(PhoneDictionary.CREATE_PEOPLE_CODE, intent);
                DBHelper dbHelper = new DBHelper(CreatePeopleDetail.this);
                dbHelper.insertAUser(mUser);
                finish();
            }
        });
        btn_return.setOnClickListener(new View.OnClickListener() {
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



    private void Changed(){

        StringBuffer infoTmp = new StringBuffer();
        String[] tt;
        mUser.name = et_name.getText().toString();
        mUser.photo = DefaultPicture.ImagePosition;
        mUser.mobilephone = info.get(0)[1];
        mUser.nickname = et_familyName.getText().toString();
        for(int i = 1 ; i < info.size(); ++i){
            tt = info.get(i);
            if(tt[1] == null || tt[1].equals(""))
                continue;
            if(i > 1)
                infoTmp.append("&&");
            infoTmp.append(tt[0]).append("&&").append(tt[1]);
        }
        mUser.info = infoTmp.toString();
        mUser.sortname = CommonSettingsAndFuncs.convertToShortPinyin(CreatePeopleDetail.this, mUser.name);
        mUser.groupname = groupName.getText().toString();
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
