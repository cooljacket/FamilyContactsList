package hw.happyjacket.com.familycontactlist.extention;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 16-3-27.
 */
public class DefaultAccessory implements Accessory{

    @Override
    public String decorate(String key, String value) {
            String result;
            switch (key)
            {
                case PhoneDictionary.NAME:
                    result = (value == null ? "Unknown" : value);
                    break;
                case PhoneDictionary.DATE:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = new Date(Long.parseLong(value));
                    result = simpleDateFormat.format(date).toString();
                    break;
                case PhoneDictionary.TYPE:
                    result = PhoneDictionary.TYPE_LIST[value.charAt(0) - '0'];
                    break;
                default:
                    result = value;
                    break;
            }
            return result;
    }

}
