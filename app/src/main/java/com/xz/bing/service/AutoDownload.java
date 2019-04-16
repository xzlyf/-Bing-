package com.xz.bing.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.xz.bing.broadcast.TimeChangeReceiver;
import com.xz.bing.util.MyApplication;

import java.util.Calendar;
import java.util.TimeZone;

public class AutoDownload extends Service {
    private IntentFilter intentFilter;
    //广播类
    private TimeChangeReceiver timeChangeReceiver;

    private Calendar mCalendar;

    public AutoDownload() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //创建广播
        intentFilter = new IntentFilter();
        //监听日期改变广播
//        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("com.xz.bing.action.AUTO_DOWNLOAD");//自定义广播值
        timeChangeReceiver = new TimeChangeReceiver();
        //注册广播
        registerReceiver(timeChangeReceiver, intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //定时任务 测试

        Intent sendBroadcast = new Intent("com.xz.bing.action.AUTO_DOWNLOAD");
//        sendBroadcast(sendBroadcast);

        PendingIntent pi = PendingIntent.getBroadcast(MyApplication.getContext(), 0, sendBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());


        //得到日历实例，主要是为了下面的获取时间
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //设置在几点提醒 设置的为13点
        mCalendar.set(Calendar.HOUR_OF_DAY, 02);
        //设置在几分提醒 设置的为25分
        mCalendar.set(Calendar.MINUTE, 00);
        //下面这两个看字面意思也知道
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        //上面设置的就是02点00分的时间点
        //获取上面设置的02点00分的毫秒值
        long selectTime = mCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //定时任务
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        //重复闹钟
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(timeChangeReceiver);
        //关闭闹钟

        Intent sendBroadcast = new Intent("com.xz.bing.action.AUTO_DOWNLOAD");
//        sendBroadcast(sendBroadcast);

        PendingIntent pi = PendingIntent.getBroadcast(MyApplication.getContext(), 0, sendBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
