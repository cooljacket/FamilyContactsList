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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

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

    ImageButton btn_img;
    public static HashMap<String,Object> map;
    int imagePosition;
    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };

    EditText et_familyName;
    EditText et_name;
    Button btn_save;
    Button btn_return;
    Bitmap imagePic;//头像Rid
    String [][] info;
    ScrollListView mListView;
    PeopleInfoAdapter mPeopleInfoAdapter;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    imagePic = DialogFactory.getImagePicture();
                    map.put(PhoneDictionary.Photo,DialogFactory.getImagePosition());
                    break;
                case 1:
                    info[0][0] = PhoneDictionary.PhoneCallChoices[msg.arg1];
                    mPeopleInfoAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    final String  param [] = {"contactPhone","contactWork","contactHome","contactEmail","contactRemark","contactGroup"};
    final String PeopleInfo[] = {"手机","公司","家庭","邮箱","备注","群组"};

    String Group[] = {};//存可选的群组






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
                    DialogFactory.setImageP(-1);
                    btn_img.setImageBitmap(imagePic);
                    map.put(PhoneDictionary.Photo,-1);
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
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        map = (HashMap)intent.getSerializableExtra("data");

        this.setContentView(R.layout.edit_people_detail);

        getUser();

//        info = new String[PeopleInfo.length][2];
//
//        for(int i = 0 ; i < PeopleInfo.length ; i++){
//            info[i][0] = PeopleInfo[i];
//            info[i][1] = map.get(param[i]).toString();
//        }

//        Toast.makeText(getApplicationContext(), map.get("contactID").toString(), Toast.LENGTH_SHORT).show();
        //头像选择
        btn_img = (ImageButton) findViewById(R.id.btn_img);

        mListView = (ScrollListView) findViewById(R.id.people_info);
//        imagePic=(int) map.get("contactPhoto");
        Log.i("detail",(int)map.get(PhoneDictionary.Photo) + "");
        DialogFactory.setImagePicture(imagePic = PhotoZoom.getBitmap((int)map.get("contactID"),(int)map.get(PhoneDictionary.Photo),TabContactsFragment.circleImage));
        btn_img.setImageBitmap(imagePic);

