package hw.happyjacket.com.familycontactlist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabContactsFragment extends Fragment {
    private Context mContext;
    private ListView listview;
    private ArrayList AL;
    private int positionNew;
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private View ContactView;
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;
//    public static final int PHONES_NUMBER_INDEX =1;
//    public static final int PHONES_PHOTO_ID_INDEX=2;
    public static final int PHONES_CONTACT_ID_INDEX=1;
    public static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//            ContactsContract.CommonDataKinds.Phone.NUMBER,
//            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
    public ArrayList contacts = new ArrayList();

//    JSONObject data=new JSONObject();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ContactView == null ? ContactView = inflater.inflate(R.layout.contact_list, container, false) : ContactView;
    }

    private int[] image = {R.drawable.q,R.drawable.man,R.drawable.woman,
            R.drawable.p1,R.drawable.p2,R.drawable.p3
    };

    public Bitmap[] circleImage = new Bitmap[6];


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                HashMap newmap = (HashMap)data.getSerializableExtra("newdata");
                AL.set(positionNew, newmap);
                loadList();
//                SimpleAdapter adapter = new SimpleAdapter(mContext, AL, R.layout.list_item
//                        ,new String[]{"contactName", "contactPhone", "contactPhoto"}
//                        ,new int[]{R.id.name, R.id.number, R.id.imageView});
//                listview.setAdapter(adapter);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        dbHelper = new DBHelper(TabContactsFragment.super.getContext());
        db = dbHelper.openDatabase();
        getCircles();

//        for(int i=0;i<6;i++){
//            circleImage[i] = createCircleImage(a);
//        }

        AL = getPhoneContacts();

        dbHelper.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (listview == null) {
            listview = (ListView) getView().findViewById(R.id.list_view);
            loadList();
        }

//        Button go = (Button) getView().findViewById(R.id.goToCL);
//        go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("android.intent.action.CL");
//                startActivity(intent);
//            }
//        });
    }


    private void getCircles(){

        Bitmap a= BitmapFactory.decodeResource(getResources(),R.drawable.man);
        circleImage[1]=createCircleImage(a,180);
        a= BitmapFactory.decodeResource(getResources(),R.drawable.woman);
        circleImage[2]=createCircleImage(a,180);
        a= BitmapFactory.decodeResource(getResources(),R.drawable.p1);
        circleImage[3]=createCircleImage(a,180);
        a= BitmapFactory.decodeResource(getResources(),R.drawable.p2);
        circleImage[4]=createCircleImage(a,180);
        a= BitmapFactory.decodeResource(getResources(),R.drawable.p3);
        circleImage[5]=createCircleImage(a,180);
        a= BitmapFactory.decodeResource(getResources(),R.drawable.q);
        circleImage[0]=createCircleImage(a,180);

    }

    private Bitmap createCircleImage(Bitmap source,int min){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min,min, Bitmap.Config.ARGB_8888);
        /*
        产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /*
        首先绘制圆形
         */
        canvas.drawCircle(min/2,min/2,min/2,paint);
        /*
        使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /*
        绘制图片
         */
        canvas.drawBitmap(source,0,0,paint);

        return target;
    }


    private  void loadList(){


        //Toast.makeText(getApplicationContext(), ""+num, Toast.LENGTH_SHORT).show();
        SimpleAdapter adapter = new SimpleAdapter(mContext, AL, R.layout.list_item
                ,new String[]{"contactName","contactPhoto"}
                ,new int[]{R.id.name, R.id.imageView});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView  && data instanceof Bitmap ){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap)data);
                    return true;
                }else{
                    return false;
                }
            }
        });
        listview.setAdapter(adapter);


        Toast.makeText(mContext, "" + num, Toast.LENGTH_SHORT).show();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PeopleDetail.class);
//                Toast.makeText(mContext, "aaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                positionNew=position;
                HashMap map = (HashMap) parent.getItemAtPosition(position);
