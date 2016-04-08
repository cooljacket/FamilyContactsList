package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import hw.happyjacket.com.familycontactlist.myphonebook.button.PhoneButton;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneOnClickListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 16-3-28.
 */

/*Adapter for the list of the phone book information*/
public class PhoneAdapter extends ArrayAdapter<HashMap<String,String>> {

    protected int resourceId;
    protected int index;
    public PhoneAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects) {
        super(context,textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public PhoneAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects, int index) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.index = index;
    }


}
