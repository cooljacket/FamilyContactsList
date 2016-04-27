package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Xml;

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
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jpinyin.stuxuhai.github.com.PinyinHelper;

/**
 * Created by jacket on 2016/4/19.
 */
public class CommonSettingsAndFuncs {
    public final static String HostURL = "http://webservice.webxml.com.cn";
    public final static String GetWeatherURLFormat = "/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=";
    public final static String Spliter = "&&";
    public static String FileHeader;

    // 将字符串转换成指定的字符集格式（这里用到的是utf8）
    public static String changeCharset(String str, String newCharset) {
        if (str != null) {
            try {
                byte[] bytes = str.getBytes(newCharset);
                String newStr = "";
                for (int i = 0; i < bytes.length; ++i)
                    newStr += String.format("%%%X", bytes[i]);
                Log.d("response newStr", newStr);
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

    public static String ParseWeatherXML(InputStream xml) throws XmlPullParserException, IOException {
        try {
            ArrayList<String> weather = ParseXMLHelper(xml);
            for (int i = 0; i < weather.size(); ++i)
                Log.d("index " + i, weather.get(i));
            String location = weather.get(1);
            String wea = weather.get(7).split(" ")[1];
            String temperature = weather.get(8);
            String sun = weather.get(5).replace("：", "").replace("。", "，");

            String content = weather.get(6);
            Pattern p = Pattern.compile("(感冒指数：[^，]*，([^。]*)。)(穿衣指数：[^，]*，([^。]*)。)(洗车指数：[^。]*。)(运动指数：[^，]*，([^。]*)。)");
            Matcher m = p.matcher(content);
            if (m.find())
                content = m.group(2) + "；" + m.group(4) + "；" + m.group(7);

            for (int i = 1; i <= m.groupCount(); ++i)
                Log.d("index " + i, m.group(i));

            return String.format("%s，今天%s%s，气温%s，%s%s。注意好身体，爱你们。", location, wea, temperature, sun, content).replaceAll("您", "");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static String ExportContacts(Context context, String dirName) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (!db.isOpen())
            return null;

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
        String date  = sDateFormat.format(new java.util.Date());
        String fileName = dirName + File.separator + date + randomString(6) + ".txt";
        Log.d("hehe", fileName);

        try {
            out = new FileOutputStream(fileName);
            writer = new BufferedWriter(new OutputStreamWriter(out));

            while (cursor.moveToNext()) {
                List<String> data = new ArrayList<>();
                data.add(cursor.getString(name_idx));
                data.add(cursor.getString(sortname_idx));
                data.add(cursor.getString(mobilephone_idx));
                data.add("" + cursor.getInt(photo_idx));
                data.add(cursor.getString(groupname_idx));
                data.add(cursor.getString(info_idx));
                writer.write(joinStrs(data, CommonSettingsAndFuncs.Spliter));
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            dbHelper.close();
        }

        return fileName;
    }

    public static Vector<User> ImportContacts(Context context, String fileName) {
        DBHelper dbHelper = new DBHelper(context);
        FileInputStream input = null;
        BufferedReader reader = null;
        Vector<User> newUsers = new Vector<>();

        try {
            input = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(CommonSettingsAndFuncs.Spliter);
                for (int i = 0; i < data.length; ++i)
                    if (data[i].equals("null"))
                        data[i] = "";
                User user = dbHelper.insertFromStrings(data);
                if (user != null)
                    newUsers.add(user);
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

    public static String convertToPinyin(Context context, String str) {
        PinyinHelper.getInstance(context);
        return PinyinHelper.convertToPinyinString(str, "");
    }
}