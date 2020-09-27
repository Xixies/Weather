package com.example.xieqingyang1.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xieqingyang1 on 2018/12/14.
 */

public class Basic {
    @SerializedName("cid")
    public String cid;

    @SerializedName("location")
    public String clocation;//地区/城市名称


    @SerializedName("lon")
    public String lon;//经度

    @SerializedName("lat")
    public String lat;//纬度
}
