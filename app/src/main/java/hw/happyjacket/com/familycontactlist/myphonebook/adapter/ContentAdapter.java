package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 16-4-2.
 */
public class ContentAdapter extends PhoneAdapter {

    private int status;

    public ContentAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
        super(context, textViewResourceId, objects);
    }

    public ContentAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects, int index) {
        super(context, textViewResourceId, objects, index);
    }

    public ContentAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects, int index, int status) {
        super(context, textViewResourceId, objects, index);
        this.status = status;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HashMap<String,String> data = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.content = (TextView) view.findViewById(R.id.call_log_info1);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.content.setText(data.get(PhoneDictionary.DATE));
        return view;
    }

    class ViewHolder
    {
        TextView content;
    }
}
