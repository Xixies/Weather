package com.example.xieqingyang1.weather;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xieqingyang1.weather.db.City;
import com.example.xieqingyang1.weather.gson.Weather;
import com.example.xieqingyang1.weather.gson.Weather_current;
import com.example.xieqingyang1.weather.util.HttpUtil;
import com.example.xieqingyang1.weather.util.Utility;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;
import org.litepal.util.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.framed.ErrorCode;
import okhttp3.internal.framed.FrameReader;
import okhttp3.internal.framed.Header;
import okhttp3.internal.framed.HeadersMode;
import okhttp3.internal.framed.Settings;
import okio.BufferedSource;
import okio.ByteString;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private List<WeatherItem> weatherlist = new ArrayList<WeatherItem>();
    private WeatherAdapter weatheradapter;
    private boolean ispad;   //当前设备是否为平板
    private Weather_current weather_current;  //实况天气
    private Weather weather;//实体类对象，包含7天天气信息
    private android.os.Handler handler;

    private  BasicActivity basic;//自定义的应用程序BasicActivity的实例
    //private List<Weather> weather_from_store;//来自WeatherStore数据库的数据列表

    private TextView day_Text;
    private TextView date_Text;
    private TextView tmp_max_Text;
    private TextView tmp_min_Text;
    private TextView cond_txt_d_Text;
    private TextView hum_Text;
    private TextView pres_Text;
    private TextView wind_spd_Text;
    private ImageView weather_Img;
    private TextView top_day_Text;
    private TextView top_date_Text;
    private TextView top_max_Text;
    private TextView top_min_Text;
    private ImageView top_weather_Img;
    private TextView top_weather_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        basic = (BasicActivity)getApplication();//获得自定义的应用程序BasicActivity

        day_Text = (TextView) findViewById(R.id.day_text);
        date_Text = (TextView) findViewById(R.id.date_text);
        tmp_max_Text = (TextView) findViewById(R.id.tmp_max_text);
        tmp_min_Text = (TextView) findViewById(R.id.tmp_min_text);
        cond_txt_d_Text = (TextView) findViewById(R.id.cond_txt_d_text);
        hum_Text = (TextView) findViewById(R.id.hum_text);
        pres_Text = (TextView) findViewById(R.id.pres_text);
        wind_spd_Text = (TextView) findViewById(R.id.wind_spd_text);
        weather_Img = (ImageView) findViewById(R.id.weather_img);
        top_day_Text = (TextView) findViewById(R.id.top_day_txt);
        top_date_Text = (TextView) findViewById(R.id.top_date_txt);
        top_max_Text = (TextView) findViewById(R.id.top_max_txt);
        top_min_Text = (TextView) findViewById(R.id.top_min_txt);
        top_weather_Img = (ImageView) findViewById(R.id.top_weather_img);
        top_weather_Text = (TextView) findViewById(R.id.top_weather_txt);

        initWeathers();//初始化listview
        weatheradapter = new WeatherAdapter(MainActivity.this, R.layout.weather_item, weatherlist);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(weatheradapter);
        ispad = isPad(MainActivity.this);

        //检查初始设置，是否开启服务发送定时通知
        if ("Enabled".equals(basic.getNotification())) {
            startService(new Intent(MainActivity.this, NotificationService.class));
        }else if ("Disabled".equals(basic.getNotification())) {
            stopService(new Intent(MainActivity.this, NotificationService.class));
        }else{
            Log.d("MainActivity", "onCreate: 通知开启标识错误");
        }


//        //获取缓存的设定城市
//        SharedPreferences pref = getSharedPreferences("last_selected", MODE_PRIVATE);
//        basic.setCurrentcity(pref.getString("currentcity","changsha"));
//        basic.setTemperature_units(pref.getString("temperature_units","Celsius"));
//        basic.setNotification(pref.getString("notification","Enabled"));

        //向服务器发送请求，并解析天气数据赋值给weather对象
        //请求7天天气，版本：6
        String address = "https://free-api.heweather.com/s6/weather/forecast?location="
                + basic.getCurrentcity()
                + "&key=b9d2981279b64fbb806b34e15f246fc7";
        Log.d("MainActivity", "onCreate: "+basic.getCurrentcity());
        queryFromServer(address);

        //请求实时天气，版本：7
        String address_current = "https://devapi.heweather.net/v7/weather/now?location="
                + basic.getCurrentcity()
                + "&key=b9d2981279b64fbb806b34e15f246fc7";
        queryFromServer_current(address_current);


