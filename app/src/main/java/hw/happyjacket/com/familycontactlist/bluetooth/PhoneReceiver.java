package hw.happyjacket.com.familycontactlist.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.Vector;


/**
 * Created by root on 16-3-29.
 */
public class PhoneReceiver extends BroadcastReceiver {


    private Vector<BluetoothDevice> devices = new Vector<>();
    private Handler handler;

    public PhoneReceiver(Handler handler) {
        this.handler = handler;
    }

    public Vector<BluetoothDevice> getDevices() {
        return devices;
    }

    public void setDevices(Vector<BluetoothDevice> devices) {
        this.devices = devices;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Message msg = Message.obtain();
        switch (action)
        {
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                msg.what = PhoneDictionary.BLUETOOTH_FOUND;
                handler.sendMessage(msg);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                msg.what = PhoneDictionary.BLUETOOTH_FINISHED;
                handler.sendMessage(msg);
                break;
            default:
                break;
        }

    }
}

