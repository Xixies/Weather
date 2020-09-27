package com.example.xieqingyang1.weather.util;

import android.text.TextUtils;

import com.example.xieqingyang1.weather.db.City;
import com.example.xieqingyang1.weather.gson.Weather;
import com.example.xieqingyang1.weather.gson.Weather_current;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xieqingyang1 on 2018/12/13.
 */

public class Utility {
    //解析服务器返回的Json数据，存入数据库
//    public static boolean handleCityResponse(String response){
//        if (!TextUtils.isEmpty(response)){
//            try{
//                JSONObject jsonobject = new JSONObject(response);
//                JSONArray jsonarray = jsonobject.getJSONArray("HeWeather6");
//                JSONObject cityobject = jsonarray.getJSONObject(0);
//                JSONObject basic = cityobject.getJSONObject("basic");
//                City city = new City();
//                city.setCityName(basic.getString("location"));
//                city.setCityCode(basic.getString("cid"));
//                city.save();
//                return true;
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

    //将返回的JSON数据解析成Weather实体类(七天天气)
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    // 将返回的JSON数据解析成Weather_current实体类(实时天气)
    public static Weather_current handleWeatherResponse_current(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject now = jsonObject.getJSONObject("now");
            String now_s = now.toString();

            return new Gson().fromJson(now_s,Weather_current.class);

        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}

