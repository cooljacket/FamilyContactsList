package hw.happyjacket.com.familycontactlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.HashMap;

import hw.happyjacket.com.familycontactlist.bluetooth.PhoneBlueTooth;
import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.show.MainShow;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabRecordFragment extends Fragment {
    private String TAG = this.getClass().toString();
    private ListView listView;
    private PhoneBlueTooth phoneBlueTooth;
    private MainShow mainShow;
    private AlertDialog.Builder mPhoneBuilder;
    private Context mContext;
    private View tabView;

    public TabRecordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return tabView == null ? tabView = inflater.inflate(R.layout.tab_record_layout, container,false) : tabView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public void onStart()
    {
        if(mainShow == null) {
            init();
        }
        else {
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
            mainShow = new MainShow(getContext(),R.layout.phone_element);
            mainShow.InitAdapter(new XiaoMiAccessory(), DataBaseDictionary.CallLog_Projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            mPhoneBuilder = new AlertDialog.Builder(getContext());
            mPhoneBuilder.setIcon(R.mipmap.ic_launcher);
            mPhoneBuilder.setNegativeButton("取消", null);
            initListView();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //phoneBlueTooth = new PhoneBlueTooth(this);
        //phoneBlueTooth.startScan();

    }

    public void initListView()
    {
        listView = (ListView) getView().findViewById(R.id.phone_list);
        listView.setAdapter(mainShow.getPhoneAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new PhoneOperation(getContext()).call(mainShow.getPhoneListElementList().get(position).get(PhoneDictionary.NUMBER));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final HashMap<String,String> t = mainShow.getPhoneListElementList().get(position);
                mPhoneBuilder.setTitle(t.get(PhoneDictionary.NUMBER));
                mPhoneBuilder.setItems(PhoneDictionary.MainItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case 0:
                                new PhoneOperation(getContext()).delete(t.get(PhoneDictionary.ID));
                                PhoneRegister.delete(t.get(PhoneDictionary.ID), t.get(PhoneDictionary.NUMBER));
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                    }
                });
                mPhoneBuilder.show();
                return true;
            }
        });

    }

}