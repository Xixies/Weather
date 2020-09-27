package com.example.xieqingyang1.weather.db;


import org.litepal.crud.DataSupport;

/**
 * Created by xieqingyang1 on 2018/12/13.
 */

public class City extends DataSupport {
    private int id;
    private String cityName;
    private String cityCode;

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public int getId() {
        return id;
    }


    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setId(int id) {
        this.id = id;
    }

}
