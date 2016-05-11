package hw.happyjacket.com.familycontactlist;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.extention.XiaoMiAccessory;
import hw.happyjacket.com.familycontactlist.myphonebook.InitService;
import hw.happyjacket.com.familycontactlist.myphonebook.Operation;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.PhoneDialog;
import hw.happyjacket.com.familycontactlist.myphonebook.show.MainShow;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneOperation;
import hw.happyjacket.com.familycontactlist.phone.database.DataBaseDictionary;
import hw.happyjacket.com.familycontactlist.phone.phonelistener.PhoneThreadCheck;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabRecordFragment extends PhoneFragment {

    private static final String TAG = TabRecordFragment.class.toString();
    private View recordView;
    private MainShow mainShow;
    private ListView listView;
    private ListView OptionListView;
    private Activity mContext;
    private PhoneDialog option;
    private FloatingActionButton Dial;
    private Handler mHandler;
    public static boolean closed = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return recordView == null ? recordView = inflater.inflate(R.layout.tab_record_layout, container, false) : recordView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        DialogFactory.Number = "";
    }

    @Override
    public void onStart() {
        if(mainShow == null) {
            init();
            // 初始化的时候可能还不够，有变化的时候也要及时更新这个量。。
            MainActivity.phoneElement = mainShow.getPhoneListElementList_backup();
        }
        else {
            mainShow.refresh(new XiaoMiAccessory(), new String[]{PhoneDictionary.DATE});
        }
        super.onStart();
    }

    @Override
    public void onDestroy()
    {
        //  phoneBlueTooth.destroyBlueToothRegister();
        mainShow.destroy();
        PhoneRegister.unRegister(mainShow.getIndex());
        closed = true;
        super.onDestroy();
    }

    public List<HashMap<String,String>> phoneElements(){
        return mainShow.getPhoneListElementList();
    }

    public void init()
    {
        try {

            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case PhoneDictionary.RADIO_OPTION:
                            Toast.makeText(mContext,msg.arg1 + "",Toast.LENGTH_SHORT).show();
                            break;
                        case PhoneDictionary.CHECKBOX_OPTION:
                            Vector<Boolean> t = (Vector<Boolean>)msg.obj;
                            Toast.makeText(mContext,t.toString(),Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }

                }
            };
            mainShow = new MainShow(mContext,R.layout.phone_element);
            mainShow.InitAdapter(new XiaoMiAccessory(), DataBaseDictionary.CallLog_Projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            String content [] = mainShow.getPhoneList().getProjection();
            Dial = (FloatingActionButton) recordView.findViewById(R.id.phone_fab);
            Dial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFactory.DiaWheel(mContext,R.style.Menu,mainShow).show();
                }
            });
            initOption();
            initListView();
           // initService();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //phoneBlueTooth = new PhoneBlueTooth(this);
        //phoneBlueTooth.startScan();

    }


    public long getTimeStamp(String number){
        Log.i(TAG,mainShow + "");
        Log.i(TAG,mainShow.getNmapp() + "");
        Integer id = mainShow.getNmapp().get(number);
        return id == null ? -1 : Long.parseLong(mainShow.getPhoneListElementList_backup().get(id).get(PhoneDictionary.DATE));
    }

    public void initOption()
    {
        option = DialogFactory.getPhoneDialog(mContext,R.layout.main_option,R.id.main_list,R.style.Menu,mainShow.getIndex(),PhoneDictionary.MainItems,PhoneDictionary.MAIN_OPTIONS);
    }

    public void initListView()
    {
        listView = (ListView) getView().findViewById(R.id.phone_list);
        listView.setAdapter(mainShow.getPhoneAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               new PhoneOperation(mContext).call(mainShow.getPhoneListElementList().get(position).get(PhoneDictionary.NUMBER));
               // DialogFactory.getCheckBoxDialog(getContext(), R.style.Menu, PhoneDictionary.MainItems, mHandler).show();
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


    @Override
    public void changePeopleDetail(User user, Bitmap picture) {
        mainShow.check();
    }

    @Override
    public void createPeopleDetail(User user, Bitmap picture) {
        mainShow.check();
    }

    @Override
    public void deletePeopleDetail(String number, int pos) {
        Integer t;
        if ( (t = mainShow.getNmapp().get(number)) != null){
            mainShow.getPhoneListElementList_backup().get(t).remove(PhoneDictionary.NAME);
            ContentValues contentValues = new ContentValues();
            contentValues.putNull(PhoneDictionary.NAME);
            mainShow.getPhoneList().update(contentValues,PhoneDictionary.NUMBER + " = ? ",new String[]{number});
        }
        for (int i = 0 ; i < mainShow.getPhoneListElementList().size(); ++i){
            if (mainShow.getPhoneListElementList().get(i).get(PhoneDictionary.NUMBER).equals(number)) {
                mainShow.getPhoneListElementList().get(i).remove(PhoneDictionary.NAME);
                break;
            }
        }
        mainShow.getPhoneAdapter().notifyDataSetChanged();
    }
}