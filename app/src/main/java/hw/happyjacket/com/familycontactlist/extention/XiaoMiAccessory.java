package hw.happyjacket.com.familycontactlist.extention;

import android.util.Log;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 16-3-28.
 */
public class XiaoMiAccessory implements Accessory{
    @Override
    public String decorate(String key, String value) {
        String result;
        switch (key)
        {

            case PhoneDictionary.DATE:
                SimpleDateFormat years = new SimpleDateFormat("yyyy年MM月dd日");
                SimpleDateFormat days = new SimpleDateFormat("MM月dd日");
                Date currentDate = new Date();
                Date phoneDate = new Date(Long.parseLong(value));
                if(phoneDate.getYear()< currentDate.getYear())
                    result = years.format(phoneDate);
                else if(phoneDate.getMonth() < currentDate.getMonth() || phoneDate.getDate() < currentDate.getDate())
                    result = days.format(phoneDate);
                else if(phoneDate.getHours() < currentDate.getHours())
                    result = String.valueOf(currentDate.getHours() - phoneDate.getHours()) + "小时";
                else if(phoneDate.getMinutes() < currentDate.getMinutes())
                    result = String.valueOf(currentDate.getMinutes() - phoneDate.getMinutes()) + "分钟";
                else
                    result = String.valueOf(currentDate.getSeconds() - phoneDate.getSeconds()) + "秒";
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
