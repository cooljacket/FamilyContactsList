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
    Button btn_modify;




    @Override
    public void onCreate(Bundle savedInstanceState){
        //debug
        Toast.makeText(getApplicationContext(), "11111", Toast.LENGTH_SHORT).show();
        //debug
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
//        Bundle bundle = getIntent().getExtras();
//        SerializableMap sM = (SerializableMap) bundle.get("data");
//        map = (HashMap)sM.getMap();

        map = (HashMap)intent.getSerializableExtra("data");
//        Bundle extras = intent.getExtra("json");
//        String jsonS = extras.getString("JSON");




        this.setContentView(R.layout.people_detail);
        position=(TextView)this.findViewById(R.id.position);
        showDetail();


    }

    private void showDetail(){

        //查询归属地





//debug
                    Toast.makeText(getApplicationContext(), "22222", Toast.LENGTH_SHORT).show();
        //debug
        pic=(ImageView) this.findViewById(R.id.p_img);
        pic.setImageBitmap((Bitmap) map.get("contactPhoto"));
        name=(TextView)this.findViewById(R.id.p_name);
        name.setText(map.get("contactName").toString());
        mobilephone=(TextView)this.findViewById(R.id.p_mobilephone);
        mobilephone.setText(map.get("contactPhone").toString());
//        homephone=(TextView)this.findViewById(R.id.p_homephone);
//        homephone.setText(map.get("contactHome").toString());
        email=(TextView)this.findViewById(R.id.p_email);
        email.setText(map.get("contactEmail").toString());
        remark=(TextView)this.findViewById(R.id.p_remark);
        remark.setText(map.get("contactRemark").toString());
        group=(TextView)this.findViewById(R.id.p_group);
//        group.setText(map.get("contactGroup").toString());
        btn_modify=(Button)this.findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PeopleDetail.this, ChangePeopleDetail.class);

                //debug
                Long ID = (Long) map.get("contactID");
                Toast.makeText(getApplicationContext(), ID.toString(), Toast.LENGTH_SHORT).show();
                //debug


                intent.putExtra("data", map);
//                intent.setClass(PeopleDetail.this, ChangePeopleDetail.class);
                startActivity(intent);

            }
        });

    }



}
