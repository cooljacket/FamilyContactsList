package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneOnClickListener;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 16-4-2.
 */
public class MainAdapter extends PhoneAdapter {

    public MainAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
        super(context, textViewResourceId, objects);
    }

    public MainAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects, int index) {
        super(context, textViewResourceId, objects,index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        HashMap<String,String> phoneListElement = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.phone_name = (TextView) view.findViewById(R.id.phone_name);
            viewHolder.phone_info = (TextView) view.findViewById(R.id.phone_info);
            viewHolder.phone_content = (PhoneButton) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(phoneListElement.get(PhoneDictionary.NAME) != null)
        {
            viewHolder.phone_name.setText(phoneListElement.get(PhoneDictionary.NAME));
            viewHolder.phone_info.setText(phoneListElement.get(PhoneDictionary.NUMBER) + " " + phoneListElement.get(PhoneDictionary.DATE));
        }
        else
        {
            viewHolder.phone_name.setText(phoneListElement.get(PhoneDictionary.NUMBER));
            viewHolder.phone_info.setText(phoneListElement.get(PhoneDictionary.DATE));
        }
        viewHolder.phone_content.setIndex(position);
        viewHolder.phone_content.setName(PhoneRegister.get(index).getPhoneListElementList().get(position).get(PhoneDictionary.NAME));
        viewHolder.phone_content.setNumber(PhoneRegister.get(index).getPhoneListElementList().get(position).get(PhoneDictionary.NUMBER));
        if(!PhoneOnClickListener.isSetUp())
        {
            PhoneOnClickListener.setContext(getContext());
            PhoneOnClickListener.setSetUp(true);
        }
        viewHolder.phone_content.setOnClickListener(PhoneOnClickListener.getInstance());
        return view;
    }

    class ViewHolder
    {
        TextView phone_name;
        TextView phone_info;
        PhoneButton phone_content;
    }
}
