package hw.happyjacket.com.familycontactlist.myphonebook.layout;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.R;
import hw.happyjacket.com.familycontactlist.myphonebook.factory.DialogFactory;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/**
 * Created by root on 16-4-28.
 */
public class PeopleLayout extends LinearLayout {

    private Handler mHandler;
    private int index;
    private List<String> content;
    private String [] have;
    private int style;
    private int change;
    private Activity mContext;

    public PeopleLayout(Context context) {
        super(context);
    }

    public PeopleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PeopleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public Activity getMContext() {
        return mContext;
    }

    public void setMContext(Activity context) {
        mContext = context;
    }

    public String[] getHave() {
        return have;
    }

    public void setHave(String[] have) {
        this.have = have;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       /* DialogFactory.getCheckBoxDialog(mContext, style, content, have, mHandler, index,change).show();*/
        return false;
    }
}