//        boolean iffind = false;//是否在数据库中找到
//        //优先在数据库中查找
//        weather_from_store = DataSupport.findAll(Weather.class);
//        if (weather_from_store.size()>0){
//            for(Weather weather_s :weather_from_store){
//                if (weather_s.basic.clocation.equals(basic.getCurrentcity())) {//表项城市名= 当前城市参数
//                    weather = weather_s;
//                    iffind = true;
//                    break;
//                }
//            }
//        }
//        //若没有在本地数据库中查询到再向服务器发送请求
//        if(!iffind)
//            queryFromServer(address);





        //使用Handler来将初始化UI的操作从子线程切换到主线程中执行
        handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Object weatherobj = (Object)msg.obj;

                //将Weather实体类对象信息展示到ListView界面
                showListInfo((Weather) weatherobj);
                if (ispad){//更新平板右侧界面
                    showWeatherInfo_Pad((Weather) weatherobj,0);
                }else{//更新手机顶部界面
                    showWeatherInfo_Mobile((Weather) weatherobj);
                }
            }
        };



        //listview事件响应
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //手机
                    if(!ispad) {
                        String[] week={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
                        final Calendar c = Calendar.getInstance();
                        String d = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取星期
                        int day = Integer.valueOf(d);

                        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    if (id == 0) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(0)));
                        intent.putExtra("day_string","Today");
                        startActivity(intent);
                    }
                    if (id == 1) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(1)));
                        intent.putExtra("day_string","Tomorrow");
                        startActivity(intent);
                    }
                    if (id == 2) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(2)));
                        intent.putExtra("day_string",week[day%7]);
                        startActivity(intent);
                    }
                    if (id == 3) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(3)));
                        intent.putExtra("day_string",week[(day+1)%7]);
                        startActivity(intent);
                    }
                    if (id == 4) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(4)));
                        intent.putExtra("day_string",week[(day+2)%7]);
                        startActivity(intent);
                    }
                    if (id == 5) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(5)));
                        intent.putExtra("day_string",week[(day+3)%7]);
                        startActivity(intent);
                    }
                    if (id == 6) {
                        intent.putExtra("weather_string",new Gson().toJson(weather.forecastList.get(6)));
                        intent.putExtra("day_string",week[(day+4)%7]);
                        startActivity(intent);
                    }
                }
                //平板
                else{
                    showListInfo(weather);
                    if (id == 0) {
                        showWeatherInfo_Pad(weather, 0);
                    }
                    if (id == 1) {
                        showWeatherInfo_Pad(weather, 1);
                    }
                    if (id == 2) {
                        showWeatherInfo_Pad(weather, 2);
                    }
                    if (id == 3) {
                        showWeatherInfo_Pad(weather, 3);
                    }
                    if (id == 4) {
                        showWeatherInfo_Pad(weather, 4);
                    }
                    if (id == 5) {
                        showWeatherInfo_Pad(weather, 5);
                    }
                    if (id == 6) {
                        showWeatherInfo_Pad(weather, 6);
                    }
                }
            }
        });



    }

    //根据传入地址从服务器查询当前选定城市天气,并存入数据库(7天天气)
    private void queryFromServer(final String address) {

                    HttpUtil.sendOkHttpRequest(address, new Callback() {
                        //响应数据会回调到onResponse()中
                        @Override
                        public void onResponse(Call call,final Response response) throws IOException {
                                //在此对返回结果进行处理
                                new Thread(){
                                    public void run(){
                                        try{
                                            String responseText = response.body().string();
                                            Weather we = Utility.handleWeatherResponse(responseText);
                                            if (we != null) {
                                                Log.d("queryFromServer", "onResponse: " + responseText);
                                                weather = we;
                                            } else
                                            {
                                             Log.d("MainActivity", "queryFromServer：onResponse:加载数据失败, 获取实体类对象=null");
                                             }
                                        }catch (Exception e) {e.printStackTrace();}
                                        handler.sendMessage(handler.obtainMessage(0,weather));
                                    }
                                }.start();
                        }//end onresponse

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("MainActivity", "queryFromServer: onFailure: 申请数据失败");
                        }
                    });

    }

    //根据传入地址从服务器查询当前选定城市天气,并存入数据库(实时天气)
    private void queryFromServer_current(final String address_current) {

        HttpUtil.sendOkHttpRequest(address_current, new Callback() {
            //响应数据会回调到onResponse()中
            @Override
            public void onResponse(Call call,final Response response) throws IOException {
                //在此对返回结果进行处理
                new Thread(){
                    public void run(){
                        try{
                            String responseText = response.body().string();
                            Weather_current we_c = Utility.handleWeatherResponse_current(responseText);
                            if (we_c != null) {
                                Log.d("queryFromServer_current", "onResponse: " + responseText);
                                weather_current = we_c;
                            } else
                            {
                                Log.d("MainActivity", "queryFromServer_current：onResponse:加载数据失败, 获取实体类对象=null");
                            }
                        }catch (Exception e) {e.printStackTrace();}
                        handler.sendMessage(handler.obtainMessage(0,weather_current));
                    }
                }.start();
            }//end onresponse

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MainActivity", "queryFromServer: onFailure: 申请数据失败");
            }
        });

    }

    //将Weather实体类对象信息展示到Pad右界面
    //i:第1至7天
    public void showWeatherInfo_Pad(Weather weather,int i){
        //星期
        String[] week={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        final Calendar c = Calendar.getInstance();
        String d = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取星期
        int day = Integer.valueOf(d);
        if(i==0){
            day_Text.setText("Today");
        } else if(i==1){
            day_Text.setText("Tomorrow");
        }else{
            day_Text.setText(week[(day+i-2)%7]);
        }


        //天气图片和天气描述文字
        String condition = weather.forecastList.get(i).cond_txt_d;
        if("晴".equals(condition)) {
            cond_txt_d_Text.setText("Sunny");
            weather_Img.setImageResource(R.drawable.sunny_picture);
        }else if("阴".equals(condition)) {
            cond_txt_d_Text.setText("Overcast");
            weather_Img.setImageResource(R.drawable.overcast_picture);
        }else if("多云".equals(condition)) {
            cond_txt_d_Text.setText("Cloudy");
            weather_Img.setImageResource(R.drawable.cloudy_picture);
        }else if("小雨".equals(condition)) {
            cond_txt_d_Text.setText("Soft Rain");
            weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("中雨".equals(condition)) {
            cond_txt_d_Text.setText("Moderate Rain");
            weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("大雨".equals(condition)) {
            cond_txt_d_Text.setText("Heavy Rain");
            weather_Img.setImageResource(R.drawable.rainy_picture);
        }else {
            cond_txt_d_Text.setText(condition);
            weather_Img.setImageResource(R.drawable.overcast2_picture);
        }

        //气温
        if("Celsius".equals(basic.getTemperature_units())) {
            tmp_max_Text.setText(weather.forecastList.get(i).tmp_max+"°");
            tmp_min_Text.setText(weather.forecastList.get(i).tmp_min+"°");
        }else if("Fahrenheit".equals(basic.getTemperature_units())){
            tmp_max_Text.setText(String.format("%.0f",Integer.valueOf(weather.forecastList.get(i).tmp_max)*1.8+32) + "℉");
            tmp_min_Text.setText(String.format("%.0f",Integer.valueOf(weather.forecastList.get(i).tmp_min)*1.8+32) + "℉");
        }else{
            Log.d("MainActivity", "showWeatherInfo_Pad: 温度单位错误");
        }

        //其他数据
        date_Text.setText(weather.forecastList.get(i).date);
        hum_Text.setText(weather.forecastList.get(i).hum);
        pres_Text.setText(weather.forecastList.get(i).pres);
        wind_spd_Text.setText(weather.forecastList.get(i).wind_spd);
    }

    //将Weather实体类对象信息(今天)展示到手机顶部界面
    public void showWeatherInfo_Mobile(Weather weather){
        top_day_Text.setText("Today");
        top_date_Text.setText(weather.forecastList.get(0).date);

        //气温
        if("Celsius".equals(basic.getTemperature_units())) {
            top_max_Text.setText(weather.forecastList.get(0).tmp_max+"°");
            top_min_Text.setText(weather.forecastList.get(0).tmp_min+"°");
        }else if("Fahrenheit".equals(basic.getTemperature_units())){
            top_max_Text.setText(String.format("%.0f",Integer.valueOf(weather.forecastList.get(0).tmp_max)*1.8+32) + "℉");
            top_min_Text.setText(String.format("%.0f",Integer.valueOf(weather.forecastList.get(0).tmp_min)*1.8+32) + "℉");
        }else{
            Log.d("MainActivity", "showWeatherInfo_Mobile: 温度单位错误");
        }


        //天气图片和天气描述文字
        String condition = weather.forecastList.get(0).cond_txt_d;
        if("晴".equals(condition)) {
            top_weather_Text.setText("Sunny");
            top_weather_Img.setImageResource(R.drawable.sunny_picture);
        }else if("阴".equals(condition)) {
            top_weather_Text.setText("Overcast");
            top_weather_Img.setImageResource(R.drawable.overcast_picture);
        }else if("多云".equals(condition)) {
            top_weather_Text.setText("Cloudy");
            top_weather_Img.setImageResource(R.drawable.cloudy_picture);
        }else if("小雨".equals(condition)) {
            top_weather_Text.setText("Soft Rain");
            top_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("中雨".equals(condition)) {
            top_weather_Text.setText("Moderate Rain");
            top_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("大雨".equals(condition)) {
            top_weather_Text.setText("Heavy Rain");
            top_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else {
            top_weather_Text.setText(condition);
            top_weather_Img.setImageResource(R.drawable.overcast2_picture);
        }
    }

    //将Weather实体类对象信息展示到ListView界面
    public void showListInfo(Weather weather){

        String[] week={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        final Calendar c = Calendar.getInstance();
        String d = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取星期
        int day = Integer.valueOf(d);

        for(int i=0;i<7;i++) {
            //星期
            if(i==0){
                weatherlist.get(i).setDate("Today");
            } else if(i==1){
                weatherlist.get(i).setDate("Tomorrow");
            }else{
                weatherlist.get(i).setDate(week[(day+i-2)%7]);
            }

            //气温
            if("Celsius".equals(basic.getTemperature_units())) {
                weatherlist.get(i).setHigh_temperature(weather.forecastList.get(i).tmp_max + "°");
                weatherlist.get(i).setLow_temperature(weather.forecastList.get(i).tmp_min + "°");
            }else if("Fahrenheit".equals(basic.getTemperature_units())){
                weatherlist.get(i).setHigh_temperature(String.format("%.0f",Integer.valueOf(weather.forecastList.get(i).tmp_max)*1.8+32) + "℉");
                weatherlist.get(i).setLow_temperature(String.format("%.0f",Integer.valueOf(weather.forecastList.get(i).tmp_min)*1.8+32) + "℉");
            }else{
                Log.d("MainActivity", "showListInfo: 温度单位错误");
            }

            //天气图标和天气描述文字
            String condition = weather.forecastList.get(i).cond_txt_d;
            Log.d("MainActivity", "showListInfo: condition= "+condition);
            if("晴".equals(condition)) {
                weatherlist.get(i).setWeather("Sunny");
                weatherlist.get(i).setImageId(R.drawable.sunny_pic);
            }else if("阴".equals(condition)) {
                weatherlist.get(i).setWeather("Overcast");
                weatherlist.get(i).setImageId(R.drawable.overcast_pic);
            }else if("多云".equals(condition)) {
                weatherlist.get(i).setWeather("Cloudy");
                weatherlist.get(i).setImageId(R.drawable.cloudy_pic);
            }else if("小雨".equals(condition)) {
                weatherlist.get(i).setWeather("Soft Rain");
                weatherlist.get(i).setImageId(R.drawable.rainy_s_pic);
            }else if("中雨".equals(condition)) {
                weatherlist.get(i).setWeather("Moderate Rain");
                weatherlist.get(i).setImageId(R.drawable.rainy_m_pic);
            }else if("大雨".equals(condition)) {
                weatherlist.get(i).setWeather("Heavy Rain");
                weatherlist.get(i).setImageId(R.drawable.rainy_l_pic);
            }else {
                weatherlist.get(i).setWeather(condition);
                weatherlist.get(i).setImageId(R.drawable.overcast_pic);
            }
        }
        weatheradapter.notifyDataSetChanged();
    }

    //初始化listview
    public void initWeathers(){

        WeatherItem item1 = new WeatherItem("Today","sunny","23°","15°",R.drawable.sunny_pic);
        weatherlist.add(item1);
        WeatherItem item2 = new WeatherItem("Tomorrow","cloudy","23°","15°",R.drawable.cloudy_pic);
        weatherlist.add(item2);
        WeatherItem item3 = new WeatherItem("Wednesday","soft rain","23°","15°",R.drawable.rainy_s_pic);
        weatherlist.add(item3);
        WeatherItem item4 = new WeatherItem("Thursday","moderate rain","23°","15°",R.drawable.rainy_m_pic);
        weatherlist.add(item4);
        WeatherItem item5 = new WeatherItem("Friday","heavy rain","23°","15°",R.drawable.rainy_l_pic);
        weatherlist.add(item5);
        WeatherItem item6 = new WeatherItem("Saturday","overcast","23°","15°",R.drawable.overcast_pic);
        weatherlist.add(item6);
        WeatherItem item7 = new WeatherItem("Monday","overcast","23°","15°",R.drawable.overcast_pic);
        weatherlist.add(item7);

    }

     //判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     //平板返回 True，手机返回 False
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载Menu资源
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_location:
                Intent intent_map = new Intent();
                String baidu_url = "baidumap://map/marker?location="+weather.basic.lat+","
                        +weather.basic.lon+"&title="+weather.basic.clocation+"&traffic=on&src=andr.baidu.openAPIdemo";
                intent_map.setData(Uri.parse(baidu_url));
                startActivity(intent_map);
                break;
            case R.id.option_settings:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //更新listview界面
        showListInfo(weather);
        if (ispad){//更新平板右侧界面
            showWeatherInfo_Pad(weather,0);
        }else{//更新手机顶部界面
            showWeatherInfo_Mobile(weather);
        }
    }

    @Override
    protected void onStop(){
        //停止运行前存储当前选定城市信息/温度单位设置/通知开启设置
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("last_selected", 0).edit();
        editor.putString("currentcity",basic.getCurrentcity());
        editor.putString("temperature_units",basic.getTemperature_units());
        editor.putString("notification",basic.getNotification());
        editor.commit();
    }


//        @Override
//    protected void onDestroy() {
//        //销毁前存储当前选定城市信息/温度单位设置/通知开启设置
//        super.onDestroy();
//        SharedPreferences.Editor editor = getSharedPreferences("last_selected", 0).edit();
//        editor.putString("currentcity",basic.getCurrentcity());
//        editor.putString("temperature_units",basic.getTemperature_units());
//        editor.putString("notification",basic.getNotification());
//        editor.commit();
//    }

    //动态加载碎片
//    private void replaceFragment(Fragment fragment){
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction transcation = fragmentManager.beginTransaction();
//        transcation.replace(R.id.container_fragment,fragment);
//        Log.d("MainActivity", "replaceFragment: container to RightFragment");
//        transcation.commit();
//    }

}
