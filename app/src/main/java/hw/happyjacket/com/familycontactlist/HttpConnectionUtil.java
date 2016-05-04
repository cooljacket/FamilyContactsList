package hw.happyjacket.com.familycontactlist;

import android.accounts.NetworkErrorException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jacket on 2016/4/11.
 */
public class HttpConnectionUtil {
    private static String arg;

    public interface HttpCallbackListener {
        void onFinish(String response, String number);
        void onError(Exception e);
    }

    public static void get(final String address, final String[] args, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getIt(address, args, listener);
            }

        }).start();
    }

    public static void getIt(final String address, final String[] args, final HttpCallbackListener listener) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            int responseCode = conn.getResponseCode();
            Log.d("responseCode", "" + responseCode);
            if (responseCode == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    response.append(line);
                if (listener != null) {
                    if (args == null)
                        listener.onFinish(response.toString(), "");
                    else
                        listener.onFinish(response.toString(), args[0]);
                }
            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.onError(e);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }
}
