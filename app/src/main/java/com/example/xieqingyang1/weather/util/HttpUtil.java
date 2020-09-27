package com.example.xieqingyang1.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by xieqingyang1 on 2018/12/13.
 */

public class HttpUtil {
    //向服务器发送http请求
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
