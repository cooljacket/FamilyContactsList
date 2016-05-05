package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;

/**
 * Created by root on 16-4-15.
 */
public class CheckBoxAdapter extends ArrayAdapter<String> {

    private int recourceID;
    private Vector<Boolean> check = new Vector<>();


    public CheckBoxAdapter(Context context, int resource) {
        super(context, resource);
        recourceID = resource;
    }

    public CheckBoxAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        recourceID = resource;
    }

    public CheckBoxAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        recourceID = resource;
        check = new Vector<Boolean>(objects.length);
        for (int i = 0 ; i < objects.length ; ++i)
            check.add(false);
    }



    public CheckBoxAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        recourceID = resource;
        check = new Vector<Boolean>(objects.size());
        for (int i = 0 ; i < objects.size() ; ++i)
            check.add(false);
    }

    public CheckBoxAdapter(Context context, int resource, List<String> objects,@Nullable String [] have) {
        super(context, resource, objects);
        recourceID = resource;
        check = new Vector<Boolean>(objects.size());
        if(have != null){

            for (int i = 0 ,count = 0 ; i < objects.size() ; ++i){
                if(count >= have.length || !have[count].equals(objects.get(i))) {
                    check.add(false);
                    continue;
                }
                check.add(true);
                count++;
            }
        }
        else {
            for (int i = 0; i < objects.size(); ++i)
                check.add(false);
        }
    }


    public Vector<Boolean> getIsChecked() {
        return check;
    }

    public void changeCheck(int position){
        if(position < 0 || position >= check.size())
            return;
        check.set(position,!check.get(position));

    }

    public void setCheck(Vector<Boolean> check) {
        this.check = check;
    }

    public void addCheck(){
        check.add(false);
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
            viewHolder.name = (TextView)view.findViewById(R.id.dialog_checkbox_textview);
            viewHolder.mCheckBox = (CheckBox) view.findViewById(R.id.dialog_checkbox_checkbox);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (MyViewHolder) view.getTag();
        }

        viewHolder.name.setText(phoneElement);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    check.set(position,isChecked);
                    notifyDataSetChanged();

            }
        });

        viewHolder.mCheckBox.setChecked(check.get(position));

        return view;
    }

    class MyViewHolder  {

        TextView name;
        CheckBox mCheckBox;
    }
}
