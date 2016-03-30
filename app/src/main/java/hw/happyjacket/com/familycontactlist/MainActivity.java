package hw.happyjacket.com.familycontactlist;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    ViewPager mPager;
    int selected_tab = 0, base_tab_id = 0;
    RadioGroup tabs_group;
    RadioButton tab_record, tab_contacts, tab_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this);
        mPager.setAdapter(pagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (selected_tab - base_tab_id != position)
                    ChangeTab(base_tab_id + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        InitTabHeader();
    }

    private void InitTabHeader() {
        tabs_group = (RadioGroup) findViewById(R.id.tab_header);
        tab_record = (RadioButton) findViewById(R.id.record);
        tab_contacts = (RadioButton) findViewById(R.id.contacts);
        tab_settings  = (RadioButton) findViewById(R.id.settings);

        base_tab_id = tab_record.getId();

        tabs_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ChangeTab(checkedId);
                mPager.setCurrentItem(checkedId-base_tab_id);
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
        return tab_settings;
    }

    public void ChangeTab(int checkedId) {
        RadioButton it = getTheTab(selected_tab);
        it.setBackgroundColor(getResources().getColor(R.color.tab_unfocused));
        it.setTextColor(getResources().getColor(R.color.tab_text_unfocus_color));

        selected_tab = checkedId;
        it = getTheTab(selected_tab);
        it.setBackgroundColor(getResources().getColor(R.color.tab_focused));
        it.setTextColor(getResources().getColor(R.color.tab_text_focus_color));
    }
}