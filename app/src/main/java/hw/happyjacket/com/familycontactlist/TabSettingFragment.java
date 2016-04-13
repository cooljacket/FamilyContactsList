package hw.happyjacket.com.familycontactlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by jacket on 2016/3/30.
 */
public class TabSettingFragment extends Fragment {
    private String HostURL = "http://webservice.webxml.com.cn";
    private String LocationURL, WeatherURL;
    private Context mContext;
    private View SettingView;
    private EditText weatherContent;
    private Button view_black_list;
    private String TAG = "response";
    private PhoneLocationMaster PLMaster;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 0：返回号码归属地，格式是字符串数据{"province", "city", "card_type"}
                // 1：返回天气信息
                case 0:
                    String[] locations = (String[]) msg.obj;
                    weatherContent.setText(locations[0] + " " + locations[1] + " " + locations[2]);
//                    WeatherURL = HostURL + String.format("/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=", NetUtils.changeCharset(location, "UTF-8"));
//                    Log.d("response", WeatherURL);
                    //new Thread(getWeather).start();
                    break;
                case 1:
                    String weather = (String) msg.obj;
                    Toast.makeText(mContext, weather, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

//    private Runnable getLocation = new Runnable() {
//        @Override
//        public void run() {
//            String phoneNumber = "18819461579";
//            LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
//            String location = NetUtils.get(LocationURL, NetUtils.GET_LOCATION_INFO);
//            Message msg = new Message();
//            msg.obj = location;
//            msg.what = 0;
//            handler.sendMessage(msg);
//        }
//    };
//
//    private Runnable getWeather = new Runnable() {
//        @Override
//        public void run() {
//            String weather = NetUtils.get(WeatherURL, NetUtils.GET_WEATHER_INFO);
//            Message msg = new Message();
//            msg.obj = weather;
//            msg.what = 1;
//            handler.sendMessage(msg);
//        }
//    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return SettingView == null ? SettingView = inflater.inflate(R.layout.tab_settings_layout, container, false) : SettingView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        PLMaster = new PhoneLocationMaster(mContext);
    }

    @Override
    public void onStart() {
        super.onStart();
        final String phoneNumber = "18819461579";
        LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
        if (view_black_list == null) {
            view_black_list = (Button) SettingView.findViewById(R.id.view_black_list);
            view_black_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Message msg = new Message();
                    msg.what = 0;
                    String[] locations = PLMaster.get(phoneNumber);
                    if (locations != null) {
                        msg.obj = locations;
                        handler.sendMessage(msg);
                        return ;
                    }

                    HttpConnectionUtil.get(LocationURL, new HttpConnectionUtil.HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            PLMaster.add(phoneNumber, response.replaceAll("<[^>]+>", ""));
                            msg.obj = PLMaster.get(phoneNumber);
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onError(Exception e) {
                            msg.obj = "获取信息失败，请检查网络连接";
                            handler.sendMessage(msg);
                        }
                    });
                }
            });
        }

        if (weatherContent == null) {
            weatherContent = (EditText) SettingView.findViewById(R.id.infos);
        }
    }
}