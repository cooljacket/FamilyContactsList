package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;

/**
 * Created by root on 16-4-15.
 */
public class RadioAdapter extends ArrayAdapter<String> {

    private int recourceID;
    private int index = 0;

    public RadioAdapter(Context context, int resource) {
        super(context, resource);
        recourceID = resource;
    //    mRadioGroup = new RadioGroup(context);
    }

    public RadioAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        recourceID = resource;
    //    mRadioGroup = new RadioGroup(context);
    }

    public RadioAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        recourceID = resource;
    //    mRadioGroup = new RadioGroup(context);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        String phoneElement = getItem(position);
        View view;
        MyViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(recourceID, null);
            viewHolder = new MyViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.dialog_radio_textview);
            viewHolder.mRadioButton = (RadioButton) view.findViewById(R.id.dialog_radio_radio);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (MyViewHolder) view.getTag();
        }

        viewHolder.name.setText(phoneElement);
        viewHolder.mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    index = position;
                    notifyDataSetChanged();
                }
            }
        });

        if(index == position)
            viewHolder.mRadioButton.setChecked(true);
        else
            viewHolder.mRadioButton.setChecked(false);

        return view;
    }

    class MyViewHolder  {

        TextView name;
        RadioButton mRadioButton;
    }
}
