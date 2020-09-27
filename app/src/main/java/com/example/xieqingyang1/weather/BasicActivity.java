package com.example.xieqingyang1.weather;

/**
 * Created by xieqingyang1 on 2018/12/16.
 */


import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.litepal.LitePalApplication;


public class BasicActivity extends LitePalApplication {

    private String currentcity;//当前设定城市
    private String temperature_units;//当前设定温度单位
    private String notification;//当前设定通知开启

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = getSharedPreferences("last_selected", MODE_PRIVATE);
        currentcity = pref.getString("currentcity","changsha");
        temperature_units = pref.getString("temperature_units","Celsius");
        notification = pref.getString("notification","Enabled");
    }


    public String getCurrentcity() {
        return currentcity;
    }

    public String getNotification() {
        return notification;
    }

    public String getTemperature_units() {
        return temperature_units;
    }

    public void setCurrentcity(String currentcity) {
        this.currentcity = currentcity;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public void setTemperature_units(String temperature_units) {
        this.temperature_units = temperature_units;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //销毁前存储当前选定城市信息/温度单位设置/通知开启设置
        SharedPreferences.Editor editor = getSharedPreferences("last_selected", 0).edit();
        editor.putString("currentcity",currentcity);
        editor.putString("temperature_units",temperature_units);
        editor.putString("notification",notification);
        editor.commit();
    }

}