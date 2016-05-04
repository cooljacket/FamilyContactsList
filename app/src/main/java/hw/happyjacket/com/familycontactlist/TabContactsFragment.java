package hw.happyjacket.com.familycontactlist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.TabContactAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabContactsFragment extends Fragment {
    public static final int LIST_LOAD_SOME = 0, LIST_LOAD_OK = 1;
    public static String NAME = "contactName";
    public static String PHOTO = "contactPhoto";
    public static String SORTNAME = "contactSortname";
    public static String DELETE = "contactDelete";
    public static String POS = "contactPos";
    public static final String DataBaseLock = "lock";
    private Context mContext;
    private ListView listview;
    private Vector<HashMap<String,Object> > AL;
    private int positionNew;
    private DBHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private View ContactView;
    private TabContactAdapter adapter;
    private static Bitmap picture;
    private static String SHARE_NAME = "contactSharePreference";
    private static int born;
    private static String The_First_Time = "first";
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;
    public static final int PHONES_CONTACT_ID_INDEX = 1;
    public static final int PHONES_NUMBER_INDEX = 2;
    public static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public Vector<HashMap<String,Object>> contacts = new Vector<>();
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
    public static Bitmap[] circleImage = new Bitmap[31];
    private Thread mThread;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LIST_LOAD_SOME:
                case LIST_LOAD_OK:
                    if (listview == null || adapter == null) {
                        if (adapter == null)
                            adapter = new TabContactAdapter(mContext,R.layout.list_item, AL);
                        if (listview == null)
                            listview = (ListView) getView().findViewById(R.id.list_view);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                positionNew = position;
                                HashMap<String, Object> map = AL.get(position);
                                // 当requestCode为3的时候表示请求转向CPD这个页面？？
                                ContactActivity.actionStart(getActivity(), map);
                            }
                        });
                        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                return true;
                            }
                        });
                    }
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



    public void notifyDataSetChanged(HashMap<String,Object> data){
        if(data.get(PHOTO) == null)
            return;
        HashMap<String,Object> t = AL.get(positionNew);
        Object tmp;
        for(Map.Entry<String,Object> i : t.entrySet()){
            if((tmp = data.get(i.getKey())) != null){
                t.put(i.getKey(),tmp);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void delete(int pos){
        if(0 <= pos && pos < AL.size()){
            AL.remove(pos);
        }
        for(int i = 0 ; i < AL.size(); ++i)
            AL.get(i).put(POS,i);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PhoneDictionary.CONTAT_ACTION_START:
                HashMap newmap = (HashMap)data.getSerializableExtra("newdata");
                AL.set(positionNew, newmap);
                adapter.notifyDataSetChanged();
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper = new DBHelper(mContext);
                db = dbHelper.openDatabase();
                getCircles();
                TheFirstTimeInit();
                AL = getPhoneContacts();
                sortList();
                dbHelper.close();
                loadList();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void getCircles() {
        Bitmap a;
        for(int i=0; i<31; i++){
            a = PhotoZoom.ratio(getActivity(), image[i]);
            circleImage[i] = PhotoZoom.createCircleImage(a, a.getWidth(), a.getHeight());//ratio(image[i],100,100)
        }
    }


    private void sortList(){
        // 选择排序——bad
//        for(int i=0;i<AL.size();i++){
//            String smallest =(String) ((HashMap)AL.get(i)).get("contactSortname");
//            int key_idx = i;
//            for(int j=i+1;j<AL.size();j++){
//                String now =(String) ((HashMap)AL.get(j)).get("contactSortname");
//                if (smallest.compareToIgnoreCase(now) > 0) {
//                    smallest = now;
//                    key_idx = j;
//                }
//            }
//
//            HashMap tmp = AL.get(i);
//            AL.setElementAt(AL.get(key_idx), i);
//            AL.setElementAt(tmp, key_idx);
//        }
        // 使用插入排序，虽然时间复杂度在列表无序时是O(n^2)
        // 但是有利于修改名字时将新的名字插入到已经有序的列表中，时间复杂度为O(n)
        for (int i = 1; i < AL.size(); ++i) {
            HashMap<String,Object> copy =  AL.get(i);
            String now = (String) copy.get("contactSortname");
            int cut_in = i;
            while (--cut_in >= 0) {
                String pre = (String) (AL.get(cut_in)).get("contactSortname");
                if (now.compareToIgnoreCase(pre) >= 0)
                    break;
                AL.setElementAt(AL.get(cut_in), cut_in+1);
            }
            AL.setElementAt(copy, cut_in+1);
        }
        for (int i = 0 ; i < AL.size(); ++i){
            AL.get(i).put(POS,i);
        }
    }



    private void loadList() {
        for(int i = 0 ; i < AL.size() ; ++i) {
            HashMap now = AL.get(i);
            int photo = (int) now.get("photo");
            int id = (int) now.get("contactID");
            if(photo != -1) {
                picture = circleImage[photo];
            }
            else {
                picture = PhotoZoom.getBitmap(id, photo, circleImage);
                picture = PhotoZoom.createCircleImage(picture, picture.getWidth(), picture.getHeight());
            }

            AL.get(i).put("contactPhoto", picture);

            // 有10的整数倍个数据的时候就先显示了，23333
            if (i > 0 && i % 10 == 0) {
                Message message = handler.obtainMessage();
                message.what = LIST_LOAD_SOME;
                handler.sendMessage(message);
            }
        }

        Message message = handler.obtainMessage();
        message.what = LIST_LOAD_OK;
        handler.sendMessage(message);


    }

    public void TheFirstTimeInit(){

        synchronized (DataBaseLock) {
            ContentResolver resolver1 = mContext.getContentResolver();
            SharedPreferences pref = mContext.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            String contactName;
            int contactid;
            String contactID;
            String contactPhone;
            Cursor cursor;
            String contactSortname;
            int first = pref.getInt(The_First_Time, -1);
            if (first == -1) {
                Cursor phoneCursor = resolver1.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
                if (phoneCursor != null) {
                    while (phoneCursor.moveToNext()) {
                        //得到联系人名称
                        contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                        //得到联系人ID
                        contactid = phoneCursor.getInt(PHONES_CONTACT_ID_INDEX);
                        contactID = phoneCursor.getString(PHONES_CONTACT_ID_INDEX);
                        contactPhone = phoneCursor.getString(PHONES_NUMBER_INDEX);
                        contactSortname = CommonSettingsAndFuncs.convertToShortPinyin(mContext, contactName);
                        cursor = db.query("user", null, "cid = " + contactID, null, null, null, null);
                        if (!cursor.moveToFirst()) {
                            User user = new User();
                            user.cid = contactid;
                            user.name = contactName;
                            user.sortname = contactSortname;
                            user.mobilephone = contactPhone.replace(" ","").replace("+86","").replace("+","");
                            user.groupname = "无";
                            user.nickname = "";
                            Random random = new Random();
                            user.photo = random.nextInt(31);
                            dbHelper.insertAUser(user);
                        }
                        cursor.close();
                    }
                    phoneCursor.close();
                }
                editor.putInt(The_First_Time, 1);
                editor.commit();
            }
        }

    }


    //        /**得到手机通讯录联系人信息**/
    public Vector<HashMap<String,Object>> getPhoneContacts() {

        ContentResolver resolver1 = mContext.getContentResolver();
        // 获取手机联系人

        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                do {
                    int contact_id = cursor.getInt(1);
                    int photo_id = cursor.getInt(5);
                    //familyname
                    String contactName = cursor.getString(2);
                    //group
                    String contactSortname = cursor.getString(3);
                    HashMap map = new HashMap();
                    map.put("contactName", contactName);
                    map.put("photo", photo_id);
                    map.put("contactSortname", contactSortname);
                    map.put("contactID", contact_id);
                    map.put("UserID", cursor.getInt(0));
                    contacts.add(map);
                }while (cursor.moveToNext());
            }
        }
        return contacts;
    }

}