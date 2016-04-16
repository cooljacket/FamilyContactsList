package hw.happyjacket.com.familycontactlist.myphonebook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 16-4-14.
 */
public abstract class RecyclerAdapter extends  RecyclerView.Adapter {

    private Context mContext;
    private int recourceID;
    private int index;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public int getRecourceID() {
        return recourceID;
    }

    public void setRecourceID(int recourceID) {
        this.recourceID = recourceID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public RecyclerAdapter(Context context, int recourceID, int index) {
        mContext = context;
        this.recourceID = recourceID;
        this.index = index;
    }

    public RecyclerAdapter(Context context, int recourceID) {
        mContext = context;
        this.recourceID = recourceID;
    }
}
