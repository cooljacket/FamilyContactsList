package hw.happyjacket.com.familycontactlist.bluetooth;

import android.os.ParcelUuid;

/**
 * Created by root on 16-3-28.
 */
public class BlueToothElement {
    private String name;
    private String address;
    private ParcelUuid[] uuid;

    public BlueToothElement(String name, String address, ParcelUuid[] uuid) {
        this.name = name;
        this.address = address;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ParcelUuid[] getUuid() {
        return uuid;
    }
    public void setUuid(ParcelUuid[] uuid) {
        this.uuid = uuid;
    }
}
