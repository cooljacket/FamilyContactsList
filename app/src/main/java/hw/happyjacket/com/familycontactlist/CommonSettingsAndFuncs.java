package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
public class CommonSettingsAndFuncs {
    public final static String HostURL = "http://webservice.webxml.com.cn";
    public final static String GetWeatherURLFormat = "/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=";
    public final static String Spliter = "||||";
    public static String FileHeader;
    public static final int NUMBER_INPUT = 0, CHINESE_INPUT = 1, EN_INPUT = 2, UNKNOWN = -1;

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

    public static void ExportContacts(Context context, String dirName) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (!db.isOpen())
            return ;

        Cursor cursor = db.query(DBHelper.DB_TABLENAME, null, null, null, null, null, null);
        int name_idx = cursor.getColumnIndex("name");
        int sortname_idx = cursor.getColumnIndex("sortname");
        int mobilephone_idx = cursor.getColumnIndex("mobilephone");
        int photo_idx = cursor.getColumnIndex("photo");
        int groupname_idx = cursor.getColumnIndex("groupname");
        int info_idx = cursor.getColumnIndex("info");

        FileOutputStream out = null;
        BufferedWriter writer = null;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        String fileName = dirName + File.separator + date + randomString(6) + ".txt";
        JSONObject AllContacts = new JSONObject();
        JSONArray data = new JSONArray();

        try {
            out = new FileOutputStream(fileName);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            while (cursor.moveToNext()) {
                JSONObject one = new JSONObject();
                one.put("name", cursor.getString(name_idx));
                one.put("sortname", cursor.getString(sortname_idx));
                one.put("mobilephone", cursor.getString(mobilephone_idx));
                one.put("photo", cursor.getInt(photo_idx));
                one.put("groupname", cursor.getString(groupname_idx));
                one.put("info", cursor.getString(info_idx));
                data.put(one);
            }

            writer.write(data.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            dbHelper.close();
        }
    }

    public static Vector<User> ImportContacts(Context context, String fileName) {
        DBHelper dbHelper = new DBHelper(context);
        FileInputStream input = null;
        BufferedReader reader = null;
        Vector<User> newUsers = new Vector<>();

        try {
            input = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuffer buf = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }

            JSONTokener jsonParser = new JSONTokener(buf.toString());
            JSONArray data = (JSONArray) jsonParser.nextValue();
            User user = new User();

            for (int i = 0; i < data.length(); ++i) {
                JSONObject one = (JSONObject) data.get(i);
                user.name = one.getString("name");
                user.sortname = one.getString("sortname");
                user.mobilephone = one.getString("mobilephone");
                user.photo = one.getInt("photo");
                user.groupname = one.getString("groupname");
                if (one.has("info"))
                    user.info = one.getString("info");

                // 不知道为什么，10086删除后，还在？
                if (dbHelper.insertAUser(user)) {
                    newUsers.add(user);
                    Log.d("haha-new", user.name + ", " + user.mobilephone);
                }
            }

            if (reader != null)
                reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }

        return newUsers;
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

    public static boolean KMP_match(String text, String pattern) {
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

        return j == pLen;
    }

    public static Vector<Integer> SearchAmongRecords(List<HashMap<String, String>> data, String pattern) {
        Vector<Integer> result = new Vector<>();
        for (int i = 0; i < data.size(); ++i) {
            String phoneNumber = data.get(i).get(PhoneDictionary.NUMBER);
            if ((KMP_match(phoneNumber, pattern))) {
                result.add(i);
            }
        }
        return result;
    }

    public static Vector<Integer> SearchAmongContacts(List<User> data, String pattern) {
        Vector<Integer> result = new Vector<>();
        int input_type = JudgePatternType(pattern);
        if (input_type == EN_INPUT)
            pattern = pattern.toLowerCase();

        for (int i = 0; i < data.size(); ++i) {
            User user = data.get(i);
            switch (input_type) {
                case NUMBER_INPUT:
                    if (KMP_match(user.mobilephone, pattern))
                        result.add(i);
                    break;
                case CHINESE_INPUT:
                    if (KMP_match(user.name, pattern))
                        result.add(i);
                    break;
                case EN_INPUT:
                    if (KMP_match(user.sortname, pattern))
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

        char ch = pattern.charAt(0);
        if (ch >= '0' && ch <= '9')
            return NUMBER_INPUT;
        if (ChineseHelper.isChinese(ch))
            return CHINESE_INPUT;
        return EN_INPUT;
    }


}