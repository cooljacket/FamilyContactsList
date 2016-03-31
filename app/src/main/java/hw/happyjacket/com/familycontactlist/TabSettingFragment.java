package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabSettingFragment extends Fragment {
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_settings_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        BlackListHandler handler = new BlackListHandler(mContext);
        handler.add("188");
        handler.add("233");
        handler.add("555");

        List<String> list = handler.getTheBlackList();
        for (int i = 0; i < list.size(); ++i)
            Log.d("black list", list.get(i));
    }
}