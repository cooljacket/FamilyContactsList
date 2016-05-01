package hw.happyjacket.com.familycontactlist.MyDirPicker;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hw.happyjacket.com.familycontactlist.R;

/**
 * Created by jacket on 2016/4/25.
 */
public class DirAdapter extends ArrayAdapter<DirDescriptor> {
    private int resourceId;

    public DirAdapter(Context context, int itemViewResourceId, List<DirDescriptor> objects) {
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
            viewHolder.dir_name = (TextView) view.findViewById(R.id.dir_name);
            viewHolder.arrow = (ImageView) view.findViewById(R.id.dir_arrow);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        DirDescriptor descriptor = getItem(position);
        viewHolder.dir_name.setText(descriptor.getDirName());
        viewHolder.arrow.setColorFilter(new LightingColorFilter(0, 0xaaaaaa));

        return view;
    }

    class ViewHolder {
        TextView dir_name;
        ImageView arrow;
    }
}
