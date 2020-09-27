package com.example.xieqingyang1.weather;

/**
 * Created by xieqingyang1 on 2018/12/15.
 */

public class SettingItem {
    private String setting_name;
    private  String setting_value;

    public SettingItem(String setting_name,String setting_value){
        this.setting_name = setting_name;
        this.setting_value = setting_value;
    }

    public String getSetting_name() {
        return setting_name;
    }

    public void setSetting_name(String setting_name) {
        this.setting_name = setting_name;
    }

    public String getSetting_value() {
        return setting_value;
    }

    public void setSetting_value(String setting_value) {
        this.setting_value = setting_value;
    }
}
