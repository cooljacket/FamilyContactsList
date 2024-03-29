package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.LightingColorFilter;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.CommonUtils;
import hw.happyjacket.com.familycontactlist.DBHelper;
import hw.happyjacket.com.familycontactlist.Group;
import hw.happyjacket.com.familycontactlist.MainActivity;
import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.DefaultPicture;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.CheckBoxAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.ImageAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.RadioAdapter;
import hw.happyjacket.com.familycontactlist.myphonebook.listview.ScrollListView;
import hw.happyjacket.com.familycontactlist.myphonebook.option.CallLogDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.ContentDialog2;
import hw.happyjacket.com.familycontactlist.myphonebook.option.DefaultDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.option.MainDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.MainShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-11.
 */
public class DialogFactory {



    public static String Number = "";

    public static String getNumber() {
        return Number;
    }

    public static void setNumber(String number) {
        Number = number;
    }



    public static PhoneDialog getPhoneDialog(Context context, int layout, int id,int style, final int index, String content[], int status){
        View viewForOption = LayoutInflater.from(context).inflate(layout,null);
        ListView OptionListView = (ListView)viewForOption.findViewById(id);
        PhoneDialog option = null;
        switch (status){
            case 0:
                option = new MainDialog(context,style,index);
                break;
            case 1:
                option = new ContentDialog(context,style,index);
                break;
            case 2:
                option = new ContentDialog2(context,style,index);
                break;
            case 3:
                option = new CallLogDialog(context,style,index);
                break;
            default:
                break;
        }
        Vector<String> data = new Vector<>();
        for(String i : content)
            data.add(i);
        ArrayAdapter<String> t = new ArrayAdapter<String>(context,android.R.layout.simple_expandable_list_item_1,data);
        OptionListView.setAdapter(t);
        OptionListView.setOnItemClickListener(option);
        option.setContentView(viewForOption);
        Window window = option.getWindow();
        window.setGravity(Gravity.BOTTOM);
        return option;
    }

