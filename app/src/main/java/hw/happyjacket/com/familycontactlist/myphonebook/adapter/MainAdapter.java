package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneOnClickListener;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 16-4-2.
 */
public class MainAdapter extends PhoneAdapter {

    private static final String TAG = MainAdapter.class.toString();
    private static ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
    private static ForegroundColorSpan gray;
    private SpannableStringBuilder builder;

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
        String t;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.phone_name = (TextView) view.findViewById(R.id.phone_name);
            viewHolder.phone_info = (TextView) view.findViewById(R.id.phone_info);
            viewHolder.phone_content = (PhoneButton) view.findViewById(R.id.content);
            viewHolder.phone_type = (ImageView) view.findViewById(R.id.phone_type);
            gray = new ForegroundColorSpan(viewHolder.phone_name.getCurrentTextColor());
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        switch (phoneListElement.get(PhoneDictionary.TYPE)){
            case "来电":
                viewHolder.phone_type.setImageResource(R.drawable.call_received);
                break;
            case "拨出":
                viewHolder.phone_type.setImageResource(R.drawable.call_made);
                break;
            case "响铃":
                viewHolder.phone_type.setImageResource(R.drawable.call_missed);
                break;
            default:
                break;
        }

        String location = phoneListElement.get(PhoneDictionary.LOCATION) == null ? "" : phoneListElement.get(PhoneDictionary.LOCATION) + " ";
        String number = phoneListElement.get(PhoneDictionary.NUMBER);
        String date = phoneListElement.get(PhoneDictionary.DATE);
        String name = phoneListElement.get(PhoneDictionary.NAME);
        String divider = phoneListElement.get(PhoneDictionary.DIVIDER);
        if(name != null)
        {
            viewHolder.phone_name.setText(name);
            if (divider != null){
                builder = new SpannableStringBuilder (location + number + " " + date);
                int d = Integer.parseInt(divider);
                int begin = location.length() + d;
                int end = location.length() + d + DialogFactory.Number.length();
                builder.setSpan(red,begin,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.phone_info.setText(builder);
            }
            else
                viewHolder.phone_info.setText(location + number + " " + date);
        }
        else
        {
            if (divider != null){
                builder = new SpannableStringBuilder (number);
                int d = Integer.parseInt(divider);
                int begin = d;
                int end = d + DialogFactory.Number.length();
                Log.i(TAG,begin + " " + end);
                builder.setSpan(red,begin,end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.phone_name.setText(builder);
            }
            else
                viewHolder.phone_name.setText(number);
            viewHolder.phone_info.setText(location + date);

        }
        viewHolder.phone_name.setTextSize(16);
        viewHolder.phone_content.setIndex(position);
        viewHolder.phone_content.setName(name);
        viewHolder.phone_content.setNumber(number);
        viewHolder.phone_content.setLocation(location);
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
        ImageView phone_type;
    }
}
