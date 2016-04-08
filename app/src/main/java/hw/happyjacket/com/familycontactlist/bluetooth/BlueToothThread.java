package hw.happyjacket.com.familycontactlist.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;


/**
 * Created by root on 16-3-28.
 */
public class BlueToothThread implements Runnable {


    private  BluetoothDevice dev;
    private static String uuid = "00001101-0000-1000-8000-00805F9B34FB";
    public BlueToothThread(BluetoothDevice dev) {
        this.dev = dev;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        BluetoothAdapter bluetoothAdapter = null;
        Method method;
        boolean isConnect;
        try
        {
            socket = dev.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
            socket.connect();
            isConnect = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
