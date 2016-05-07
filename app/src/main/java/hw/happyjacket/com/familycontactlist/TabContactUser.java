package hw.happyjacket.com.familycontactlist;

import android.graphics.Bitmap;

/**
 * Created by root on 16-5-4.
 */
public class TabContactUser extends User {
    public Bitmap picture;

    public TabContactUser(){
        super();
    }

    public TabContactUser (TabContactUser user){
        this.update(user);
        this.picture = user.picture;
    }
}
