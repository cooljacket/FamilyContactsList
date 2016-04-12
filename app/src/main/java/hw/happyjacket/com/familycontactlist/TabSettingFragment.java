package hw.happyjacket.com.familycontactlist;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
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
    public EditText weatherContent;
    private String TAG = "response";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    String location = (String) msg.obj;
                    Toast.makeText(mContext, location, Toast.LENGTH_LONG).show();
                    WeatherURL = HostURL + String.format("/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=", NetUtils.changeCharset(location, "UTF-8"));
                    Log.d("response", WeatherURL);
                    new Thread(getWeather).start();
                    break;
                case 1:
                    String weather = (String) msg.obj;
                    Toast.makeText(mContext, weather, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    private Runnable getLocation = new Runnable() {
        @Override
        public void run() {
            String phoneNumber = "18819461579";
            LocationURL = String.format("%s/WebServices/MobileCodeWS.asmx/getMobileCodeInfo?mobileCode=%s&userID=", HostURL, phoneNumber);
            String location = NetUtils.get(LocationURL, NetUtils.GET_LOCATION_INFO);
            Message msg = new Message();
            msg.obj = location;
            msg.what = 0;
            handler.sendMessage(msg);
        }
    };

    private Runnable getWeather = new Runnable() {
        @Override
        public void run() {
            String weather = NetUtils.get(WeatherURL, NetUtils.GET_WEATHER_INFO);
            Message msg = new Message();
            msg.obj = weather;
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_settings_layout, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button view_black_list = (Button) getView().findViewById(R.id.view_black_list);
        view_black_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(getLocation).start();

//                new Thread(){
//                    public void run() {
//                        String location = NetUtils.get(LocationURL, NetUtils.GET_LOCATION_INFO);
//                        Message msg = new Message();
//                        msg.obj = location;
//                        msg.what = 0;
//                        handler.sendMessage(msg);
//                    }
//                }.start();
            }
        });
    }
}