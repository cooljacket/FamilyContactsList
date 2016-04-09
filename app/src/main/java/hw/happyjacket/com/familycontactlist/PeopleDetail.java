package hw.happyjacket.com.familycontactlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by leo on 2016/4/3.
 */
public class PeopleDetail extends AppCompatActivity {
    ImageView pic;
    TextView name;
    TextView mobilephone;
    TextView homephone;
    TextView group;
    TextView email;
    TextView remark;
    TextView position;
    HashMap map;
    int imagePic;
    Button btn_modify;
    boolean flag=false;//默认没修改



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
        newmap.put("contactEmail",email.getText());
        newmap.put("contactRemark",remark.getText());
        return newmap;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        //debug
//        Toast.makeText(getApplicationContext(), "11111", Toast.LENGTH_SHORT).show();
        //debug
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        map = (HashMap)intent.getSerializableExtra("data");





        this.setContentView(R.layout.people_detail);

        pic=(ImageView) this.findViewById(R.id.p_img);
        name=(TextView)this.findViewById(R.id.p_name);
        mobilephone=(TextView)this.findViewById(R.id.p_mobilephone);
//        homephone=(TextView)this.findViewById(R.id.p_homephone);
        email=(TextView)this.findViewById(R.id.p_email);
        remark=(TextView)this.findViewById(R.id.p_remark);
//        group=(TextView)this.findViewById(R.id.p_group);

        position=(TextView)this.findViewById(R.id.position);
        showDetail();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){//处理结果码
            case 1://有修改

                map =(HashMap)data.getSerializableExtra("newdata");
                imagePic = (int) map.get("contactPhoto");
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
        //        homephone.setText(map.get("contactHome").toString());
        //        group.setText(map.get("contactGroup").toString());
        email.setText(map.get("contactEmail").toString());
        remark.setText(map.get("contactRemark").toString());

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