//                SerializableMap tmpmap = new SerializableMap();
//                tmpmap.setMap(map);
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", tmpmap);
//                Toast.makeText(mContext, "bbbbbbbbbbbb", Toast.LENGTH_SHORT).show();
//                intent.putExtras(bundle);
                intent.putExtra("data", map);

                // 当requestCode为3的时候表示请求转向CPD这个页面？？
                startActivityForResult(intent, 3);
            }
        });
    }




    private int num = 0;
    //        /**得到手机通讯录联系人信息**/
    public ArrayList getPhoneContacts() {

        ContentResolver resolver1 = mContext.getContentResolver();
//        ArrayList contacts = new ArrayList();
        // 获取手机联系人
        Cursor phoneCursor = resolver1.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
//                if(++num >= 15) {
////                    num = 0;
//                    break;
//                }
                num++;
//                    String phoneNumber = new String();
//                    String homeNumber = new String();
                //得到手机号码
//                    int index=0;
//                    int num=0;
//                    if(phoneCursor.getCount()>0){
//                        index = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
//                    }
//                    while (phoneCursor.moveToNext()){
//                        if(num==0)phoneNumber = phoneCursor.getString(index);
//                        else homeNumber = phoneCursor.getString(index);
//                        num++;
//                    }
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                int contactid = phoneCursor.getInt(PHONES_CONTACT_ID_INDEX);

                String contactID = phoneCursor.getString(PHONES_CONTACT_ID_INDEX);

//                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);

                // 根据contact_ID取得MobilePhone号码
//                Cursor mobilePhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                        new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                        ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                                + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                                +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                        new String[]{contactID,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)}, null);
//                if(mobilePhoneCur.moveToFirst()){
//                    phoneNumber=mobilePhoneCur.getString(mobilePhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                }
//                mobilePhoneCur.close();


                // 根据contact_ID取得WorkPhone号码
//                String workPhone = new String();
//                Cursor workPhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                        new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                        ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                                + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                                +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                        new String[]{contactID,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK)}, null);
//                if(workPhoneCur.moveToFirst()){
//                    workPhone=workPhoneCur.getString(workPhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                }
//                workPhoneCur.close();

                //debug
//                    Toast.makeText(mContext, "11111", Toast.LENGTH_SHORT).show();
                //debug



                //String Group = new String();
//                String email=new String();
//                String remark = new String();
//
//                //email
//
//                Cursor dataCursor = resolver1.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactid, null, null);
//                while (dataCursor.moveToNext()){
//                    email = dataCursor.getString(dataCursor.getColumnIndex(
//                            ContactsContract.CommonDataKinds.Email.DATA));
//                    break;
//                }


//                dataCursor.close();
//
//                String noteWhere =
//                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
//                                ContactsContract.Data.MIMETYPE + " = ?";
//
//                String[] noteWhereParams = new String[]{
//                        ""+contactid,
//                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//
//                Cursor noteCursor = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                        null,
//                        noteWhere,
//                        noteWhereParams,
//                        null);
//                if (noteCursor.moveToFirst()) {
//                    remark = noteCursor.getString(noteCursor.getColumnIndex(
//                            ContactsContract.CommonDataKinds.Note.NOTE));
//                }
//
//                noteCursor.close();

                //debug
//                    Toast.makeText(mContext, "3333", Toast.LENGTH_SHORT).show();
                //debug

                //得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//
//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if(photoid > 0 ) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);//contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver1, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                }else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.q);
//                }
                //photo
                Cursor cursor;
                Bitmap contactPhoto=circleImage[0];//头像默认的图片
                boolean contactFamily=false;
                String contactFamilyname="NO";
                String contactGroup="UNKNOWN";
                cursor = db.query("user",null,"uid="+contactID,null,null,null,null);

                if(!cursor.moveToFirst()){
                    User user = new User();
                    user._id=contactid;
                    user.family=false;
                    user.group="UNKNOWN";
                    user.photo=0;
                    user.familyName="NO";
                    dbHelper.init(user);
//                    Toast.makeText(mContext, "塞进去", Toast.LENGTH_SHORT).show();
                }else{
//
                        //photo
                        contactPhoto = circleImage[cursor.getInt(2)];
                        //是否family
                        contactFamily = cursor.getInt(1)>0;
                        //familyname
                        contactFamilyname = cursor.getString(4);
                        //group
                        contactGroup = cursor.getString(3);
//                        Toast.makeText(mContext, "数据库没出问题" +
//                                "contactPhoto"+contactPhoto
//                                +"contactFamily"+contactFamily+
//                                "contactFamilyname"+contactFamilyname, Toast.LENGTH_SHORT).show();

                }

                cursor.close();

//
//
//                Cursor cursor2 = db.query("user",null,"uid="+contactID,null,null,null,null);
//
//
//                cursor2.close();

                // 根据contact_ID取得HomePhone号码
//                String homeNumber=new String();
//                Cursor homePhoneCur = resolver1.query(ContactsContract.Data.CONTENT_URI,
//                        new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
//                        ContactsContract.Data.CONTACT_ID + "=?" + " AND "
//                                + ContactsContract.Data.MIMETYPE + "=? "+" AND "
//                                +ContactsContract.CommonDataKinds.Phone.TYPE + "=?",
//                        new String[]{contactID,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)}, null);
//                if(homePhoneCur.moveToFirst()){
//                    homeNumber=homePhoneCur.getString(homePhoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                }
//                homePhoneCur.close();

                HashMap map=new HashMap();
                map.put("contactName",contactName);
//                map.put("contactPhone",phoneNumber);
                map.put("contactPhoto",contactPhoto);
                map.put("contactID",contactid);
                map.put("contactGroup", contactGroup);
                map.put("contactFamily", contactFamily);
                map.put("contactFamilyname", contactFamilyname);




                // map.put();
                contacts.add(map);
                //Log.d("debug",contactName);
            }

            phoneCursor.close();
        }
        return contacts;
    }
}