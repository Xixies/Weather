package com.example.xieqingyang1.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xieqingyang1.weather.gson.Forecast;
import com.google.gson.Gson;

import static android.R.attr.bitmap;


public class DetailActivity extends AppCompatActivity {

    Forecast forecast;
    String day;
    TextView mobile_day_Text;
    TextView mobile_date_Text;
    TextView mobile_max_Text;
    TextView mobile_min_Text;
    TextView mobile_hum_Text;
    TextView mobile_press_Text;
    TextView mobile_wind_Text;
    ImageView mobile_weather_Img;
    TextView mobile_weather_Text;
    private  BasicActivity basic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);

        basic = (BasicActivity)getApplication();//获得自定义的应用程序BasicActivity

        mobile_day_Text = (TextView)findViewById(R.id.mobile_day_txt);
        mobile_date_Text = (TextView)findViewById(R.id.mobile_date_txt);
        mobile_max_Text = (TextView)findViewById(R.id.mobile_max_txt);
        mobile_min_Text = (TextView)findViewById(R.id.mobile_min_txt);
        mobile_hum_Text = (TextView)findViewById(R.id.mobile_hum_txt);
        mobile_press_Text = (TextView)findViewById(R.id.mobile_press_txt);
        mobile_wind_Text = (TextView)findViewById(R.id.mobile_wind_txt);
        mobile_weather_Img = (ImageView)findViewById(R.id.mobile_weather_img);
        mobile_weather_Text = (TextView)findViewById(R.id.mobile_weather_txt);

        Intent intent = getIntent();
        String forecast_Json = intent.getStringExtra("weather_string");
        forecast = new Gson().fromJson(forecast_Json,Forecast.class);
        day = intent.getStringExtra("day_string");

        mobile_date_Text.setText(forecast.date);
        mobile_day_Text.setText(day);
        mobile_hum_Text.setText(forecast.hum);
        mobile_press_Text.setText(forecast.pres);
        mobile_wind_Text.setText(forecast.wind_spd);

        //气温
        if("Celsius".equals(basic.getTemperature_units())) {
            mobile_max_Text.setText(forecast.tmp_max+"°");
            mobile_min_Text.setText(forecast.tmp_min+"°");
        }else if("Fahrenheit".equals(basic.getTemperature_units())){
            mobile_max_Text.setText(String.format("%.0f",Integer.valueOf(forecast.tmp_max)*1.8+32) + "℉");
            mobile_min_Text.setText(String.format("%.0f",Integer.valueOf(forecast.tmp_min)*1.8+32)+ "℉");
        }else{
            Log.d("DetailActivity", "onCreate: 温度单位错误");
        }

        //天气图片和天气描述文字
        String condition = forecast.cond_txt_d;
        if("晴".equals(condition)) {
            mobile_weather_Text.setText("Sunny");
            mobile_weather_Img.setImageResource(R.drawable.sunny_picture);
        }else if("阴".equals(condition)) {
            mobile_weather_Text.setText("Overcast");
            mobile_weather_Img.setImageResource(R.drawable.overcast_picture);
        }else if("多云".equals(condition)) {
            mobile_weather_Text.setText("Cloudy");
            mobile_weather_Img.setImageResource(R.drawable.cloudy_picture);
        }else if("小雨".equals(condition)) {
            mobile_weather_Text.setText("Soft Rain");
            mobile_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("中雨".equals(condition)) {
            mobile_weather_Text.setText("Moderate Rain");
            mobile_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else if("大雨".equals(condition)) {
            mobile_weather_Text.setText("Heavy Rain");
            mobile_weather_Img.setImageResource(R.drawable.rainy_picture);
        }else {
            mobile_weather_Text.setText(condition);
            mobile_weather_Img.setImageResource(R.drawable.overcast2_picture);
        }


        setTitle("Details");

        //添加返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载Menu资源
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_share:


                Bitmap map = getActivityBitmap(DetailActivity.this);

                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),map, null,null));
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);//设置分享行为
                intent.setType("image/*");//设置分享内容的类型
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent = Intent.createChooser(intent, "share");
                startActivity(intent);

                break;
            case android.R.id.home:
                this.finish(); // back button
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public Bitmap getActivityBitmap(Activity activity){
        //获得当前屏幕Activity的View
        View view =  activity.getWindow().getDecorView();
        Bitmap  bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //view.buildDrawingCache(true);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }



}
