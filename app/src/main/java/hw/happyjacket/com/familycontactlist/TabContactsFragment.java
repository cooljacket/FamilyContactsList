package hw.happyjacket.com.familycontactlist;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
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
    public static final int PHONES_DISPLAY_NAME_INDEX = 0;
    public static final int PHONES_NUMBER_INDEX =1;
    public static final int PHONES_PHOTO_ID_INDEX=2;
    public static final int PHONES_CONTACT_ID_INDEX=3;
    public static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    };
    public ArrayList contacts = new ArrayList();

//    JSONObject data=new JSONObject();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contact_list, container, false);
    }

    private int[] image = {R.drawable.contact_list_icon,R.drawable.man,R.drawable.woman,
            R.drawable.p1,R.drawable.p2,R.drawable.p3
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1:
                HashMap newmap = (HashMap)data.getSerializableExtra("newdata");
                AL.set(positionNew,newmap);
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
        AL = getPhoneContacts();
        // [通话记录]在这里写自己的逻辑，加载页面部分在onCreateView里面自动调用了，tab_contacts_layout
    }

    @Override
    public void onStart() {
        super.onStart();
        listview = (ListView) getView().findViewById(R.id.list_view);


        loadList();
//        Button go = (Button) getView().findViewById(R.id.goToCL);
//        go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("android.intent.action.CL");
//                startActivity(intent);
//            }
//        });
    }

    private  void loadList(){
        //Toast.makeText(getApplicationContext(), ""+num, Toast.LENGTH_SHORT).show();
        SimpleAdapter adapter = new SimpleAdapter(mContext, AL, R.layout.list_item
                ,new String[]{"contactName", "contactPhone", "contactPhoto"}
                ,new int[]{R.id.name, R.id.number, R.id.imageView});
        listview.setAdapter(adapter);
        Toast.makeText(mContext, "" + num, Toast.LENGTH_SHORT).show();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PeopleDetail.class);
                Toast.makeText(mContext, "aaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                positionNew=position;
                HashMap map = (HashMap) parent.getItemAtPosition(position);
//                SerializableMap tmpmap = new SerializableMap();
//                tmpmap.setMap(map);
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", tmpmap);
                Toast.makeText(mContext, "bbbbbbbbbbbb", Toast.LENGTH_SHORT).show();
//                intent.putExtras(bundle);
                intent.putExtra("data", map);

                // 当requestCode为3的时候表示请求转向CPD这个页面？？
                startActivityForResult(intent, 3);
            }
        });
    }

    public class CopyOfContactCollector {

        private final String TAG = CopyOfContactCollector.class.getSimpleName();

        private static final String KEY_BIRTH = "birthday";
        private static final String KEY_ADDR = "address";
        private static final String KEY_NICKNAME = "nickname";
        private static final String KEY_ORG = "org";
        private static final String KEY_IM = "IM";
        private static final String KEY_NOTE = "note";
        private static final String KEY_EMAIL = "email";
        private static final String KEY_PHONE = "phone";
        private static final String KEY_WEBSITE = "website";
        private static final String KEY_PHOTO = "photo";

        private Context context;

        public CopyOfContactCollector(Context context) {
            this.context = context;
        }

        public void getContacts() {

            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                JSONArray contactList = new JSONArray();
                while (cursor.moveToNext()) {

                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    int hasPhone = cursor.getInt(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String contactName = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    long photoId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));

                    JSONObject item = new JSONObject();
                    item.put("id", contactId);
                    item.put("name", contactName);

                    // phone
                    if (hasPhone == 1) {
                        this.getPhone(contactId, item);
                    }

                    // photo
                    this.getPhoto(contactId, photoId, item);

                    // email
                    this.getEmail(contactId, item);

                    // address
                    this.getAddress(contactId, item);

                    // birthdat
                    this.getBirthday(contactId, item);

                    // instant message
                    this.getIM(contactId, item);

                    // nickname
                    this.getNickname(contactId, item);

                    // note
                    this.getNote(contactId, item);

                    // org
                    this.getOrg(contactId, item);

                    // website
                    this.getWebsite(contactId, item);

                    contactList.put(item);
                }

//                data = new JSONObject();
//                data.put("CONTACTS", contactList);
//                data.put("TIMESTAMP", System.currentTimeMillis());

//                System.out.println(data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        private void getPhone (String contactId, JSONObject data) throws JSONException {
            Cursor pCur = null;
            try {
                pCur = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{contactId + ""},
                        null);

                JSONArray phoneList = new JSONArray();
                while (pCur.moveToNext()) {
                    int type = pCur.getInt(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.TYPE));
                    String phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                            context.getResources(), type, "").toString();
                    String phoneNumber = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                    JSONObject item = new JSONObject();
                    item.put("phone", phoneNumber);
                    item.put("type", phoneType);

                    phoneList.put(item);
                }

                data.put(KEY_PHONE, phoneList);
            } finally {
                if (pCur != null) {
                    pCur.close();
                }
            }
        }

        private void getEmail (String contactId, JSONObject data) throws JSONException {
            Cursor emailCur = null;
            try {
                emailCur = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{contactId},
                        null);

                JSONArray emailList = new JSONArray();
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(emailCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.DATA));
                    int type = emailCur.getInt(emailCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.TYPE));
                    String emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(
                            context.getResources(), type, "").toString();

                    JSONObject item = new JSONObject();
                    item.put("email", email);
                    item.put("type", emailType);

                    emailList.put(item);
                }

                data.put(KEY_EMAIL, emailList);
            } finally {
                if (emailCur != null) {
                    emailCur.close();
                }
            }
        }

        private void getNote (String contactId, JSONObject data) throws JSONException {
            Cursor noteCur = null;
            try {
                String noteWhere =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";

                String[] noteWhereParams = new String[]{
                        contactId,
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};

                noteCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        noteWhere,
                        noteWhereParams,
                        null);
                if (noteCur.moveToFirst()) {
                    String note = noteCur.getString(noteCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Note.NOTE));
                    data.put(KEY_NOTE, note);
                }
            } finally {
                if (noteCur != null) {
                    noteCur.close();
                }
            }
        }

        private void getWebsite (String contactId, JSONObject data) throws JSONException {
            Cursor websiteCur = null;
            try {
                String where =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";

                String[] whereParams = new String[]{
                        contactId,
                        ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE};

                websiteCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        where,
                        whereParams,
                        null);
                if (websiteCur.moveToFirst()) {
                    String website = websiteCur.getString(websiteCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Website.URL));
                    data.put(KEY_WEBSITE, website);
                }
            } finally {
                if (websiteCur != null) {
                    websiteCur.close();
                }
            }
        }

        private void getIM (String contactId, JSONObject data) throws JSONException {
            Cursor imCur = null;
            try {
                String imWhere =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";

                String[] imWhereParams = new String[]{contactId,
                        ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};

                imCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        imWhere,
                        imWhereParams,
                        null);

                JSONArray imList = new JSONArray();
                while (imCur.moveToNext()) {
                    String imName = imCur.getString(
                            imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                    int type = imCur.getInt(
                            imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                    String imType = ContactsContract.CommonDataKinds.Im.getTypeLabel(
                            context.getResources(), type, "").toString();

                    JSONObject item = new JSONObject();
                    item.put("imName", imName);
                    item.put("imType", imType);

                    imList.put(item);
                }

                data.put(KEY_IM, imList);
            } finally {
                if (imCur != null) {
                    imCur.close();
                }
            }
        }

        private void getOrg (String contactId, JSONObject data) throws JSONException {
            Cursor orgCur = null;
            try {
                String orgWhere =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";

                String[] orgWhereParams = new String[]{contactId,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};

                orgCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        orgWhere,
                        orgWhereParams,
                        null);
                JSONArray orgList = new JSONArray();
                while (orgCur.moveToNext()) {
                    String orgName = orgCur.getString(orgCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Organization.DATA));
                    String title = orgCur.getString(orgCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Organization.TITLE));

                    JSONObject item = new JSONObject();
                    item.put("orgName", orgName);
                    item.put("title", title);

                    orgList.put(item);
                }
                data.put(KEY_ORG, orgList);
            } finally {
                if (orgCur != null) {
                    orgCur.close();
                }
            }
        }

        private void getNickname (String contactId, JSONObject data) throws JSONException {
            Cursor nicknameCur = null;
            try {
                String nicknameWhere =
                        ContactsContract.Data.CONTACT_ID + " = ? AND " +
                                ContactsContract.Data.MIMETYPE + " = ?";

                String[] nicknameWhereParams = new String[]{contactId,
                        ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE};

                nicknameCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        nicknameWhere,
                        nicknameWhereParams,
                        null);

                while (nicknameCur.moveToNext()) {
                    String nickname = nicknameCur.getString(nicknameCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Nickname.NAME));
                    data.put(KEY_NICKNAME, nickname);
                    break;
                }
            } finally {
                if (nicknameCur != null) {
                    nicknameCur.close();
                }
            }
        }

        private void getBirthday (String contactId, JSONObject data) throws JSONException {
            Cursor bCur = null;
            try {
                bCur = context.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[] {ContactsContract.CommonDataKinds.Event.DATA },
                        ContactsContract.Data.CONTACT_ID+" = "+contactId+" AND "
                                +ContactsContract.Data.MIMETYPE+" = '"
                                +ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE+"' AND "
                                +ContactsContract.CommonDataKinds.Event.TYPE+" = "
                                +ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                        null,
                        null);
                while (bCur.moveToNext()) {
                    String birthday = bCur.getString(0);
                    data.put(KEY_BIRTH, birthday);
                    break;
                }
            } finally {
                if (bCur != null) {
                    bCur.close();
                }
            }
        }

        //        /**
