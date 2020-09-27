package com.example.xieqingyang1.weather;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private List<SettingItem> settinglist = new ArrayList<SettingItem>();
    private SettingAdapter settingadapter;
    private BasicActivity basic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        basic = (BasicActivity)getApplication();//获得自定义的应用程序BasicActivity

        initSettings();//初始化listview
        settingadapter = new SettingAdapter(SettingActivity.this,R.layout.setting_item,settinglist);
        ListView listView = (ListView) findViewById(R.id.setting_list_view);
        listView.setAdapter(settingadapter);

        settinglist.get(0).setSetting_value(basic.getCurrentcity());
        settinglist.get(1).setSetting_value(basic.getTemperature_units());
        settinglist.get(2).setSetting_value(basic.getNotification());
        settingadapter.notifyDataSetChanged();

        //listView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id==0){//设置城市
                    Location_Edit();
                }
                if (id==1){//设置温度单位
                    Temperature_Unit_Choice();
                }
                if (id==2){//开启通知
                    Notification_Choice();
                }

            }
        });





        setTitle("Settings");

        //添加返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    //初始化listview
    public void initSettings(){
        SettingItem item1 = new SettingItem("Location","Changsha");
        settinglist.add(item1);
        SettingItem item2 = new SettingItem("Temperature Units","Metric");
        settinglist.add(item2);
        SettingItem item3 = new SettingItem("Weather Notifications","Enabled");
        settinglist.add(item3);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮处理
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //温度单位单选dialog
    private void Temperature_Unit_Choice() {
        final String items[] = {"Celsius", "Fahrenheit"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,2);
        builder.setTitle("Temperature Units");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setSingleChoiceItems(items,0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basic.setTemperature_units(items[which]);
                        settinglist.get(1).setSetting_value(basic.getTemperature_units());
                        settingadapter.notifyDataSetChanged();
                        Toast.makeText(SettingActivity.this, items[which], Toast.LENGTH_SHORT).show();

                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(SettingActivity.this, "Setting Finished", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    //通知消息单选dialog
    private void Notification_Choice() {
        final String items[] = {"Enabled", "Disabled"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this,2);
        builder.setTitle("Notification");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setSingleChoiceItems(items,0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        basic.setNotification(items[which]);
                        settinglist.get(2).setSetting_value(basic.getNotification());
                        settingadapter.notifyDataSetChanged();
                        Toast.makeText(SettingActivity.this, "Notification "+items[which],
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //通过服务发送定时通知
                if ("Enabled".equals(basic.getNotification())) {
                    startService(new Intent(SettingActivity.this, NotificationService.class));
                }else if ("Disabled".equals(basic.getNotification())) {
                    stopService(new Intent(SettingActivity.this, NotificationService.class));
                }else{
                    Log.d("MainActivity", "onCreate: 通知开启标识错误");
                }
                Toast.makeText(SettingActivity.this, "Finished", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void Location_Edit() {
        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,3);
        builder.setTitle("Please input the name of city");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                basic.setCurrentcity(editText.getText().toString());
                settinglist.get(0).setSetting_value(basic.getCurrentcity());
                settingadapter.notifyDataSetChanged();
                Toast.makeText(SettingActivity.this, "City Setting Finished :"+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent_new = new Intent(SettingActivity.this,MainActivity.class);
                startActivity(intent_new);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

}
