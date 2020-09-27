package com.example.xieqingyang1.weather;

/**
 * Created by xieqingyang1 on 2018/12/10.
 */

public class WeatherItem {
    private String date;
    private String weather;
    private String high_temperature;
    private String low_temperature;
    private int imageId;

    public WeatherItem(String date,String weather,String high_temperature,String low_temperature,int imageId){
        this.date = date;
        this.weather = weather;
        this.imageId = imageId;
        this.high_temperature = high_temperature;
        this.low_temperature = low_temperature;
    }

    public String getDate(){return date;}
    public String getWeather(){return weather;}
    public String getHighTemperature(){return high_temperature;}
    public String getLowTemperature(){return low_temperature;}
    public int getImageId(){return imageId;}

    public void setDate(String date) {this.date = date;}
    public void setHigh_temperature(String high_temperature) {this.high_temperature = high_temperature;}
    public void setImageId(int imageId) {this.imageId = imageId;}
    public void setLow_temperature(String low_temperature) {this.low_temperature = low_temperature;}
    public void setWeather(String weather) {this.weather = weather;}
}
