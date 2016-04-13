package hw.happyjacket.com.familycontactlist.myphonebook;

import android.content.Intent;
import android.view.View;

/**
 * Created by root on 16-4-13.
 */
public class PhoneDial implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        
    }
}
