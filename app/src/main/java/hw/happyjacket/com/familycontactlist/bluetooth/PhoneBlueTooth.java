package hw.happyjacket.com.familycontactlist.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.util.Scanner;
import java.util.Vector;



/**
 * Created by root on 16-3-28.
 */
public class PhoneBlueTooth {

    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private Handler handler;
    private String TAG = this.getClass().toString();
    private PhoneReceiver phoneReceiver;

    public PhoneBlueTooth(final Context context)
    {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = new Handler(){
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case PhoneDictionary.BLUETOOTH_FOUND:
                        Log.i(TAG, phoneReceiver.getDevices().get(phoneReceiver.getDevices().size() - 1).getName());
                        break;
                    case PhoneDictionary.BLUETOOTH_FINISHED:
                        Log.i(TAG, "Finished");
                       /* connect(0);*/
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        this.phoneReceiver = new PhoneReceiver(handler);
        initBlueToothAdapter();
    }


    public void initBlueToothAdapter()
    {
        bluetoothEnable();
        register();
        scanEnable(10);
    }

    public void bluetoothEnable()
    {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.context.startActivity(intent);
        }
    }

    public void register()
    {
        IntentFilter receiveFilter = new IntentFilter();
        receiveFilter.addAction(BluetoothDevice.ACTION_FOUND);
        receiveFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(phoneReceiver, receiveFilter);
    }

    public void scanEnable(int duration)
    {
        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        this.context.startActivity(discoverIntent);
    }

    public void destroyBlueToothRegister()
    {
        context.unregisterReceiver(phoneReceiver);
    }

    public void startScan() {bluetoothAdapter.startDiscovery();}

    public void connect(int i) {new Thread(new BlueToothThread(phoneReceiver.getDevices().get(i))).start();}

}
