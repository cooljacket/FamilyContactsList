package hw.happyjacket.com.familycontactlist;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by leo on 2016/4/3.
 */
public class PeopleDetail extends AppCompatActivity {
    ImageView pic;
    TextView name;
    TextView mobilephone;
    TextView homephone;
    TextView workPhone;
    TextView email;
    TextView remark;
//    TextView position;
    TextView group;
    CheckBox family;
    TextView familyname;
    boolean isfamily;
    HashMap map;
    int imagePic;//头像
    Button btn_modify;
    boolean flag=false;//默认没修改

    String contactHome= new String();
    String contactWork= new String();
    String contactRemark= new String();
    String contactEmail= new String();



    @Override
    public void onBackPressed() {
        map = getChanged();
        Intent data = new Intent();
        data.putExtra("newdata", map);
        setResult(1, data);//不管有没修改都要更新数据
        finish();
        super.onBackPressed();
    }

    private HashMap getChanged(){
        HashMap newmap=new HashMap();
        newmap.put("contactName", name.getText());
        newmap.put("contactPhone", mobilephone.getText());
        newmap.put("contactPhoto",imagePic);
        newmap.put("contactID", map.get("contactID"));
        newmap.put("contactGroup", group.getText());
        newmap.put("contactFamily", isfamily);
        newmap.put("contactFamilyname", familyname.getText());
        return newmap;
    }

    private void getOtherDetail(){//从id查其他数据
        int contactid = (int)map.get("contactID");
        ContentResolver resolver1 = this.getApplicationContext().getContentResolver();
        //home
        Cursor homePhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
                        + ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
                new String[]{""+contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)}, null);

        if (homePhoneCur.moveToFirst()) {
            contactHome = homePhoneCur.getString(homePhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        homePhoneCur.close();
        // work
        Cursor workPhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Data.CONTACT_ID + "=?" + " AND "
                        + ContactsContract.Data.MIMETYPE + "=? "+" AND "
                        +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
                new String[]{""+contactid,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)}, null);
        if(workPhoneCur.moveToFirst()){
            contactWork=workPhoneCur.getString(workPhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        workPhoneCur.close();

        //email

        Cursor dataCursor = resolver1.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactid, null, null);
        while (dataCursor.moveToNext()){
            contactEmail = dataCursor.getString(dataCursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Email.DATA));
            break;
        }
        dataCursor.close();
        //remark
        String noteWhere =
                ContactsContract.Data.CONTACT_ID + " = ? AND " +
                        ContactsContract.Data.MIMETYPE + " = ?";

        String[] noteWhereParams = new String[]{
                ""+contactid,
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

        Cursor noteCursor = resolver1.query(ContactsContract.Data.CONTENT_URI,
                null,
                noteWhere,
                noteWhereParams,
                null);
        if (noteCursor.moveToFirst()) {
            contactRemark = noteCursor.getString(noteCursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Note.NOTE));
        }

        noteCursor.close();



        //补充进map
        map.put("contactHome",contactHome);
        map.put("contactWork", contactWork);
        map.put("contactRemark", contactRemark);
        map.put("contactEmail", contactEmail);
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        //debug
//        Toast.makeText(getApplicationContext(), "11111", Toast.LENGTH_SHORT).show();
        //debug
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        map = (HashMap)intent.getSerializableExtra("data");
        getOtherDetail();





        this.setContentView(R.layout.people_detail);

        pic=(ImageView) this.findViewById(R.id.p_img);
        name=(TextView)this.findViewById(R.id.p_name);
        mobilephone=(TextView)this.findViewById(R.id.p_mobilephone);
        homephone=(TextView)this.findViewById(R.id.p_homephone);
        email=(TextView)this.findViewById(R.id.p_email);
        remark=(TextView)this.findViewById(R.id.p_remark);
        workPhone=(TextView)this.findViewById(R.id.p_workphone);
        group=(TextView)this.findViewById(R.id.p_group);
        family=(CheckBox)this.findViewById(R.id.p_family);
        familyname=(TextView)this.findViewById(R.id.p_familyname);
//        position=(TextView)this.findViewById(R.id.position);
        showDetail();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){//处理结果码
            case 1://有修改

                map =(HashMap)data.getSerializableExtra("newdata");
//                imagePic = (int) map.get("contactPhoto");
//                isfamily=(boolean)map.get("contactFamily");

                flag=true;
                showDetail();
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDetail(){

        //查询归属地

        //天气



//debug
//                    Toast.makeText(getApplicationContext(), "22222", Toast.LENGTH_SHORT).show();
        //debug


        imagePic=(int) map.get("contactPhoto");
        pic.setImageResource(imagePic);
//        pic.setImageBitmap((Bitmap) map.get("contactPhoto"));

        name.setText(map.get("contactName").toString());
        mobilephone.setText(map.get("contactPhone").toString());
        Log.d("hehe", "" + map.get("cantactHome"));
        homephone.setText(map.get("contactHome").toString());
        workPhone.setText(map.get("contactWork").toString());
        email.setText(map.get("contactEmail").toString());
        remark.setText(map.get("contactRemark").toString());
        group.setText(map.get("contactGroup").toString());
        isfamily = (boolean)map.get("contactFamily");
        family.setEnabled(false);
        familyname.setText(map.get("contactFamilyname").toString());
        if(isfamily){

            family.setChecked(true);
        }
        else {
            family.setChecked(false);
        }


        Toast.makeText(getApplicationContext(),"啊啊啊debug", Toast.LENGTH_SHORT).show();


        btn_modify=(Button)this.findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PeopleDetail.this, ChangePeopleDetail.class);


                intent.putExtra("data", map);
                // 当requestCode为1的时候表示请求转向CPD这个页面？？

                startActivityForResult(intent, 10);

            }
        });

    }



}
