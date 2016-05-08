package hw.happyjacket.com.familycontactlist;


import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.MyDirPicker.DirPicker;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;


public class MainActivity extends AppCompatActivity {
    public static final String SEARCHID = "searchID";
    public static final String SEARCHCONTENT = "searchContent";
    private static int searchID;
    private static String searchContent;
    private ViewPager mPager;
    private int selected_tab = 0, base_tab_id = 0;
    private RadioGroup tabs_group;
    private RadioButton tab_record, tab_contacts;
    private TabRecordFragment mRecordTab;
    private TabContactsFragment mContactTab;
    private static List<PhoneFragment> mTabs;
    private FragmentPagerAdapter mPagerAdapter;
    public static List<HashMap<String,String>> phoneElement;
    private static final int FILE_SELECT_CODE = 0;
    private MenuItem login_register_item;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private ImageButton menu;

    public static int getSearchID() {
        return searchID;
    }

    public static void setSearchID(int searchID) {
        MainActivity.searchID = searchID;
    }

    public static String getSearchContent() {
        return searchContent;
    }

    public static void setSearchContent(String searchContent) {
        MainActivity.searchContent = searchContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        InitFragments();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (selected_tab - base_tab_id != position)
                    ChangeTab(base_tab_id + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        menu = (ImageButton) findViewById(R.id.main_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreatePopupMenu(v);
            }
        });
        menu.setColorFilter(new LightingColorFilter(0,menu.getSolidColor()));

