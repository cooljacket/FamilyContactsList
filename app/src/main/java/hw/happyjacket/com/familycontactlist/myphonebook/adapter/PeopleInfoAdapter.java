package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.R;

/**
 * Created by root on 16-4-16.
 */
public class PeopleInfoAdapter extends ArrayAdapter<String[]> {

    private int recourceID;

    public PeopleInfoAdapter(Context context, int resource) {
        super(context, resource);
        recourceID = resource;
    }

    public PeopleInfoAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        recourceID = resource;
    }

    public PeopleInfoAdapter(Context context, int resource, String[][] objects) {
        super(context, resource, objects);
        recourceID = resource;
    }


    /*由于重用view导致所有监听器都能够访问任何一个文本的变化，所有每次都新建一个view，每个监听器只访问自己的view*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String[] phoneElement = getItem(position);
        View view;
        MyViewHolder viewHolder;
        /*if(convertView == null)
        {*/
            view = LayoutInflater.from(getContext()).inflate(recourceID, null);
            viewHolder = new MyViewHolder();
            viewHolder.name = (TextView)view.findViewById(R.id.people_info_name);
            viewHolder.content = (EditText) view.findViewById(R.id.people_info_content);
            view.setTag(viewHolder);
        /*}*/
        /*else
        {
            view = convertView;
            viewHolder = (MyViewHolder) view.getTag();
        }
*/
        viewHolder.name.setText(phoneElement[0]);
        viewHolder.content.setText(phoneElement[1]);
        viewHolder.content.setHint(phoneElement[0]);
        viewHolder.content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneElement[1] = s.toString();
            }
        });
        return view;
    }

    class MyViewHolder{
        TextView name;
        EditText content;
    }
}
