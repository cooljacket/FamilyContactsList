package hw.happyjacket.com.familycontactlist.extention;

import net.sourceforge.pinyin4j.PinyinHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by root on 16-4-27.
 */
public class ChineseWord {

    public static boolean isChinese(char word){
        String regex = "[\\u4e00-\\u9fa5]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(String.valueOf(word));
        return matcher.find();
    }

    public static char Pinyin(char word){
        String [] pinyin = PinyinHelper.toHanyuPinyinStringArray(word);
        return pinyin[0].charAt(0);
    }

    public static String Pinyin(String name){
        StringBuffer buffer = new StringBuffer();
        char t,u;
        for(int i = 0 ; i < name.length() ; ++i){
            if(isChinese(t = name.charAt(i))){
                buffer.append(Pinyin(t));
            }
            else if('a' <= (u = Character.toLowerCase(t)) && u <= 'z'){
                buffer.append(t);
            }
        }
        return buffer.toString();
    }
}
