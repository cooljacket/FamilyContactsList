package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import hw.happyjacket.com.familycontactlist.HttpConnectionUtil;
import hw.happyjacket.com.familycontactlist.PhoneLocationDBHelper;
import hw.happyjacket.com.familycontactlist.PhoneLocationMaster;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneRegister;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;
import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

/*
 * Created by root on 16-4-8.
*/

public class InitService extends Service {

    private PhoneLocationMaster PLMaster = new PhoneLocationMaster(this);

    private boolean errorOfGetingLocation = false;

    private int index;

    private MyBinder mMyBinder = new MyBinder();




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMyBinder;
    }


    public class MyBinder extends Binder{

        public void stop(){
            stopSelf();
        }
    }




}
