package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.TabContactUser;
import hw.happyjacket.com.familycontactlist.TabContactsFragment;
import hw.happyjacket.com.familycontactlist.User;

/**
 * Created by root on 16-4-26.
 */
public class TabContactAdapter extends ArrayAdapter<TabContactUser> {

    private int recourceID;
    private Character sortName = null;
    private int height;
    private String TAG = this.getClass().toString();

    public TabContactAdapter(Context context, int resource) {
        super(context, resource);
        recourceID = resource;
    }

    public TabContactAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        recourceID = resource;
    }

    public TabContactAdapter(Context context, int resource, List<TabContactUser> objects) {
        super(context, resource, objects);
        recourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MyHolder myHolder;
        if(convertView == null){

            view = LayoutInflater.from(getContext()).inflate(recourceID,null);
            myHolder = new MyHolder();
            myHolder.icon = (ImageView) view.findViewById(R.id.contact_icon);
            myHolder.name = (TextView) view.findViewById(R.id.contact_name);
            myHolder.alphebat = (TextView) view.findViewById(R.id.seperate_alphebat);
            height = myHolder.alphebat.getLineHeight();
            view.setTag(myHolder);
        }
        else{
            view = convertView;
            myHolder = (MyHolder) view.getTag();
        }



        TabContactUser item = getItem(position);
        TabContactUser last = null;
        String t1 = item.sortname,t2;
        char h1,h2;
        myHolder.alphebat.setHeight(0);
        if(position > 0)
            last = getItem(position - 1);

        else if(t1 != null && t1.length() > 0 && 'A'<= (h1 = Character.toUpperCase(t1.charAt(0))) && h1 <= 'Z'){
            myHolder.alphebat.setHeight(height);
            myHolder.alphebat.setText(" " + h1);
        }

        if(last != null && (t2 = last.sortname) != null && t2.length() > 0
                && t1 != null && t1.length() > 0 && 'A'<= (h1 = Character.toUpperCase(t1.charAt(0))) && h1 <= 'Z'){
            h2 = Character.toUpperCase(t2.charAt(0));
            if(h2 != h1) {
                myHolder.alphebat.setHeight(height);
                myHolder.alphebat.setText(" " + h1);
            }
        }


        myHolder.name.setText(item.name);
        myHolder.icon.setImageBitmap(item.picture);
        return view;
    }

    class MyHolder{
        ImageView icon;
        TextView name;
        TextView alphebat;
    }
}
