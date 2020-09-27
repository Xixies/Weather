package com.example.xieqingyang1.weather;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.example.xieqingyang1.weather.gson.Weather;

public class NotificationService extends Service {

    private Weather weather;//实体类对象，包含7天天气信息

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendnotification();
        //定时运行设置
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int ten_second = 1000*10;
        long triggerAtTime = SystemClock.elapsedRealtime()+ten_second;
        Intent i = new Intent(this,NotificationService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void sendnotification(){
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Weather Forecast")
                .setContentText("Today: Sunny   Maximum temperature 13°    Minimum temperature 2°")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.overcast_pic)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .build();
        manager.notify(1,notification);

    }
}