//         * Get address infomation of given contact.
//         *
//         * @param contactId
//         * @param data
//         * @throws JSONException
//         */
        private void getAddress (String contactId, JSONObject data) throws JSONException {

            Cursor postals = null;
            try {
                // address
                postals = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                        null,
                        null);

                int postFormattedNdx = postals.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
                int postTypeNdx = postals.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
                int postStreetNdx = postals.getColumnIndex(
                        ContactsContract.CommonDataKinds.StructuredPostal.STREET);

                JSONArray addrList = new JSONArray();
                while (postals.moveToNext()) {
                    String addressType = ContactsContract.CommonDataKinds.StructuredPostal
                            .getTypeLabel(context.getResources(), postTypeNdx, "").toString();
                    String str1 = postals.getString(postFormattedNdx);
                    String str2 = postals.getString(postStreetNdx);

                    JSONObject item = new JSONObject();
                    item.put("addressType", addressType);
                    item.put("address", str1 + str2);

                    addrList.put(item);
                }

                data.put(KEY_ADDR, addrList);
            } finally {
                if (postals != null) {
                    postals.close();
                }
            }
        }

        //        /**
//         * Get the photo of given contact.
//         *
//         * @param cr
//         * @param id
//         * @param photo_id
//         * @return
//         */
        private void getPhoto (String contactId, long photoId, JSONObject data) throws JSONException {

            Uri uri = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));

            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.getContentResolver(), uri);
            if (input != null) {
                Bitmap photo =  BitmapFactory.decodeStream(input);
                data.put(KEY_PHOTO, photo);
            } else {
                Log.d(TAG, "First try failed to load photo!");
            }