//        btn_img.setImageResource(imagePic);
        btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.getPhotoDialog(ChangePeopleDetail.this, "请选择方法",new String[]{"从相库中选取","从默认图片选取"},new MyViewFactory(ChangePeopleDetail.this),btn_img,mHandler).show();
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
        et_name = (EditText) findViewById(R.id.et_name);
        et_familyName=(EditText)findViewById(R.id.et_familyName);
        et_name.setText(map.get("contactName").toString());

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
                updateUser(getChanged());

               /* map = getChanged();*/
                Intent intent = new Intent();
                int tmp;
                HashMap<String,Object> t = new HashMap<String, Object>();
                t.put(PhoneDictionary.NAME, et_name.getText().toString());
                t.put(PhoneDictionary.NUMBER, mPeopleInfoAdapter.getItem(0)[1]);
                t.put(PhoneDictionary.Photo,tmp = (int)map.get(PhoneDictionary.Photo));
                t.put(PhoneDictionary.Picture, imagePic);
                intent.putExtra(PhoneDictionary.OTHER, t);
                setResult(PhoneDictionary.CONTACT_REQUEST_CODE, intent);
                if(tmp == -1)
                    PhotoZoom.saveBitmap((int) map.get("contactID"), imagePic);
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

    private void updateUser(HashMap map){
        DBHelper helper =new DBHelper(ChangePeopleDetail.this.getApplicationContext());
        SQLiteDatabase db =helper.openDatabase();

        StringBuffer sb=new StringBuffer();
        for(int i=1;i<mListView.getCount()-1;i++){
            String[] ss =(String[]) mListView.getItemAtPosition(i);
            sb.append(ss[0]).append(";").append(ss[1]).append(";");
        }


        User user = new User();
        user.cid = (int) map.get("contactID");
        user.uid = (int) map.get("UserID");
        user.photo = DialogFactory.getImageP();
        String s1 = mPeopleInfoAdapter.getItem(0)[1];
//        String[] s1 =(String[]) mListView.getItemAtPosition(0);
        //sb.append(ss[0]).append(";").append(ss[1]).append(";");
        user.mobilephone=s1;

        user.name=et_name.getText().toString();
        user.sortname=getSpells(user.name);

//        String[] s2 =(String[]) mListView.getItemAtPosition(mListView.getCount()-1);
//        mPeopleInfoAdapter.notifyDataSetChanged();
        String s2 = mPeopleInfoAdapter.getItem(1)[1];
        user.groupname=s2;
        Toast.makeText(getApplicationContext(),s1+" "+s2,Toast.LENGTH_SHORT).show();
        user.info=sb.toString();
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

        boolean hadgroup=false;
        for(int i=0;i<Group.length;i++){
            if(user.groupname==Group[i]){
                hadgroup=true;
            }
        }
//        DBHelper dbHelper = null;
        if(!hadgroup){//新加入一个group给grouptable
            Cursor cursor;


            cursor = db.query("grouptable",null,"groupname='"+user.groupname+"'",null,null,null,null);
            if(cursor==null) {//若没有这个组
                Group group=new Group();
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
        helper.close();
    }


    private void getUser(){
        DBHelper helper =new DBHelper(ChangePeopleDetail.this.getApplicationContext());
        SQLiteDatabase db =helper.openDatabase();
        int Userid = (int) map.get("UserID");
        Cursor cursor = db.query("user",null,"uid="+Userid,null,null,null,null);
        //String[][] info;
        String[] param;
        String[] paramName;
        ArrayList list1=new ArrayList();
        ArrayList list2=new ArrayList();
        int photo=0;
        String infos="";
        String groupname="NO",mobile="";
//        Toast.makeText(getApplicationContext(),"get"+Userid, Toast.LENGTH_SHORT).show();
        if(cursor.moveToFirst()){
            infos = cursor.getString(7);
            groupname = cursor.getString(6);
            photo = cursor.getInt(3);//头像int
            mobile = cursor.getString(4);

        }
        cursor.close();
        list1.add(mobile);
        list2.add("手机");
        //String infos2=null;
        if(infos!=null)
        for(int i=0;i<infos.length();i++){
            int index = infos.indexOf(";");
            list2.add(infos.substring(0,index-1));//name
            infos = infos.substring(index+1);
            index = infos.indexOf(";");
            list1.add(infos.substring(0,index-1));//param
            infos = infos.substring(index+1);
            if(infos.length()==1){
                break;
            }
        }
        list1.add(groupname);
        list2.add("组群");
        int size = list1.size();
        param = (String[])list1.toArray(new String[size]);
        paramName=(String[])list2.toArray(new String[size]);

        info = new String[param.length][2];

        for(int i = 0 ; i < param.length ; i++){
            info[i][0] = paramName[i];
            info[i][1] = param[i];
        }
        helper.close();
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

    private HashMap getChanged(){
        HashMap newmap=new HashMap();
        newmap.put("contactName", et_name.getText().toString());
//        newmap.put("contactPhone",et_phone.getText().toString());
        newmap.put("UserID",map.get("UserID"));
        newmap.put("contactID", map.get("contactID"));
        newmap.put("photo", DialogFactory.getImagePosition());
        return newmap;
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

    public static String getSpells(String characters) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {

            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    // 获取一个汉字的首字母
    public static Character getFirstLetter(char ch) {

        byte[] uniCode = null;
        try {
            uniCode = String.valueOf(ch).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (uniCode[0] < 128 && uniCode[0] > 0) { // 非汉字
            return null;
        } else {
            return convert(uniCode);
        }
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }



}

