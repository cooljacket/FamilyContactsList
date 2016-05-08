package hw.happyjacket.com.familycontactlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hw.happyjacket.com.familycontactlist.phone.PhoneDictionary;
import jpinyin.stuxuhai.github.com.ChineseHelper;
import jpinyin.stuxuhai.github.com.PinyinHelper;

/**
 * Created by jacket on 2016/4/19.
 */
public class CommonUtils {
    public final static String W_Host = "http://webservice.webxml.com.cn";
    public final static String GetWeatherURLFormat = "/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=";
    public static final String OurHost = "http://188.166.252.165:8080", EXPORT_URL = OurHost + "/?r=item/saveItems", IMPORT_URL = OurHost + "/?r=item/getItems";
    public static final String LOGIN_URL = OurHost + "/?r=site/login", REGISTER_URL = OurHost + "/?r=site/register";
    public static final String TOKEN_SF = "FM_TOKEN", TOKEN_KEY = "token";
    public static final int NUMBER_INPUT = 0, CHINESE_INPUT = 1, EN_INPUT = 2, VOID = 3, UNKNOWN = -1;
    public static final String HAS_LOGIN = "退出登录", TO_LOGIN = "登录/注册";

    // 将字符串转换成指定的字符集格式（这里用到的是utf8）
    public static String changeCharset(String str, String newCharset) {
        if (str != null) {
            try {
                byte[] bytes = str.getBytes(newCharset);
                String newStr = "";
                for (int i = 0; i < bytes.length; ++i)
                    newStr += String.format("%%%X", bytes[i]);
                return newStr;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<String> ParseXMLHelper(InputStream xml) throws XmlPullParserException, IOException {
        ArrayList<String> contents = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "ArrayOfString");
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "string");
            contents.add(parser.nextText());
            if (parser.getEventType() != XmlPullParser.END_TAG) {
                parser.nextTag();
            }
            parser.require(XmlPullParser.END_TAG, null, "string");
        }
        parser.require(XmlPullParser.END_TAG, null, "ArrayOfString");

        return contents;
    }

    public static String[] ParseWeatherXML(InputStream xml) throws XmlPullParserException, IOException {
        try {
            ArrayList<String> weather = ParseXMLHelper(xml);
            String location = weather.get(1);
            String wea = weather.get(7).split(" ")[1];
            String temperature = weather.get(8);
            String sun = weather.get(5).replace("：", "").replace("。", "，");

            String content = weather.get(6);
            Pattern p = Pattern.compile("(感冒指数：[^，]*，([^。]*)。)(穿衣指数：[^，]*，([^。]*)。)(洗车指数：[^。]*。)(运动指数：[^，]*，([^。]*)。)");
            Matcher m = p.matcher(content);
            if (m.find())
                content = m.group(2) + "；" + m.group(4) + "；" + m.group(7);

            String[] result = new String[] {
                    String.format("%%s，今天%s%s，气温%s，%s%s。注意好身体，爱你们。", location, wea, temperature, sun, content).replaceAll("您", ""),
                    location, wea, temperature, sun, content
            };

            return result;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String joinStrs(List<String> data, String spliter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.size(); ++i) {
            if (i > 0)
                builder.append(spliter);
            builder.append(data.get(i));
        }
        builder.append("\n");
        return builder.toString();
    }

    public static String randomString(int len) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < len; ++i)
            buffer.append((char)('a' + random.nextInt(26)));
        return buffer.toString();
    }

    public static String convertToShortPinyin(Context context, String str) {
        PinyinHelper.getInstance(context);
        return PinyinHelper.getShortPinyin(str);
    }

    private static int[] calNext(String pattern) {
        if(pattern == null || pattern.equals(""))
            return null;
        int j = 0, k = -1, pLen = pattern.length();
        int[] next = new int[pLen];
        next[0] = -1;

        while (j < pLen-1) {
            if (k == -1 || pattern.charAt(k) == pattern.charAt(j))
                next[++j] = ++k;
            else
                k = next[k];
        }

        return next;
    }

    public static int KMP_match(String text, String pattern) {
        int[] next = calNext(pattern);
        int i = 0, j = 0, pLen = pattern.length(), tLen = text.length();

        while (j < pLen && i < tLen) {
            if (j == -1 || text.charAt(i) == pattern.charAt(j)) {
                ++i;
                ++j;
            }
            else {
                j = next[j];
            }
        }

        return j == pLen ? i-j : -1;
    }

    public static Vector<Integer> SearchAmongRecords(List<HashMap<String, String>> data, String pattern, Vector<Integer> position) {
        Vector<Integer> result = new Vector<>();
        Vector<Integer> keys = new Vector<>();

        for (int i = 0; i < data.size(); ++i) {
            String phoneNumber = data.get(i).get(PhoneDictionary.NUMBER);
            int pos = KMP_match(phoneNumber, pattern);
            if (pos < 0)
                continue;

            int index = keys.size();
            while (--index >= 0 && pos < keys.get(index));
            ++index;
            keys.insertElementAt(pos, index);
            result.insertElementAt(i, index);
        }
        position.addAll(keys);
        return result;
    }

    public static Vector<Integer> SearchAmongContacts(List<User> data, String pattern) {
        Vector<Integer> result = new Vector<>();
        int input_type = JudgePatternType(pattern);
        if (input_type == VOID){
            for (int i = 0 ; i < data.size(); ++i)
                result.add(i);
            return result;
        }
        if (input_type == EN_INPUT)
            pattern = pattern.toLowerCase();

        for (int i = 0; i < data.size(); ++i) {
            User user = data.get(i);
            switch (input_type) {
                case NUMBER_INPUT:
                    if (KMP_match(user.mobilephone, pattern) >= 0)
                        result.add(i);
                    break;
                case CHINESE_INPUT:
                    if (KMP_match(user.name, pattern) >= 0)
                        result.add(i);
                    break;
                case EN_INPUT:
                    if (KMP_match(user.sortname, pattern) >= 0)
                        result.add(i);
                    break;
                default:
                    break;
            }

        }
        return result;
    }

    public static int JudgePatternType(String pattern) {
        if (pattern == null)
            return UNKNOWN;
        if(pattern.length() == 0)
            return VOID;

        char ch = pattern.charAt(0);
        if (ch >= '0' && ch <= '9')
            return NUMBER_INPUT;
        if (ChineseHelper.isChinese(ch))
            return CHINESE_INPUT;
        return EN_INPUT;
    }


    public static void AlertMsg(Context context, String title, String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("俺知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}