package hw.happyjacket.com.familycontactlist;

import android.accounts.NetworkErrorException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacket on 2016/4/11.
 */
public class HttpConnectionUtil {
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
            Log.d("url", address);
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

    public static void post(final String address, final HashMap<String, String> args, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    StringBuffer buf = new StringBuffer();
                    for (Map.Entry<String, String> entry : args.entrySet()) {
                        if (buf.length() > 0)
                            buf.append("&");
                        buf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF8"));
                    }

                    String data = buf.toString();
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.d("url", conn.getURL().toString());

                    //获取输出流
                    OutputStream os = conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    int responseCode = conn.getResponseCode();

                    if (responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int len;
                        byte buffer[] = new byte[1024];
                        while ((len = is.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }

                        is.close();
                        baos.close();

                        // 如果返回true，则表示post成功
                        String result = new String(baos.toByteArray());

                        if (listener != null)
                            listener.onFinish(result, null);
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
        }).start();
    }
}