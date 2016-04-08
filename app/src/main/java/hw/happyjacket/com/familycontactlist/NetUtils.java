package hw.happyjacket.com.familycontactlist;

import android.accounts.NetworkErrorException;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jacket on 2016/4/7.
 */
public class NetUtils {
    public static int GET_LOCATION_INFO = 0, GET_WEATHER_INFO = 1;
    private static String TAG = "response";


    public static String get(String url, int code) {
        Log.d(TAG + " see", url);
        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                Log.d(TAG, "200 OK");

                if (code == AsynNetUtils.GET_WEATHER_INFO) {
                    return ParseWeatherXML(is);
                }
                else if (code == AsynNetUtils.GET_LOCATION_INFO) {
                    return ParseLocationXML(is);
                }
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
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
        ArrayList<String> weather = ParseXMLHelper(xml);
//        return weather.get(4) + " " + weather.get(6) + " " + weather.get(7);
        String content = weather.get(6);
        Pattern p = Pattern.compile("紫外线指数：([^。]*)。");
        Matcher m = p.matcher(content);
        if (m.find())
            return m.group(0);
        else
            return "2333" + content;
    }

    public static String ParseLocationXML(InputStream xml) throws XmlPullParserException, IOException {
        try {
            String location = new String(getDataFromInputStream(xml));
            location = location.replaceAll("<[^>]+>", "");
            return location.split(" ")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    //从流中读取数据
    public static byte[] getDataFromInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
