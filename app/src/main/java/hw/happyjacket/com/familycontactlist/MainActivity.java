package hw.happyjacket.com.familycontactlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.MyDirPicker.DirPicker;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;


public class MainActivity extends AppCompatActivity {
    private ViewPager mPager;
    private int selected_tab = 0, base_tab_id = 0;
    private RadioGroup tabs_group;
    private RadioButton tab_record, tab_contacts, tab_settings;
    private ArrayList<Point> tab_sizes = new ArrayList<>();
    private TabRecordFragment mRecordTab;
    private TabContactsFragment mContactTab;
    private TabSettingFragment mSettingTab;
    private List<Fragment> mTabs;
    private FragmentPagerAdapter mPagerAdapter;
    public static List<HashMap<String,String>> phoneElement;
    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonSettingsAndFuncs.FileHeader = getExternalFilesDir(null) + File.separator;
//        InitTabSizes();
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

        InitTabHeader();
    }

    private void InitFragments() {
        mRecordTab = new TabRecordFragment();
        mContactTab = new TabContactsFragment();
        mTabs = new ArrayList<Fragment>();
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
 //        tab_settings  = (RadioButton) findViewById(R.id.settings);
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

         InitTabSizes();
     }

     private void InitTabSizes() {
         ViewTreeObserver vto1 = tab_record.getViewTreeObserver();
         vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {
                 tab_record.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                 tab_sizes.add(new Point(tab_record.getWidth(), tab_record.getHeight()));
             }
         });

         ViewTreeObserver vto2 = tab_contacts.getViewTreeObserver();
         vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
             @Override
             public void onGlobalLayout() {
                 tab_contacts.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                 tab_sizes.add(new Point(tab_contacts.getWidth(), tab_contacts.getHeight()));
             }
         });
     }

     private RadioButton getTheTab(int id) {
         if (id == base_tab_id)
             return tab_record;
         if (id == tab_contacts.getId())
             return tab_contacts;
 //        return_btn tab_settings;
         return tab_record;
     }

     public void ChangeTab(int checkedId) {
         RadioButton last = getTheTab(selected_tab);
         last.setBackgroundColor(getResources().getColor(R.color.tab_bk_color));
         last.setTextColor(getResources().getColor(R.color.tab_text_unfocus_color));

         selected_tab = checkedId;
         RadioButton it = getTheTab(selected_tab);
         it.setBackgroundColor(getResources().getColor(R.color.tab_front_color));
         it.setTextColor(getResources().getColor(R.color.tab_text_focus_color));
         if (!tab_sizes.isEmpty())
             onTabSelected(it, tab_sizes.get(checkedId - base_tab_id), getResources().getColor(R.color.tab_front_color), getResources().getColor(R.color.tab_bk_color));
     }


     void onTabSelected(RadioButton btn, Point size, int frontColor, int bkColor) {
         int width = size.x, height = size.y;
         Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
         Canvas canvasTemp = new Canvas(newb);
         canvasTemp.drawColor(bkColor);

         Paint p = new Paint();
         p.setColor(frontColor);
         p.setAntiAlias(true);// 设置画笔的锯齿效果
         canvasTemp.drawText("画圆角矩形:", 10, 260, p);
         RectF oval1 = new RectF(0, 0, width, height);   // 设置个新的长方形
         canvasTemp.drawRoundRect(oval1, 20, 15, p);     //第二个参数是x半径，第三个参数是y半径
         RectF oval2 = new RectF(0, 15, width, height);  // 设置个新的长方形
         canvasTemp.drawRect(oval2, p);
         Drawable drawable = new BitmapDrawable(newb);
         btn.setBackgroundDrawable(drawable);
     }

    private class TabAdapter {
        private int frontColor, bkColor;

        TabAdapter(int frontColor, int bkColor) {
            this.frontColor = frontColor;
            this.bkColor = bkColor;
        }

        void onSelected(RadioButton btn, Point size, int frontColor, int bkColor) {
            int width = size.x, height = size.y;
            Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvasTemp = new Canvas(newb);
            canvasTemp.drawColor(bkColor);

            Paint p = new Paint();
            p.setColor(frontColor);
            p.setAntiAlias(true);// 设置画笔的锯齿效果
            canvasTemp.drawText("画圆角矩形:", 10, 260, p);
            RectF oval1 = new RectF(0, 0, width, height);   // 设置个新的长方形
            canvasTemp.drawRoundRect(oval1, 20, 15, p);     //第二个参数是x半径，第三个参数是y半径
            RectF oval2 = new RectF(0, 15, width, height);  // 设置个新的长方形
            canvasTemp.drawRect(oval2, p);
            Drawable drawable = new BitmapDrawable(newb);
            btn.setBackgroundDrawable(drawable);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case MainActivity.FILE_SELECT_CODE:
                    Uri uri = data.getData();
                    Vector<User> newUsers = CommonSettingsAndFuncs.ImportContacts(MainActivity.this, uri.getPath());
                    // newUsers是导入后新增的用户数据，需要根据这个来更新列表
                    // ps，联系人修改姓名之后，sortname要记得也要同步更新！！！
                    break;
                case DirPicker.TO_PICK_A_DIR:
                    CommonSettingsAndFuncs.ExportContacts(MainActivity.this, data.getStringExtra(DirPicker.PATH_KEY));
                    Toast.makeText(MainActivity.this, "export", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        else{
            switch (requestCode) {
                case PhoneDictionary.CONTAT_ACTION_START:
                    if (data != null && data.getSerializableExtra(PhoneDictionary.OTHER) != null)
                        mContactTab.notifyDataSetChanged((HashMap<String, Object>) data.getSerializableExtra(PhoneDictionary.OTHER));
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_search:

                Toast.makeText(MainActivity.this, CommonSettingsAndFuncs.convertToPinyin(MainActivity.this, "郭庆潼"), Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_export:
                Intent intent = new Intent(DirPicker.ACTION);
                startActivityForResult(intent, DirPicker.TO_PICK_A_DIR);
                return true;
            case R.id.action_import:
                Toast.makeText(MainActivity.this, "import", Toast.LENGTH_SHORT).show();
                chooseFile();
                return true;
            case R.id.action_settings:
                // do some settings...
                Toast.makeText(MainActivity.this, "some settings...", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
}