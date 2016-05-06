package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PhoneAdapter;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 16-4-1.
 */
public class CallLogAdapter extends PhoneAdapter {

    private int sms  = -1;
    private String number = "";
    private String message = "";

    public CallLogAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects) {
        super(context,textViewResourceId, objects);
    }

    public CallLogAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects, int index) {
        super(context, textViewResourceId, objects,index);
    }

    public CallLogAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects, int index, int sms) {
        super(context, textViewResourceId, objects, index);
        this.sms = sms;
    }

    public CallLogAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects, int index, int sms,String number) {
        super(context, textViewResourceId, objects, index);
        this.sms = sms;
        this.number = number;
    }

    public CallLogAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects, int index, int sms,String number, String message) {
        super(context, textViewResourceId, objects, index);
        this.sms = sms;
        this.number = number;
        this.message = message;
    }



    public int isSms() {
        return sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final HashMap<String,String> data = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.info1 = (TextView) view.findViewById(R.id.call_log_info1);
            viewHolder.info2 = (TextView) view.findViewById(R.id.call_log_info2);
            viewHolder.info3 = (TextView) view.findViewById(R.id.call_log_info3);
            viewHolder.info4 = (Button) view.findViewById(R.id.call_log_info4);
            viewHolder.height = viewHolder.info2.getLineHeight();
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.info1.setText(data.get(PhoneDictionary.DATE));

        if(sms == position) {
            viewHolder.info4.setVisibility(View.VISIBLE);
            viewHolder.info4.setBackgroundResource(android.R.drawable.sym_action_chat);
            viewHolder.info4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Operation.sms(number,message);
                }
            });
        }

        else
            viewHolder.info4.setVisibility(View.INVISIBLE);


        if(data.get(PhoneDictionary.NUMBER) != null) {
            viewHolder.info2.setHeight(viewHolder.height);
            viewHolder.info2.setText(data.get(PhoneDictionary.NUMBER));
        }
        else
            viewHolder.info2.setHeight(0);
        String a = data.get(PhoneDictionary.TYPE) , b = data.get(PhoneDictionary.DURATION);
        if(a != null && b !=null) {
            viewHolder.info3.setHeight(viewHolder.height);
            if (!b.equals("-1"))
                viewHolder.info3.setText(a + " " + b + "秒");
            else
                viewHolder.info3.setText("新建联系人");
        }
        else
            viewHolder.info3.setHeight(0);


        return view;
    }

    class ViewHolder
    {
        TextView info1;
        TextView info2;
        TextView info3;
        Button info4;
        int height;
    }
}
