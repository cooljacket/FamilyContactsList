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
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Random;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.myphonebook.DefaultPicture;
import hw.happyjacket.com.familycontactlist.myphonebook.PhotoZoom;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.TabContactAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabContactsFragment extends PhoneFragment {
    public static final int LIST_LOAD_SOME = 0, LIST_LOAD_OK = 1;
    public static String NAME = "contactName";
    public static String PHOTO = "contactPhoto";
    public static String SORTNAME = "contactSortname";
    public static String DELETE = "contactDelete";
    public static String POS = "contactPos";
    public static String CARGO = "cargo";
    public static String NUMBER = "number";

    public static final String DataBaseLock = "lock";
    private Button search;
    private SearchView mSearchView;
    private Context mContext;
    private ListView listview;
    private Vector<User> ALBacckUp = new Vector<>();
    private Vector<TabContactUser> AL = new Vector<>();
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
    public static Bitmap[] circleImage = new Bitmap[31];
    private Thread mThread;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LIST_LOAD_SOME:
                case LIST_LOAD_OK:
                    mSearchView = (SearchView) getView().findViewById(R.id.search_view);
                    mSearchView.setIconifiedByDefault(true);
                    mSearchView.onActionViewExpanded();
                    mSearchView.setFocusable(false);
                    mSearchView.clearFocus();
                    mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            Vector<Integer> chosen = CommonUtils.SearchAmongContacts(ALBacckUp, newText);
                            AL.removeAllElements();
                            for (int i = 0 ; i < chosen.size() ; ++i){
                                AL.add((TabContactUser)ALBacckUp.get(chosen.get(i)));
                            }
                            adapter.notifyDataSetChanged();
                            return true;
                        }
                    });
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
                                TabContactUser user = AL.get(position);
                                // 当requestCode为3的时候表示请求转向CPD这个页面？？
                                ContactActivity.actionStart(getActivity(), user);
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



    public void notifyDataSetChanged(User data, Bitmap newPicture){
        if(newPicture == null)
            return;
        TabContactUser t = AL.get(positionNew);
        t.update(data);
        t.picture = newPicture;
        adapter.notifyDataSetChanged();
    }

    public void delete(int pos){
        if(0 <= pos && pos < AL.size()){
            AL.remove(pos);
        }
        for(int i = 0 ; i < AL.size(); ++i)
            AL.get(i).pos = i;
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//useless
        switch (requestCode){
            case PhoneDictionary.CONTAT_ACTION_START:
                Toast.makeText(this.getContext(),"debug "+10, Toast.LENGTH_SHORT).show();
                boolean deleted = data.getBooleanExtra(TabContactsFragment.DELETE, false);
                Toast.makeText(this.getContext(),"debug d "+deleted, Toast.LENGTH_SHORT).show();
                if(deleted){
                    delete(positionNew);
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper = new DBHelper(mContext);
                db = dbHelper.openDatabase();
                TheFirstTimeInit();
                getCircles();
                getPhoneContacts();
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
            a = PhotoZoom.ratio(getActivity(), DefaultPicture.ImageID[i]);
            circleImage[i] = PhotoZoom.createCircleImage(a, a.getWidth(), a.getHeight());//ratio(image[i],100,100)
        }
    }

    private void sortList(){
        // 使用插入排序，虽然时间复杂度在列表无序时是O(n^2)
        // 但是有利于修改名字时将新的名字插入到已经有序的列表中，时间复杂度为O(n)
        for (int i = 1; i < AL.size(); ++i) {
            TabContactUser  copy =  AL.get(i);
            String now = copy.sortname;
            int cut_in = i;
            while (--cut_in >= 0) {
                String pre = (AL.get(cut_in)).sortname;
                if (now.compareToIgnoreCase(pre) >= 0)
                    break;
                AL.setElementAt(AL.get(cut_in), cut_in+1);
            }
            AL.setElementAt(copy, cut_in+1);
        }

        for (int i = 0 ; i < AL.size(); ++i){
            AL.get(i).pos = i;

        }
        ALBacckUp.removeAllElements();
        ALBacckUp.addAll(AL);
    }

    private void loadList() {
        for(int i = 0 ; i < AL.size() ; ++i) {
            User now = AL.get(i);
            int photo = now.photo;
            String number = now.mobilephone;
            if(photo != -1) {
                picture = circleImage[photo];
            }
            else {
                picture = PhotoZoom.getBitmap(number, photo, circleImage);
                picture = PhotoZoom.createCircleImage(picture, picture.getWidth(), picture.getHeight());
            }

            AL.get(i).picture = picture;

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
            Vector<Integer> v = new Vector<>();
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
                        contactPhone = phoneCursor.getString(PHONES_NUMBER_INDEX).replace(" ","").replace("+86","").replace("+", "");
                        contactSortname = CommonUtils.convertToShortPinyin(mContext, contactName);
                        contactid = phoneCursor.getInt(PHONES_CONTACT_ID_INDEX);
                        if(!v.contains(contactid)){
                            User user = new User();
                            user.name = contactName;
                            user.sortname = contactSortname;
                            user.mobilephone = contactPhone;
                            user.groupname = "无";
                            user.nickname = "";
                            v.add(contactid);
                            Random random = new Random();
                            user.photo = random.nextInt(31);
                            dbHelper.insertAUser(user);
                        }
//                        cursor = db.query(DBHelper.DB_TABLENAME, null, DBHelper.CONTACTID + " = " + contactid, null, null, null, null);
//
//                        if (!cursor.moveToFirst()) {
//                            User user = new User();
//                            user.name = contactName;
//                            user.sortname = contactSortname;
//                            user.mobilephone = contactPhone;
//                            user.groupname = "无";
//                            user.nickname = "";
//                            v.add(contactid);
//                            Random random = new Random();
//                            user.photo = random.nextInt(31);
//                            dbHelper.insertAUser(user);
//                        }
//                        cursor.close();
                    }
                    phoneCursor.close();
                }
                editor.putInt(The_First_Time, 1);
                editor.commit();
            }
        }

    }

    // 得到手机通讯录联系人信息
    public void getPhoneContacts() {
        ContentResolver resolver1 = mContext.getContentResolver();
        // 获取手机联系人

        Cursor cursor = db.query(DBHelper.DB_TABLENAME, new String[]{DBHelper.NAME,DBHelper.SORTNAME,DBHelper.NUMBER,DBHelper.PHOTO,DBHelper.ID}, null, null, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                do {
                    TabContactUser user = new TabContactUser();
                    user.name = cursor.getString(0);
                    user.sortname = cursor.getString(1);
                    user.mobilephone = cursor.getString(2);
                    user.photo = cursor.getInt(3);
                    user.uid = cursor.getInt(4);
                    AL.add(user);
                }while (cursor.moveToNext());
            }
        }
    }

    // 追加新插入的用户到AL的尾部，然后插入排序，最后notify一下adapter
    public void AddNewUser(Vector<User> newUsers) {
        TabContactUser t = new TabContactUser();
        for (User i : newUsers){
            t.update(i);
            if (t.name == null || t.mobilephone == null)
                continue;
            t.sortname = CommonUtils.convertToShortPinyin(mContext,t.name);
            t.picture = circleImage[new Random().nextInt(DefaultPicture.ImageID.length)];
            AL.add(t);
        }
        sortList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void changePeopleDetail(User user, Bitmap picture) {
        for(TabContactUser i : AL){
            if(i.mobilephone.equals(user.mobilephone)){
                i.picture = picture;
                i.name = user.name;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void createPeopleDetail(User user, Bitmap picture) {
        TabContactUser user1 = new TabContactUser();
        user1.update(user);
        user1.picture = picture;
        AL.add(user1);
        sortList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deletePeopleDetail(String number) {
        for(int i = 0 ; i < AL.size() ; ++i) {
            Log.i("changePeople",AL.get(i).mobilephone + " " + number);
            if(AL.get(i).mobilephone.equals(number)){
                Log.i("changePeople",AL.get(i).mobilephone + " " + i + "  " + number);
                delete(i);
                break;
            }
        }
    }
}