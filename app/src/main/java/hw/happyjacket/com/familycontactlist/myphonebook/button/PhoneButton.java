package hw.happyjacket.com.familycontactlist.myphonebook.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by root on 16-4-1.
 */

/*One kind of button which can save name and number*/
public class PhoneButton extends Button{

    private int index = -1;

    private String name;

    private String number;

    public PhoneButton(Context context) {
        super(context);
    }

    public PhoneButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
