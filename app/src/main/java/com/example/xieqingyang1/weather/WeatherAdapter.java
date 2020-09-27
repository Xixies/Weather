package com.example.xieqingyang1.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by xieqingyang1 on 2018/12/10.
 */

public class WeatherAdapter extends ArrayAdapter{
    private final int resourceId;
    public WeatherAdapter(Context context,int textViewResourceId, List<WeatherItem> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeatherItem weatheritem = (WeatherItem) getItem(position); // 获取当前项的WeatherItem实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);// 实例化一个对象
        ImageView weatherimage = (ImageView) view.findViewById(R.id.item_weather_img);//获取该布局内的图片视图
        TextView textview_date = (TextView) view.findViewById(R.id.item_date_txt);//获取该布局内的文本视图
        TextView textview_weather = (TextView) view.findViewById(R.id.item_weather_txt);
        TextView textview_high_emperature = (TextView) view.findViewById(R.id.item_max_txt);
        TextView textview_low_emperature = (TextView) view.findViewById(R.id.item_min_txt);

        weatherimage.setImageResource(weatheritem.getImageId());//为图片视图设置图片资源
        textview_date.setText(weatheritem.getDate());//日期
        textview_weather.setText(weatheritem.getWeather());//天气
        textview_high_emperature.setText(weatheritem.getHighTemperature());//最高温度
        textview_low_emperature.setText(weatheritem.getLowTemperature());//最低温度
        return view;
    }
}
