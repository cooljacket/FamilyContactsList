package hw.happyjacket.com.familycontactlist;
import java.util.HashMap;

//import com.example.menu.MyLetterListView.OnTouchingLetterChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * Created by leo on 2016/3/30.
 */
public class ChangePeopleDetail extends AppCompatActivity {
    ImageButton btn_img;
    AlertDialog imageChooseDialog;
    Gallery gallery;
    ImageSwitcher IS;
    HashMap map;
    int imagePosition;
    EditText et_name;
    EditText et_phone;
    EditText et_home;
    EditText et_email;
    EditText et_remark;
    Button btn_save;
    Button btn_return;
    int imageP;//头像序号
    int imagePic;//头像Rid

    private int[] image = {R.drawable.contact_list_icon,R.drawable.man,R.drawable.woman,
            R.drawable.p1,R.drawable.p2,R.drawable.p3
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){//处理请求
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

//        Toast.makeText(getApplicationContext(), map.get("contactID").toString(), Toast.LENGTH_SHORT).show();
        //头像选择
        btn_img = (ImageButton) this.findViewById(R.id.btn_img);
        imagePic=(int) map.get("contactPhoto");

        btn_img.setImageResource(imagePic);
        btn_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initImageChooseDialog();
                imageChooseDialog.show();
            }
        });
        et_name=(EditText)findViewById(R.id.et_name);
        et_phone=(EditText)findViewById(R.id.et_mobilephone);
        et_home=(EditText)findViewById(R.id.et_homephone);
        et_email=(EditText)findViewById(R.id.et_email);
        et_remark=(EditText)findViewById(R.id.et_remark);
        btn_return=(Button)findViewById(R.id.btn_return);
        btn_save=(Button)findViewById(R.id.btn_save);

        et_name.setText(map.get("contactName").toString());
        et_phone.setText(map.get("contactPhone").toString());
//        et_home.setText(map.get("contactHome").toString());
        et_email.setText(map.get("contactEmail").toString());
        et_remark.setText(map.get("contactRemark").toString());

        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                map = getChanged();
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

    private HashMap getChanged(){
        HashMap newmap=new HashMap();
        newmap.put("contactName",et_name.getText());
        newmap.put("contactPhone",et_phone.getText());
        newmap.put("contactEmail",et_email.getText());
        newmap.put("contactRemark",et_remark.getText());
        newmap.put("contactPhoto",image[imageP]);
        return newmap;
    }


    private void initImageChooseDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("请选择头像");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_img.setImageResource(image[imagePosition]);
                imageP=imagePosition;
                imagePic=image[imageP];
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.head, null);

        gallery = (Gallery) view.findViewById(R.id.img_gallery);
        gallery.setAdapter(new ImageAdapter(this));
        gallery.setSelection(image.length / 2);

        IS = (ImageSwitcher) view.findViewById(R.id.image_switcher);

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IS.setImageResource(image[position]);
                imagePosition=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        IS.setFactory(new MyViewFactory(this));



        builder.setView(view);

        imageChooseDialog=builder.create();
    }

    class ImageAdapter extends BaseAdapter{
        private  Context context;

        public  ImageAdapter(Context context){
            this.context = context;

        }
        @Override
        public int getCount(){
            return image.length;
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0;

        }

        @Override
        public View getView(int positoon,View converView,ViewGroup parent){
            ImageView iv = new ImageView(context);
            iv.setImageResource(image[positoon]);
            iv.setLayoutParams(new Gallery.LayoutParams(200,200));
            iv.setPadding(15,10,15,10);

            return iv;

        }


    }

    class MyViewFactory implements ViewSwitcher.ViewFactory {
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
