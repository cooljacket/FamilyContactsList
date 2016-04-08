package hw.happyjacket.com.familycontactlist.extention;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by root on 16-3-31.
 */
public class Decorate {

    private Accessory mAccessory;

    public Decorate(Accessory accessory)
    {
        mAccessory = accessory;
    }

    public HashMap<String,String> decorate(HashMap<String,String> t)
    {
        for(Map.Entry<String,String> i : t.entrySet())
            i.setValue(mAccessory.decorate(i.getKey(),i.getValue()));
        return t;
    }

    public Vector<HashMap<String,String>> decorate(Vector<HashMap<String,String>> t)
    {
        for(HashMap<String,String> i : t)
            for (Map.Entry<String,String> j : i.entrySet())
                j.setValue(mAccessory.decorate(j.getKey(),j.getValue()));
        return t;
    }
}