////得到联系人头像ID
//            ContentResolver resolver1 = getContentResolver();
//            Cursor phoneCursor = resolver1.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
//            Long photoid = phoneCursor.getLong(0);
//            Long contactid = phoneCursor.getLong(1);
//
//            //得到联系人头像Bitamp
//            Bitmap contactPhoto = null;
//
//            //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//            if(photoid > 0 ) {
//                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);//contactid);
//                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver1, uri);
//                contactPhoto = BitmapFactory.decodeStream(input);
//            }else {
//                contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_list_icon);
//            }
//            data.put(KEY_PHOTO, contactPhoto);
//
//            phoneCursor.close();

        }


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
                if(++num >= 15) {
//                    num = 0;
                    break;
                }
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

                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

                String contactID = phoneCursor.getString(PHONES_CONTACT_ID_INDEX);

                //debug
//                    Toast.makeText(mContext, "11111", Toast.LENGTH_SHORT).show();
                //debug



                String Group = new String();
                String email=new String();
                String remark = new String();

                //email

                Cursor dataCursor = resolver1.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + contactid, null, null);
                while (dataCursor.moveToNext()){
                    email = dataCursor.getString(dataCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.DATA));
                    break;
                }

//                    emailCur = context.getContentResolver().query(
//                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[]{contactId},
//                            null);
//
//                    JSONArray emailList = new JSONArray();
//                    while (emailCur.moveToNext()) {
//                        String email = emailCur.getString(emailCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Email.DATA));
//                        int type = emailCur.getInt(emailCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Email.TYPE));
//                        String emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(
//                                context.getResources(), type, "").toString();


                //Group!!!!!!!!!!!
//                    dataCursor = resolver1.query(ContactsContract.Groups.CONTENT_URI, null,
//                            ContactsContract.Groups._ID + "=" + contactid
//                            , null, null);
//                    if (dataCursor != null){
//                        if(dataCursor.getCount()>0){
//                            index=dataCursor.getColumnIndex(ContactsContract.Groups.TITLE);
//                        }
//                        if(index!=-1)
//                        Group = dataCursor.getString(index);
//                    }

                //remark
                dataCursor.close();

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
                    remark = noteCursor.getString(noteCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Note.NOTE));
                }

                noteCursor.close();

                //debug
//                    Toast.makeText(mContext, "3333", Toast.LENGTH_SHORT).show();
                //debug

                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//
//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if(photoid > 0 ) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);//contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver1, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                }else {
//                    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_list_icon);
//                }
                int contactPhoto=image[0];//头像默认的图片



                HashMap map=new HashMap();
                map.put("contactName",contactName);
                map.put("contactPhone",phoneNumber);
                map.put("contactPhoto",contactPhoto);
//                    map.put("contactHome",homeNumber);
                map.put("contactID",contactid);
//                    map.put("contactGroup", Group);
                map.put("contactRemark", remark);
                map.put("contactEmail", email);
                // map.put();
                contacts.add(map);
                //Log.d("debug",contactName);
            }

            phoneCursor.close();
        }
        return contacts;
    }
}