package com.example.xieqingyang1.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xieqingyang1 on 2018/12/14.
 */

public class Weather {

    @SerializedName("basic")  //城市基本信息
    public Basic basic;

    @SerializedName("status")   //返回结果状态
    public String status;

    @SerializedName("daily_forecast")    //七天天气信息
    public List<Forecast> forecastList;
}
