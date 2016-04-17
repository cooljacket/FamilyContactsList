//package hw.happyjacket.com.familycontactlist;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.HashMap;
//import java.util.List;
//
//import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PhoneAdapter;
//import hw.happyjacket.com.familycontactlist.circleImage ;
//import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
//import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
//import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneOnClickListener;
//
///**
// * Created by leo on 2016/4/13.
// */
//public class contactAdapter extends PhoneAdapter {
//    public contactAdapter(Context context, int textViewResourceId, List<HashMap<String, String>> objects) {
//        super(context, textViewResourceId, objects);
//    }
//
//    public contactAdapter(Context context, int textViewResourceId, List<HashMap<String,String>> objects, int index) {
//        super(context, textViewResourceId, objects,index);
//    }
//
////    @Override
////    public View getView(int position, View convertView, ViewGroup parent)
////    {
////        HashMap<String,String> phoneListElement = getItem(position);
////        View view;
////        ViewHolder viewHolder;
////        if(convertView == null)
////        {
////            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
////            viewHolder = new ViewHolder();
////            viewHolder.contactName = (TextView) view.findViewById(R.id.name);
////
////            viewHolder.contactPhoto = (circleImage) view.findViewById(R.id.imageView);
////            view.setTag(viewHolder);
////        }
////        else
////        {
////            view = convertView;
////            viewHolder = (ViewHolder) view.getTag();
////        }
////        if(phoneListElement.get(PhoneDictionary.NAME) != null)
////        {
////            viewHolder.phone_name.setText(phoneListElement.get(PhoneDictionary.NAME));
////            viewHolder.phone_info.setText(phoneListElement.get(PhoneDictionary.NUMBER) + " " + phoneListElement.get(PhoneDictionary.DATE));
////        }
////        else
////        {
////            viewHolder.phone_name.setText(phoneListElement.get(PhoneDictionary.NUMBER));
////            viewHolder.phone_info.setText(phoneListElement.get(PhoneDictionary.DATE));
////        }
////        viewHolder.phone_name.setTextSize(16);
////        viewHolder.phone_content.setIndex(position);
////        viewHolder.phone_content.setName(PhoneRegister.get(index).getPhoneListElementList().get(position).get(PhoneDictionary.NAME));
////        viewHolder.phone_content.setNumber(PhoneRegister.get(index).getPhoneListElementList().get(position).get(PhoneDictionary.NUMBER));
////        if(!PhoneOnClickListener.isSetUp())
////        {
////            PhoneOnClickListener.setContext(getContext());
////            PhoneOnClickListener.setSetUp(true);
////        }
////        viewHolder.phone_content.setOnClickListener(PhoneOnClickListener.getInstance());
////        return_btn view;
////    }
//
//    class ViewHolder
//    {
//        TextView contactName;
//        TextView contactPhone;
//        circleImage contactPhoto;
//    }
//}
