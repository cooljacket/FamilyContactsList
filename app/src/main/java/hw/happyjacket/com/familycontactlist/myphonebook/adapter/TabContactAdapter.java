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
import hw.happyjacket.com.familycontactlist.TabContactsFragment;

/**
 * Created by root on 16-4-26.
 */
public class TabContactAdapter extends ArrayAdapter<HashMap<String,Object> > {

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

    public TabContactAdapter(Context context, int resource, List<HashMap<String, Object>> objects) {
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

        HashMap<String,Object> item = getItem(position);

        String t = (String) item.get(TabContactsFragment.SORTNAME);
        char h;
        if(t.length() > 0 && 'A'<= (h = (char) (t.charAt(0) - 'a' + 'A')) && h <= 'Z' && ((sortName == null) || h != sortName)){
            sortName = h;
            myHolder.alphebat.setHeight(height);
            myHolder.alphebat.setText(" " + sortName.toString());
        }
        else
            myHolder.alphebat.setHeight(0);

        myHolder.name.setText((String)item.get(TabContactsFragment.NAME));
        myHolder.icon.setImageBitmap((Bitmap)item.get(TabContactsFragment.PHOTO));
        return view;
    }

    class MyHolder{
        ImageView icon;
        TextView name;
        TextView alphebat;
    }
}
