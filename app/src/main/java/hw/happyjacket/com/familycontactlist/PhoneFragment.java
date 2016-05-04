package hw.happyjacket.com.familycontactlist;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

/**
 * Created by root on 16-5-5.
 */
public abstract class PhoneFragment extends Fragment {

    public abstract void changePeopleDetail(User user, Bitmap picture);

    public abstract void createPeopleDetail(User user, Bitmap picture);
}
