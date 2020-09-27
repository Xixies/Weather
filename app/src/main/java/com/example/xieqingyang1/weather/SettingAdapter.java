package com.example.xieqingyang1.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xieqingyang1 on 2018/12/15.
 */

public class SettingAdapter extends ArrayAdapter {
    private final int resourceId;
    public SettingAdapter(Context context, int textViewResourceId, List<SettingItem> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingItem settingitem = (SettingItem) getItem(position); // 获取当前项的SettingItem实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);// 实例化一个对象

        TextView setting_Text = (TextView) view.findViewById(R.id.item_setting_txt);
        TextView value_Text = (TextView) view.findViewById(R.id.item_value_txt);

        setting_Text.setText(settingitem.getSetting_name());
        value_Text.setText(settingitem.getSetting_value());
        return view;
    }
}
