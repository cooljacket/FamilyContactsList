package hw.happyjacket.com.familycontactlist;
import java.util.HashMap;

//import com.example.menu.MyLetterListView.OnTouchingLetterChangedListener;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.ImageAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PeopleInfoAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by leo on 2016/3/30.
 */
public class ChangePeopleDetail extends AppCompatActivity {
    ImageButton btn_img;
    AlertDialog imageChooseDialog;
    ImageSwitcher IS;
    Gallery gallery;

    HashMap map;
    boolean family = false;
    int imagePosition;
//    CheckBox et_family;
    EditText et_familyName;
    EditText et_name;
    EditText et_phone;
    EditText et_home;
    EditText et_email;
    EditText et_work;
    EditText et_group;
    EditText et_remark;
    Button btn_save;
    Button btn_return;
    int imageP;//头像序号
    Bitmap imagePic;//头像Rid
    int imagePP;
    String [][] info;
    ScrollListView mListView;
    PeopleInfoAdapter mPeopleInfoAdapter;

    final String  param [] = {"contactPhone","contactWork","contactHome","contactEmail","contactRemark","contactGroup"};
    final String PeopleInfo[] = {"手机","公司","家庭","Email","备注","群组"};







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){//处理请求

            case PhoneDictionary.IMAGE_REQUEST_CODE:
                PhotoZoom.startPhotoZoom(ChangePeopleDetail.this,data.getData());
                break;
            case PhoneDictionary.RESULT_REQUEST_CODE:
                if(data != null){
                    PhotoZoom.getImageToView(ChangePeopleDetail.this,data,btn_img);
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), requestCode, Toast.LENGTH_SHORT).show();
                map = new HashMap();
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
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        map = (HashMap)intent.getSerializableExtra("data");

        this.setContentView(R.layout.edit_people_detail);

        info = new String[PeopleInfo.length][2];

        for(int i = 0 ; i < PeopleInfo.length ; i++){
            info[i][0] = PeopleInfo[i];
            info[i][1] = map.get(param[i]).toString();
        }

//        Toast.makeText(getApplicationContext(), map.get("contactID").toString(), Toast.LENGTH_SHORT).show();
        //头像选择
        btn_img = (ImageButton) findViewById(R.id.btn_img);

        mListView = (ScrollListView) findViewById(R.id.people_info);
        mPeopleInfoAdapter = new PeopleInfoAdapter(this,R.layout.change_people_detail,info);
        mListView.setAdapter(mPeopleInfoAdapter);

//        imagePic=(int) map.get("contactPhoto");
        DialogFactory.setImagePicture(imagePic = (Bitmap) map.get("contactPhoto"));
        btn_img.setImageBitmap(imagePic);

//        btn_img.setImageResource(imagePic);
        btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getPhotoDialog(ChangePeopleDetail.this, "请选择方法",new String[]{"从相库中选取","从默认图片选取"},new MyViewFactory(ChangePeopleDetail.this),btn_img).show();;

            }
        });


        btn_return=(Button)findViewById(R.id.btn_return);
        btn_save=(Button)findViewById(R.id.btn_save);



       /* et_name.setText(map.get("contactName").toString());
        et_work.setText(map.get("contactWork").toString());
        et_phone.setText(map.get("contactPhone").toString());
        et_home.setText(map.get("contactHome").toString());
        et_email.setText(map.get("contactEmail").toString());
        et_remark.setText(map.get("contactRemark").toString());
        et_group.setText(map.get("contactGroup").toString());*/


//        et_family=(CheckBox)findViewById(R.id.et_family);
        et_familyName=(EditText)findViewById(R.id.et_familyName);

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

        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*changePhoneContact();*/

                for(int i=0;i<5;i++){
                    if(PhoneDictionary.ImageID[i]== DialogFactory.getImagePP()){
                        DialogFactory.setImageP(i);
                        break;
                    }
                }
//                Toast.makeText(getApplicationContext(),"image1  "+imageP
//                        +"image2  "+imagePosition, Toast.LENGTH_SHORT).show();
//                imagePic= btn_img.();


                User user = new User();
                user._id = (int) map.get("contactID");
                user.photo = DialogFactory.getImageP();
                user.family = family;
                if(family){
                    user.familyName=et_familyName.getText().toString();
                    user.group="FAMILY";
                }
                else {
                    user.familyName="NO";
                    user.group="";
                }
                //DBHelper.change(user);
                //!!!!这里！！！！
                DBHelper helper = new DBHelper(ChangePeopleDetail.this);
                helper.openDatabase();
                helper.change(user);
                helper.close();

               /* map = getChanged();*/
                Intent data = new Intent();
                data.putExtra("newdata", map);
                setResult(1, data);
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


    private void changePhoneContact(){
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String contactid = map.get("contactID").toString();
        ContentValues values ;
        ContentResolver resolver = this.getApplicationContext().getContentResolver();
        Toast.makeText(getApplicationContext(),"改改改！！！！", Toast.LENGTH_SHORT).show();

        // 更新Display_name

        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, et_name.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "
                , new String[]{contactid, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});

        // 更新homePhone
        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_home.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
                , new String[]{contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)});

        // 更新workPhone
        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_work.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
                , new String[]{contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)});

        // 更新mobilePhone
        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, et_phone.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? " + " AND "
                        + ContactsContract.CommonDataKinds.Phone.TYPE + "=?"
                , new String[]{contactid, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)});

        // 更新email
//        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                                + ContactsContract.Data.MIMETYPE + "=?" + " AND "
//                                + ContactsContract.CommonDataKinds.Email.TYPE + "=?",
//                        new String[]{contactid, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK)})
//                .withValue(ContactsContract.CommonDataKinds.Email.DATA, et_email.getText())
//                .build());
        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Email.DATA, et_email.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=?"
                , new String[]{contactid, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE});

        // 更新remark
        values = new ContentValues();
        values.put(ContactsContract.CommonDataKinds.Note.NOTE, et_remark.getText().toString());
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "
                , new String[]{contactid, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE});


    }


    private HashMap getChanged(){
        HashMap newmap=new HashMap();
        newmap.put("contactName", et_name.getText().toString());
        newmap.put("contactPhone",et_phone.getText().toString());
        newmap.put("contactEmail",et_email.getText().toString());
        newmap.put("contactHome",et_home.getText().toString());
        newmap.put("contactWork",et_work.getText().toString());
        newmap.put("contactID",map.get("contactID"));
        newmap.put("contactRemark",et_remark.getText().toString());
        newmap.put("contactPhoto", DialogFactory.getImagePicture());
        newmap.put("contactGroup", et_group.getText().toString());
//        newmap.put("contactFamily", family);
        newmap.put("contactFamilyname", et_familyName.getText().toString());
        return newmap;
    }





    private void initImageChooseDialog(){

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
