package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.List;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.TabContactsFragment;

/**
 * Created by root on 16-5-10.
 */
public class AlphebatAdapter extends ArrayAdapter<Character> {

    private int resourceID;
    private TabContactsFragment mTabContactsFragment;
    public AlphebatAdapter(Context context, int resource) {
        super(context, resource);
        resourceID = resource;
    }

    public AlphebatAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        resourceID = resource;
    }

    public AlphebatAdapter(Context context, int resource, Character [] objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    public TabContactsFragment getTabContactsFragment() {
        return mTabContactsFragment;
    }

    public void setTabContactsFragment(TabContactsFragment tabContactsFragment) {
        mTabContactsFragment = tabContactsFragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        final MyHolder myHolder;
        if (convertView == null){
            v = LayoutInflater.from(getContext()).inflate(resourceID,null);
            myHolder = new MyHolder();
            myHolder.button = (Button) v.findViewById(R.id.forward_button);
            v.setTag(myHolder);
        }
        else {
            v = convertView;
            myHolder = (MyHolder) v.getTag();
        }
        return v;
    }

    class MyHolder{
        Button button;
    }
}
