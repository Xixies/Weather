package com.example.xieqingyang1.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xieqingyang1 on 2020/8/12.
 */

public class Weather_current {
    @SerializedName("temp")  //实况温度
    public String temp;

    @SerializedName("text")  //天气名称
    public String text;
}
