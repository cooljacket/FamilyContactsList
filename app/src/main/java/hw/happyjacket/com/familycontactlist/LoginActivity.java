package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/5.
 */

public class LoginActivity extends AppCompatActivity {
    public static final int LOGIN_RESULT = 777;
    public static final String LOGIN_RESULT_KEY = "login_result", LOGIN_REMEMBER_ME = "rememberMe";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        toolbar.setTitle("登录");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.arrow_back_selector);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        final CheckBox remember = (CheckBox) findViewById(R.id.cbRememberPwd);

        final EditText userName = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.pwd);

        userName.setText("18819461579");
        password.setText("233");

        Button register = (Button) findViewById(R.id.RegisterButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, LOGIN_RESULT);
            }
        });

        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = userName.getText().toString();
                if (uname == null || uname.length() == 0) {
                    CommonUtils.AlertMsg(LoginActivity.this, "输入非法", "用户名不能为空");
                    return ;
                }

                String pwd = password.getText().toString();
                if (pwd == null || pwd.length() == 0) {
                    CommonUtils.AlertMsg(LoginActivity.this, "输入非法", "密码不能为空");
                }

                HashMap<String, String> data = new HashMap<>();
                data.put("username", uname);
                data.put("psw", pwd);
                HttpConnectionUtil.post(CommonUtils.LOGIN_URL, data, new HttpConnectionUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response, String number) {
                        Log.d("activity_login", "" + response);
                        String result = "true";
                        if (response.contains("false"))
                           result = "false";
                        else {
                            SetToken(LoginActivity.this, response);
                            SetRememberMe(LoginActivity.this, remember.isChecked());
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra(LOGIN_RESULT_KEY, result);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        Message msg = new Message();
                        msg.obj = "请检查用户名密码或网络是否有错误";
                        handler.sendMessage(msg);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LoginActivity.LOGIN_RESULT:
                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    public static String getToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CommonUtils.TOKEN_SF, MODE_PRIVATE);
        return pref.getString(CommonUtils.TOKEN_KEY, null);
    }

    public static boolean getRememberState(Context context) {
        SharedPreferences pref = context.getSharedPreferences(CommonUtils.TOKEN_SF, MODE_PRIVATE);
        return pref.getBoolean(LOGIN_REMEMBER_ME, false);
    }

    public static void SetToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CommonUtils.TOKEN_SF, Context.MODE_PRIVATE).edit();
        if (token == null)
            editor.clear();
        else
            editor.putString(CommonUtils.TOKEN_KEY, token);
        editor.apply();
    }

    public static void SetRememberMe(Context context, boolean remember) {
        SharedPreferences.Editor editor = context.getSharedPreferences(CommonUtils.TOKEN_SF, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOGIN_REMEMBER_ME, remember);
        editor.apply();
    }

    public static void Logout(Context context) {
        SetToken(context, null);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CommonUtils.AlertMsg(LoginActivity.this, "登录出错", (String) msg.obj);
        }
    };
}



