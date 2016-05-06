package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by jacket on 2016/5/4.
 */
public class ContactsDataUtils {
    public static final int IMPORT_FROM_WEB = 233, EXPORT_TO_WEB = 234;

    public static void ExportContactsToLocalFile(Context context, String dirName) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        String fileName = dirName + File.separator + date + CommonUtils.randomString(6) + ".txt";
        JSONArray data = ExportAsJSONArray(context);
        if (data != null)
            WriteJsonArray(fileName, data);
    }

    public static void ExportContactsToWeb(Context context, final Handler handler) {
        String token = LoginActivity.getToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            return ;
        }

        JSONArray data = ExportAsJSONArray(context);
        if (data == null)
            return ;

        HashMap<String, String> args = new HashMap<>();
        args.put("data", data.toString());
        args.put("token", token);
        // post to web
        HttpConnectionUtil.post(CommonUtils.EXPORT_URL, args, new HttpConnectionUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response, String number) {
                Log.d("post-response", response);
                Message msg = new Message();
                msg.what = EXPORT_TO_WEB;
                msg.obj = response;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public static JSONArray ExportAsJSONArray(Context context) {
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
        JSONArray data = new JSONArray();

        try {
            while (cursor.moveToNext()) {
                JSONObject one = new JSONObject();
                one.put("name", cursor.getString(name_idx));
                one.put("sortname", cursor.getString(sortname_idx));
                one.put("mobilephone", cursor.getString(mobilephone_idx));
                one.put("photo", cursor.getInt(photo_idx));
                one.put("groupname", cursor.getString(groupname_idx));
                one.put("info", cursor.getString(info_idx));
                if (!one.has("info"))
                    one.put("info", "");
                data.put(one);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            db.close();
            dbHelper.close();
        }

        return data;
    }

    public static void WriteJsonArray(String fileName, JSONArray data) {
        FileOutputStream out = null;
        BufferedWriter writer = null;

        try {
            out = new FileOutputStream(fileName);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data.toString());
            writer.flush();
            if (writer != null)
                writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Vector<User> ImportContactsFromLocalFile(Context context, String fileName) {
        FileInputStream input = null;
        BufferedReader reader = null;
        Vector<User> newUsers = null;

        try {
            input = new FileInputStream(fileName);
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuffer buf = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }

            newUsers = ImportWithJSONText(context, buf.toString());

            if (reader != null)
                reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newUsers;
    }

    public static void ImportContactsFromWeb(final Context context, final Handler handler) {
        String token = LoginActivity.getToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
            return ;
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("token", token);
        HttpConnectionUtil.post(CommonUtils.IMPORT_URL, data, new HttpConnectionUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response, String number) {
                Message msg = new Message();
                Log.d("post-result", "" + response);
                msg.obj = ImportWithJSONText(context, response);
                msg.what = IMPORT_FROM_WEB;
                Log.d("hehe-web ", response + "");
                handler.sendMessage(msg);
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public static Vector<User> ImportWithJSONText(Context context, String content) {
        DBHelper dbHelper = new DBHelper(context);
        Vector<User> newUsers = new Vector<>();

        JSONTokener jsonParser = new JSONTokener(content);
        Log.d("import-content", content);
        User user = new User();

        try {
            JSONArray data = (JSONArray) jsonParser.nextValue();
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
                } else {
                    Log.d("haha-old", user.name + ", " + user.mobilephone);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newUsers;
    }
}
