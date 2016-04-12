package hw.happyjacket.com.familycontactlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;
import java.util.zip.Inflater;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.MainShow;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabRecordFragment extends Fragment {

    private static final String TAG = TabRecordFragment.class.toString();
    private View recordView;
    private MainShow mainShow;
    private ListView listView;
    private ListView OptionListView;
    private Context mContext;
    private PhoneDialog option;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return recordView == null ? recordView = inflater.inflate(R.layout.tab_record_layout, container, false) : recordView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onStart() {

        if(mainShow == null) {
            init();
        }
        else{
            mainShow.refresh(new XiaoMiAccessory(),new String[]{PhoneDictionary.DATE});
        }
        super.onStart();

    }

    @Override
    public void onDestroy()
    {
        //  phoneBlueTooth.destroyBlueToothRegister();
        mainShow.destroy();
        PhoneRegister.unRegister(mainShow.getIndex());
        super.onDestroy();
    }

    public void init()
    {
        try {
            Operation.setContext(mContext);
            mainShow = new MainShow(mContext,R.layout.phone_element);
            mainShow.InitAdapter(new XiaoMiAccessory(), DataBaseDictionary.CallLog_Projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            initOption();
            initListView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //phoneBlueTooth = new PhoneBlueTooth(this);
        //phoneBlueTooth.startScan();

    }

    public void initOption()
    {
       /* View viewForOption = LayoutInflater.from(mContext).inflate(R.layout.main_option,null);
        OptionListView = (ListView)viewForOption.findViewById(R.id.main_list);
        Vector<String> data = new Vector<>();
        for(String i : PhoneDictionary.MainItems)
            data.add(i);
        ArrayAdapter<String> t = new ArrayAdapter<String>(mContext,android.R.layout.simple_expandable_list_item_1,data);
        OptionListView.setAdapter(t);
        OptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,String> q = mainShow.getPhoneListElementList().get(option.getPos());
                switch (position){
                    case 0:
                        Operation.delete(q.get(PhoneDictionary.ID),q.get(PhoneDictionary.NUMBER));
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                option.dismiss();
            }
        });*/
        option = DialogFactory.getPhoneDialog(mContext,R.layout.main_option,R.style.Menu,mainShow.getIndex(),PhoneDictionary.MainItems,PhoneDictionary.MAIN_OPTIONS);
    }

    public void initListView()
    {
        listView = (ListView) getView().findViewById(R.id.phone_list);
        listView.setAdapter(mainShow.getPhoneAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new PhoneOperation(mContext).call(mainShow.getPhoneListElementList().get(position).get(PhoneDictionary.NUMBER));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                option.setPos(position);
                option.show();
                return true;
            }
        });
    }





}