package hw.happyjacket.com.familycontactlist;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by leo on 2016/4/10.
 */
public class User implements Serializable{
    public int uid;
    public int photo;
    public int pos;
    public String name;
    public String sortname;
    public String mobilephone;
    public String groupname ;
    public String info;
    public String nickname;

    public User() {
        uid = photo = pos = -2;
    }

    public void update(User users){
        uid = users.uid == -2 ? uid : users.uid;
        photo = users.photo == -2 ? photo : users.photo;
        name = users.name == null ? name : users.name;
        sortname = users.sortname == null ? sortname : users.sortname;
        mobilephone = users.mobilephone ==  null ? mobilephone : users.mobilephone;
        groupname = users.groupname == null ? groupname : users.groupname;
        info = users.info == null ? info : users.info;
        nickname = users.name == null ? nickname : users.nickname;
    }

    public User(int uid, int photo, String name, String sortname, String mobilephone, String groupname, String info, String nickname) {
        this.uid = uid;
        this.photo = photo;
        this.name = name;
        this.sortname = sortname;
        this.mobilephone = mobilephone;
        this.groupname = groupname;
        this.info = info;
        this.nickname = nickname;
    }
}