    public static PhoneDialog getRadioDialog(Context context, int style, String content[], final Handler handler, final int msgIndex){

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option2,null);
        final RadioAdapter radioAdapter = new RadioAdapter(context,R.layout.dialog_radio,content);
        ScrollListView listView = (ScrollListView) view.findViewById(R.id.dialog_option2_listview);
        listView.setAdapter(radioAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                radioAdapter.setIndex(position);
                radioAdapter.notifyDataSetChanged();
            }
        });
        final DefaultDialog defaultDialog = new DefaultDialog(context,style,0);
        defaultDialog.setContentView(view);
        Button positive = (Button) view.findViewById(R.id.dialog_option2_positive);
        Button negative = (Button) view.findViewById(R.id.dialog_option2_negative);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                message.what = msgIndex;
                message.arg1 = radioAdapter.getIndex();
                handler.sendMessage(message);
                defaultDialog.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultDialog.dismiss();
            }
        });
        return defaultDialog;
    }

    public static PhoneDialog getCheckBoxDialog(final Activity context, int style, final List<String> content, String [] have,final Handler handler,final int index){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_option,null);
        final CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(context,R.layout.dialog_checkbox,content,have);
        final DBHelper dbHelper = new DBHelper(context);
        ScrollListView listView = (ScrollListView) view.findViewById(R.id.dialog_option_listview);
        listView.setAdapter(checkBoxAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkBoxAdapter.changeCheck(position);
                checkBoxAdapter.notifyDataSetChanged();
            }
        });
        final DefaultDialog defaultDialog = new DefaultDialog(context,style,0);
        defaultDialog.setContentView(view);
        Button positive = (Button) view.findViewById(R.id.dialog_option_positive);
        Button negative = (Button) view.findViewById(R.id.dialog_option_negative);
        Button addNewOption = (Button) view.findViewById(R.id.new_dialog_option);
        Button delete = (Button) view.findViewById(R.id.dialog_option_delete);
        addNewOption.setText("新建群组");
        final Handler sHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        boolean pass = true;
                        String t = (String) msg.obj;
                        for (String i : content) {
                            if (i.equals(t)) {
                                WarningDialog(context,"该群组已存在").show();
                                pass = false;
                                break;
                            }
                        }
                        if (!pass)
                            break;
                        content.add(t);
                        checkBoxAdapter.addCheck();
                        checkBoxAdapter.notifyDataSetChanged();
                        dbHelper.initGroup(new Group(t,0));
                        break;
                    default:
                        break;
                }
            }
        };


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = handler.obtainMessage();
                message.what = index;
                message.obj = checkBoxAdapter.getIsChecked();
                handler.sendMessage(message);
                defaultDialog.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultDialog.dismiss();
            }
        });


        addNewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog(context,"请输入组名",sHandler,0).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vector<Boolean> t = checkBoxAdapter.getIsChecked();
                int i = 0;
                while (i < t.size()){
                    if(t.get(i)) {
                        dbHelper.deleteGroup(content.get(i));
                        content.remove(i);
                        t.remove(i);
                        continue;
                    }
                    ++i;
                }
                checkBoxAdapter.notifyDataSetChanged();

            }
        });


        return defaultDialog;

    }

    public static AlertDialog InputDialog(final Activity context,String title, final Handler handler, final int index){
        final EditText editText = new EditText(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = handler.obtainMessage();
                msg.what = index;
                msg.obj = editText.getText().toString();
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }



    public static AlertDialog WarningDialog(final Activity context, String warn){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(warn);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        });
        return  builder.create();
    }

    public static AlertDialog JudgeDialog(final  Activity context, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return  builder.create();
    }

    public static AlertDialog DeleteDialog (final Activity context, final String number, final int pos) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否删除该联系人?");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHelper mDBHelper = new DBHelper(context);
                mDBHelper.deleteUser(number);
                MainActivity.deletePeopleDetai(number,pos);
                context.finish();
                dialog.dismiss();
            }
        });
        return builder.create();
    }


    public static AlertDialog getPhotoDialog(final Activity context, String title, final String [] content, final ViewSwitcher.ViewFactory factory, final ImageButton btn_img, final Handler handler){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        context.startActivityForResult(intent, PhoneDictionary.IMAGE_REQUEST_CODE);
                        break;
                    case 1:
                        DialogFactory.getImageDialog(context,"请选择图片",factory,btn_img,handler).show();
                        break;
                    default:
                        break;

                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

     return builder.create();
    }

    public static AlertDialog getImageDialog(final Activity context, String title, ViewSwitcher.ViewFactory factory, final ImageButton btn_img, final Handler handler){

        final Gallery gallery;
        final ImageSwitcher IS;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.head, null);
        gallery = (Gallery) view.findViewById(R.id.img_gallery);
        IS = (ImageSwitcher) view.findViewById(R.id.image_switcher);

        final int [] image = DefaultPicture.ImageID;

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("请选择头像");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bitmap a= PhotoZoom.ratio(context,image[DefaultPicture.ImagePosition]);
                Bitmap circleImage=PhotoZoom.createCircleImage(a, a.getWidth(),a.getHeight());
                btn_img.setImageBitmap(circleImage);
                DefaultPicture.ImagePicture = circleImage;
                DefaultPicture.ImageP = DefaultPicture.ImagePosition;
                DefaultPicture.ImagePP = image[DefaultPicture.ImagePosition];
                Message message = handler.obtainMessage();
                message.what = 100;
                message.obj = DefaultPicture.ImagePicture;
                handler.sendMessage(message);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        gallery.setAdapter(new ImageAdapter(context, image));
        gallery.setSelection(image.length / 2);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IS.setImageResource(image[position]);
                DefaultPicture.ImagePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        IS.setFactory(factory);
        builder.setView(view);
        return builder.create();
    }

    public static PhoneDialog DiaWheel(final Activity context,int style, final MainShow mainShow){

        Vector<HashMap<String,Object>> imageItem = new Vector<>();
        final PhoneDialog phoneDialog = new DefaultDialog(context,style);
        for(int i = 0 ; i < PhoneDictionary.PhoneCallNumber.length ; ++i){
            HashMap<String,Object> t = new HashMap<>();
            t.put(PhoneDictionary.DiaWheelNumber,PhoneDictionary.PhoneCallNumber[i]);
            t.put(PhoneDictionary.DialogAlphabet,PhoneDictionary.PhoneCallAlphabet[i]);
            imageItem.add(t);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dia_wheel,null);
        final EditText editText = (EditText) view.findViewById(R.id.dia_wheel_screen);
        ImageButton retrive = (ImageButton) view.findViewById(R.id.dia_wheel_retrieve);
        ImageButton call = (ImageButton) view.findViewById(R.id.dia_wheel_call);
        final ImageButton delete = (ImageButton) view.findViewById(R.id.dia_wheel_delete);
        call.setImageResource(R.drawable.ic_menu_call);
        call.setColorFilter(new LightingColorFilter(0,0xffffff));
        editText.clearFocus();
        editText.setInputType(InputType.TYPE_NULL);
        GridView gridView = (GridView)view.findViewById(R.id.dia_wheel_table);
        SimpleAdapter simpleAdapter = new SimpleAdapter(context,imageItem,R.layout.dia_wheel_detail,new String[] {PhoneDictionary.DiaWheelNumber,PhoneDictionary.DialogAlphabet}, new int[]{R.id.dia_wheel_number,R.id.dia_wheel_alphabet});
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(editText.getText() + PhoneDictionary.PhoneCallNumber[position]);
            }
        });

        retrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneDialog.dismiss();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation.call(editText.getText().toString());
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t;
                t = editText.getText().toString();
                if (t.isEmpty())
                    return;
                editText.setText(t.substring(0, t.length() - 1));
            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editText.setText("");
                return true;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString();
                if(Number.equals(t))
                    return;
                Number = t;
                Vector<Integer> key = new Vector<Integer>();
                Vector<Integer> pos = CommonUtils.SearchAmongRecords(MainActivity.phoneElement, Number,key);
                mainShow.notifyDataSetChanged(pos,key);
            }
        });
        editText.setText(Number);
        phoneDialog.setContentView(view);
        phoneDialog.setCanceledOnTouchOutside(true);
        Window window = phoneDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams p = window.getAttributes();
        WindowManager windowManager = context.getWindowManager();
        Display d = windowManager.getDefaultDisplay();
        p.height = (int)(d.getHeight() * 0.54);
        p.width = (int)(d.getWidth() * 1.0);
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.0f; // 设置黑暗度
        window.setAttributes(p);
        return phoneDialog;
    }
}
