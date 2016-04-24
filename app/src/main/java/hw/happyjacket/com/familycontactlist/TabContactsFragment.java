package hw.happyjacket.com.familycontactlist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabContactsFragment extends Fragment {
    private Context mContext;
    private ListView listview;
    private Vector<HashMap<String,Object>> AL;
    private int positionNew;
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private View ContactView;
    private SimpleAdapter adapter;
    private Vector<Integer> AllID = new Vector<>();
    private HashMap<Integer,Integer> IDtoPos = new HashMap<>();
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;
    public static final int PHONES_CONTACT_ID_INDEX=1;
    public static final int PHONES_NUMBER_INDEX=2;
    public static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public Vector<HashMap> contacts = new Vector<>();
    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600 };
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z' };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ContactView == null ? ContactView = inflater.inflate(R.layout.contact_list, container, false) : ContactView;
    }



    private int[] image = {R.drawable.p4,R.drawable.p5,R.drawable.p6,
            R.drawable.p1,R.drawable.p2,R.drawable.p3,
            R.drawable.p7,R.drawable.p8,R.drawable.p9,
            R.drawable.p10,R.drawable.p11,R.drawable.p12,
            R.drawable.p13,R.drawable.p14,R.drawable.p15,
            R.drawable.p16,R.drawable.p17,R.drawable.p18,
            R.drawable.p19,R.drawable.p20,R.drawable.p21,
            R.drawable.p22,R.drawable.p23,R.drawable.p24,
            R.drawable.p25,R.drawable.p26,R.drawable.p27,
            R.drawable.p28,R.drawable.p29,R.drawable.p30,
            R.drawable.p31
    };

    public Bitmap[] circleImage = new Bitmap[31];


    public void notifyDataSetChanged(HashMap<String,Object> data){
        AL.set(positionNew,data);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PhoneDictionary.CONTAT_ACTION_START:
                Log.i("hehe2","hehe2");
                HashMap newmap = (HashMap)data.getSerializableExtra("newdata");
                AL.set(positionNew, newmap);
                Log.i("hehe2",AL.get(positionNew).get("contactName") + "");
                adapter.notifyDataSetChanged();
                break;
            default:
                Log.i("hehe2","hehe3");
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


        AL = getPhoneContacts();


        sortList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < AllID.size() ; ++i){
                    int id = AllID.get(i);
                    int pos = IDtoPos.get(id);
                    contacts.get(pos).put("contactPhoto",PhotoZoom.createCircleImage(PhotoZoom.ratio(id),180));
                }
                Message message = handler.obtainMessage();
                message.what = 0;
                handler.sendMessage(message);

            }
        }).start();

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
        Bitmap a;
        for(int i=0;i<31;i++){
//            a= BitmapFactory.decodeResource(getResources(),image[i]);
            a = PhotoZoom.ratio(getActivity(),image[i]);
            circleImage[i]=PhotoZoom.createCircleImage(a, 180);//ratio(image[i],100,100)
        }
    }



    private void sortList(){
        for(int i=0;i<AL.size();i++){
            for(int j=i+1;j<AL.size();j++){
                HashMap h1 = (HashMap)AL.get(i);
                HashMap h2 = (HashMap)AL.get(j);
                String v1 =(String) h1.get("contactSortname");
                int id1 = (int) h1.get("contactID");
                String v2 =(String) h2.get("contactSortname");
                int id2 = (int) h2.get("contactID");
                if(v1.compareTo(v2)>0){
                    AL.setElementAt(h2,i);
                    AL.setElementAt(h1, j);
                    IDtoPos.put(id2,i);
                    IDtoPos.put(id1,j);
                }
            }
        }
    }



    private  void loadList(){


        //Toast.makeText(getApplicationContext(), ""+num, Toast.LENGTH_SHORT).show();
         adapter = new SimpleAdapter(mContext, AL, R.layout.list_item
                ,new String[]{"contactName","contactPhoto"}
                ,new int[]{R.id.name, R.id.imageView});
//        adapter.
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
                Intent intent = new Intent(mContext, ContactActivity.class);
                positionNew = position;
                HashMap map = (HashMap) parent.getItemAtPosition(position);
                // 当requestCode为3的时候表示请求转向CPD这个页面？？
                ContactActivity.actionStart(getActivity(), map);
            }
        });
    }




    private int num = 0;
    //        /**得到手机通讯录联系人信息**/
    public Vector getPhoneContacts() {

        ContentResolver resolver1 = mContext.getContentResolver();
//        ArrayList contacts = new ArrayList();
        // 获取手机联系人
        Cursor phoneCursor = resolver1.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
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

                String contactPhone = phoneCursor.getString(PHONES_NUMBER_INDEX);

//                Log.i("hh",contactName+" "+contactid);
////                 根据contact_ID取得MobilePhone号码
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
                int contactPhotonum =0;
                Bitmap contactPhoto=circleImage[0];//头像默认的图片
                String contactSortname=getSpells(contactName);
                cursor = db.query("user",null,"cid="+contactID,null,null,null,null);
                if(!cursor.moveToFirst()) {
                    User user = new User();
                    user.cid=contactid;
                    user.name=contactName;
                    user.sortname=contactSortname;
                    user.mobilephone=contactPhone;
//                    user.mobilephone=
//                    user.family=false;
//                    user.mobilephone
//                    user.group="UNKNOWN";
                    Random random = new Random();
                    contactPhoto = circleImage[random.nextInt(31)];
                    PhotoZoom.saveBitmap(contactid,contactPhoto);

                    dbHelper.initUser(user);
//                    Toast.makeText(mContext, "塞进去" + contactName, Toast.LENGTH_SHORT).show();

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


//                map.put("contactGroup", contactGroup);
//                map.put("contactFamily", contactFamily);
//                map.put("contactFamilyname", contactFamilyname);




                // map.put();

                //Log.d("debug",contactName);
            }

            phoneCursor.close();
        }

        Cursor cursor = db.query("user",null,null,null,null,null,null);
        if (cursor != null)
        while(cursor.moveToNext()){
            num++;
            //photo

//                    contactPhotonum = cursor.getInt(2);
            int contactid = cursor.getInt(1);

            AllID.add(contactid);
            IDtoPos.put(contactid,AllID.size() - 1);




            //familyname
            String contactName = cursor.getString(2);
            //group
            String contactSortname = cursor.getString(3);

            HashMap map=new HashMap();
            map.put("contactName",contactName);
//                map.put("contactPhone",phoneNumber);
            map.put("contactSortname",contactSortname);
            map.put("contactID",contactid);
            map.put("UserID",cursor.getInt(0));
            contacts.add(map);
        }

        return contacts;
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