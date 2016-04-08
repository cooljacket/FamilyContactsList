package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by jacket on 2016/4/6.
 */
public class AsynNetUtils {
    private Context mContext;
    public static int GET_LOCATION_INFO = 0, GET_WEATHER_INFO = 1;

    AsynNetUtils(Context context) {
        mContext = context;
    }

    public interface Callback {
        void onResponse(String response);
    }

    public void get(final String url, final int code, final Callback callback){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.get(url, code);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public void setInfomation(final String locationURL, final String weatherURL) {
        get(locationURL, GET_LOCATION_INFO, new Callback() {
            @Override
            public void onResponse(String response)  {
                Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                Log.d("response location", response + "233");

                // 根据已经拿到的城市，获取相应的天气数据
                get(String.format(weatherURL, changeCharset(response, "UTF-8")), GET_WEATHER_INFO, new Callback() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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
}
