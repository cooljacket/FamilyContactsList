package hw.happyjacket.com.familycontactlist;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final EditText username = (EditText) findViewById(R.id.register_uname);
        final EditText psw1 = (EditText) findViewById(R.id.register_psw1);
        final EditText psw2 = (EditText) findViewById(R.id.register_psw2);

        final Button reset = (Button) findViewById(R.id.btnReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText("");
                psw1.setText("");
                psw2.setText("");
            }
        });

        Button submit = (Button) findViewById(R.id.btnRegister_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String psw = psw1.getText().toString();
                if (uname.equals("null") || uname.length() == 0) {
                    LoginActivity.AlertMsg(RegisterActivity.this, "注册错误", "请输入用户名");
                    return ;
                }

                if (psw.equals("null") || uname.length() == 0) {
                    LoginActivity.AlertMsg(RegisterActivity.this, "注册错误", "请输入密码");
                    return ;
                }

                if (!psw.equals(psw2.getText().toString())) {
                    LoginActivity.AlertMsg(RegisterActivity.this, "注册错误", "两次输入的密码不一致");
                    return ;
                }

                HashMap<String, String> data = new HashMap<>();
                data.put("username", uname);
                data.put("psw", psw);
                HttpConnectionUtil.post(CommonUtils.REGISTER_URL, data, new HttpConnectionUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(String response, String number) {
                        Log.d("activity_register", "" + response);

                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getString("status").equals("true")) {
                                LoginActivity.SetToken(RegisterActivity.this, data.getString("token"));
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Message msg = new Message();
                                msg.obj = data.getString("msg");
                                handler.sendMessage(msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        LoginActivity.AlertMsg(RegisterActivity.this, "登录出错", "请检查用户名密码或网络是否有错误");
                    }
                });
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LoginActivity.AlertMsg(RegisterActivity.this, "注册出错", (String) msg.obj);
        }
    };
}