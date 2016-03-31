package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacket on 2016/3/30.
 */
public class MyPagerAdapter extends PagerAdapter {
    List<View> mListViewPager;            // ViewPager对象的内容
    Context mContext;

    public MyPagerAdapter(Context context) {
        mContext = context;

        View page1 = LayoutInflater.from(mContext).inflate(R.layout.tab_record_layout, null);
        View page2 = LayoutInflater.from(mContext).inflate(R.layout.tab_contacts_layout, null);
        View page3 = LayoutInflater.from(mContext).inflate(R.layout.tab_settings_layout, null);
        mListViewPager = new ArrayList<View>();
        mListViewPager.add(page1);
        mListViewPager.add(page2);
        mListViewPager.add(page3);
    }

    @Override
    public int getCount() {
        return mListViewPager.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(mListViewPager.get(position));
        return mListViewPager.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) mListViewPager.get(position));
    }
}