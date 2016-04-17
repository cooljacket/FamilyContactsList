package hw.happyjacket.com.familycontactlist.myphonebook.factory;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.myphonebook.adapter.PhoneAdapter;

/**
 * Created by root on 16-4-14.
 */
public class PhoneDialogProxy {
    private RecyclerView mRecyclerView;
    private PhoneDialog mDialog;
    private RecyclerView.Adapter mPhoneAdapter;
    private Vector<HashMap<String,String>> element;

    public PhoneDialogProxy(Context context,int layout, int viewid, int recourceid,RecyclerView recyclerView, PhoneDialog dialog, Vector<HashMap<String, String>> element) {
        mRecyclerView = recyclerView;
        mDialog = dialog;
        this.element = element;
       // mPhoneAdapter = new PhoneAdapter(context,recourceid,element);

    }
}
