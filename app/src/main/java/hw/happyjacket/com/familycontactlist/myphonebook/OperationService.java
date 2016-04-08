package hw.happyjacket.com.familycontactlist.myphonebook;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import hw.happyjacket.com.familycontactlist.phone.PhoneList;
import hw.happyjacket.com.familycontactlist.phone.database.DataBase;

/**
 * Created by root on 16-4-8.
 */
public class OperationService extends Service {

    private OperationBinder mOperationBinder = new OperationBinder();

    public OperationBinder getOperationBinder() {
        return mOperationBinder;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mOperationBinder;
    }

    public class OperationBinder extends Binder{

        public void delete(final String id, final PhoneList phoneList) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    phoneList.delete(id);
                }
            }).start();
        }
        public PhoneList deleteAll(final String number, final PhoneList phoneList){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    phoneList.deleteAll(number);
                }
            }).start();
            return phoneList;
        }

    }
}