        InitTabHeader();
    }



    private void InitFragments() {
        mRecordTab = new TabRecordFragment();
        mContactTab = new TabContactsFragment();
        mTabs = new ArrayList<>();
        mTabs.add(mRecordTab);
        mTabs.add(mContactTab);

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }

            @Override
            public int getCount() {
                return mTabs.size();
            }
        };
    }
    private void InitTabHeader() {
        tabs_group = (RadioGroup) findViewById(R.id.tab_header);
        tab_record = (RadioButton) findViewById(R.id.record);
        tab_contacts = (RadioButton) findViewById(R.id.contacts);
        base_tab_id = tab_record.getId();

        tabs_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ChangeTab(checkedId);
                mPager.setCurrentItem(checkedId - base_tab_id);
            }
        });

        selected_tab = base_tab_id;
        tab_record.setChecked(true);
    }

    private RadioButton getTheTab(int id) {
        if (id == base_tab_id)
            return tab_record;
        if (id == tab_contacts.getId())
            return tab_contacts;
        return tab_record;
    }

    public void ChangeTab(int checkedId) {
        RadioButton last = getTheTab(selected_tab);
        last.setChecked(false);
        last.setTextColor(getResources().getColor(R.color.black));
        /* last.setBackgroundColor(getResources().getColor(R.color.tab_bk_color));
         last.setTextColor(getResources().getColor(R.color.tab_text_unfocus_color));*/

        selected_tab = checkedId;
        RadioButton it = getTheTab(selected_tab);
        it.setChecked(true);
        it.setTextColor(getResources().getColor(R.color.bluegreen));
        /* it.setBackgroundColor(getResources().getColor(R.color.tab_front_color));
         it.setTextColor(getResources().getColor(R.color.tab_text_focus_color));*/

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case MainActivity.FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    Vector<User> newUsers = ContactsDataUtils.ImportContactsFromLocalFile(MainActivity.this, uri.getPath());
                    // newUsers是导入后新增的用户数据，需要根据这个来更新列表
                    // ps，联系人修改姓名之后，sortname要记得也要同步更新！！！
                    // 注。。。AddNewUser接口还没实现啊啊啊啊啊
                    mContactTab.AddNewUser(newUsers);
                    Toast.makeText(MainActivity.this, "导入本地联系人成功", Toast.LENGTH_SHORT).show();
                    break;
                case DirPicker.TO_PICK_A_DIR:
                    ContactsDataUtils.ExportContactsToLocalFile(MainActivity.this, data.getStringExtra(DirPicker.PATH_KEY));
                    Toast.makeText(MainActivity.this, "导出本地联系人成功", Toast.LENGTH_SHORT).show();
                    break;
                case LoginActivity.LOGIN_RESULT:
                    login_register_item.setTitle(CommonUtils.HAS_LOGIN);
                    Toast.makeText(MainActivity.this, "登录/注册成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
        else if(resultCode == PhoneDictionary.CONTACT_DELETE){
            int pos = data.getIntExtra(TabContactsFragment.POS, -1);
            mContactTab.delete(pos);
        }
        else{
            switch (requestCode) {
                case PhoneDictionary.CONTAT_ACTION_START:
                    if (data != null && data.getSerializableExtra(PhoneDictionary.OTHER) != null)
                        mContactTab.notifyDataSetChanged((User) data.getSerializableExtra(PhoneDictionary.OTHER),(Bitmap)data.getParcelableExtra(PhoneDictionary.Picture));
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        login_register_item = menu.getItem(0);
        if (LoginActivity.getRememberState(MainActivity.this))
            LoginActivity.SetToken(MainActivity.this, null);
        if (LoginActivity.getToken(MainActivity.this) != null)
            login_register_item.setTitle(CommonUtils.HAS_LOGIN);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_export:
                Intent intent = new Intent(DirPicker.ACTION);
                startActivityForResult(intent, DirPicker.TO_PICK_A_DIR);
                break;
            case R.id.action_import:
                chooseFile();
                break;
            case R.id.action_export_web:
                ContactsDataUtils.ExportContactsToWeb(MainActivity.this, handler);
                break;
            case R.id.action_import_web:
                ContactsDataUtils.ImportContactsFromWeb(MainActivity.this, handler);
                break;
            case R.id.action_fm:
                Intent fm = new Intent(MainActivity.this, FamilyMoney.class);
                startActivity(fm);
                break;
            case R.id.action_account:
                LoginOrRegister(MainActivity.this);
                break;
            case R.id.action_add_contact:
                CreatePeopleDetail.actionStart(MainActivity.this, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }*/

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "亲，木有文件管理器啊啊啊啊-_-!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void LoginOrRegister(Context context) {
        if (LoginActivity.getToken(context) != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("已经登录了，您想退出登录吗？");
            dialog.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LoginActivity.Logout(MainActivity.this);
                    login_register_item.setTitle(CommonUtils.TO_LOGIN);
                }
            });
            dialog.show();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.LOGIN_RESULT);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ContactsDataUtils.EXPORT_TO_WEB:
                    Log.d("hehe-export", (String) msg.obj);
                    boolean result = ((String) msg.obj).equals("true");
                    String str = result ? "备份到云端成功" : "备份到云端失败，请稍后重试";
                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                    break;
                case ContactsDataUtils.IMPORT_FROM_WEB:
                    Vector<User> newUsers = (Vector<User>) msg.obj;
                    // 已插入到数据库中，记得更新列表的显示

                    Toast.makeText(MainActivity.this, "从云端导入联系人成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public static void changePeopleDetail(User user, Bitmap picture){
        for (PhoneFragment i : mTabs)
            i.changePeopleDetail(user, picture);
    }

    public static void createPeopleDetail(User user, Bitmap picture){
        for (PhoneFragment i : mTabs)
            i.createPeopleDetail(user, picture);
    }

    public static void deletePeopleDetai(String number){
        for(PhoneFragment i : mTabs)
            i.deletePeopleDetail(number);
    }


    private void onCreatePopupMenu(View v){
        final ImageButton menu1 = menu;
        final int color = menu.getSolidColor();
        menu1.setColorFilter(new LightingColorFilter(0,0x00afff));

        PopupMenu popupMenu = new PopupMenu(this,v);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, popupMenu.getMenu());
        login_register_item = popupMenu.getMenu().getItem(0);
        if (LoginActivity.getRememberState(MainActivity.this))
            LoginActivity.SetToken(MainActivity.this, null);
        if (LoginActivity.getToken(MainActivity.this) != null)
            login_register_item.setTitle(CommonUtils.HAS_LOGIN);

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                menu1.setColorFilter(new LightingColorFilter(0,color));
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 处理动作按钮的点击事件
                switch (item.getItemId()) {
                    case R.id.action_export:
                        Intent intent = new Intent(DirPicker.ACTION);
                        startActivityForResult(intent, DirPicker.TO_PICK_A_DIR);
                        break;
                    case R.id.action_import:
                        chooseFile();
                        break;
                    case R.id.action_export_web:
                        ContactsDataUtils.ExportContactsToWeb(MainActivity.this, handler);
                        break;
                    case R.id.action_import_web:
                        ContactsDataUtils.ImportContactsFromWeb(MainActivity.this, handler);
                        break;
                    case R.id.action_fm:
                        Intent fm = new Intent(MainActivity.this, FamilyMoney.class);
                        startActivity(fm);
                        break;
                    case R.id.action_account:
                        LoginOrRegister(MainActivity.this);
                        break;
                    case R.id.action_add_contact:
                        CreatePeopleDetail.actionStart(MainActivity.this, null);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });
        popupMenu.show();

    }
}