package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacket on 2016/5/4.
 */
public class FamilyRecordAdapter extends ArrayAdapter<FamilyRecord> {
    private int resourceId;

    public FamilyRecordAdapter(Context context, int itemViewResourceId, List<FamilyRecord> objects) {
        super(context, itemViewResourceId, objects);
        resourceId = itemViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.family_record_name);
            viewHolder.time = (TextView) view.findViewById(R.id.family_record_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        FamilyRecord record = getItem(position);
        viewHolder.name.setText(record.name);
        viewHolder.time.setText(record.timeToStr());

        return view;
    }

    class ViewHolder {
        TextView name, time;
    }
}
