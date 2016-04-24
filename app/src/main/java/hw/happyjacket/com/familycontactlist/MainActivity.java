package hw.happyjacket.com.familycontactlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewTreeObserver;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//        mSettingTab = new TabSettingFragment();
        mTabs = new ArrayList<Fragment>();
        mTabs.add(mRecordTab);
        mTabs.add(mContactTab);
//        mTabs.add(mSettingTab);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PhoneDictionary.CONTAT_ACTION_START:
               /* Log.i("tt","Tt");
                mContactTab.notifyDataSetChanged((HashMap<String,Object>)data.getSerializableExtra("newdata"));*/
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}