package hw.happyjacket.com.familycontactlist.myphonebook.show;

import hw.happyjacket.com.familycontactlist.extention.Accessory;
import hw.happyjacket.com.familycontactlist.myphonebook.show.PhoneShow;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 16-4-1.
 */

/*The global class used to store the Show which is used to get data through phoneList and generate the adapter*/
public class PhoneRegister {

    private static Vector<PhoneShow> phoneTable = new Vector<>();


    public static int register(PhoneShow phoneShow)
    {
        phoneTable.add(phoneShow);
        return phoneTable.size() - 1;
    }

    public static void unRegister(int position)
    {
        try {
            phoneTable.removeElementAt(position);
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
        }
    }

    public static PhoneShow get(int position)
    {
        return phoneTable.get(position);
    }

    public static void add(Accessory accessory,HashMap<String,String> newGuy)
    {
        for(PhoneShow i : phoneTable)
            i.AddPhoneElement(accessory,newGuy);
    }

    public static void delete(Boolean first,String id, String number)
    {
        for(PhoneShow i : phoneTable)
            i.DeletePhoneElement(first,id,number);
    }

}
