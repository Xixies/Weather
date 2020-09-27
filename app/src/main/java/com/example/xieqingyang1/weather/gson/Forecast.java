package com.example.xieqingyang1.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xieqingyang1 on 2018/12/14.
 */

public class Forecast {
    @SerializedName("date")
    public String date;

    @SerializedName("cond_txt_d")
    public String cond_txt_d;

    @SerializedName("tmp_max")
    public String tmp_max;

    @SerializedName("tmp_min")
    public String tmp_min;

    @SerializedName("hum")
    public String hum;

    @SerializedName("pres")
    public String pres;

    @SerializedName("wind_spd")
    public String wind_spd;



}
