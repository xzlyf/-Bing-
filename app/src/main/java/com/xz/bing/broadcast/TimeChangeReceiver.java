package com.xz.bing.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xz.bing.MainActivity;
import com.xz.bing.R;
import com.xz.bing.net.CoreNet;
import com.xz.bing.net.HttpCallbackListener;
import com.xz.bing.util.DateFormat;
import com.xz.bing.util.MyNetworkInfo;

import java.io.File;

/**
 *
 * 接收广播实现自动下载更新壁纸
 */
public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "doDownload: 接收到广播");
        //下载最新壁纸操作

        doDownload(context);


    }

    /**
     * 下载壁纸
     * @param context
     */
    private void doDownload(Context context) {

        //1.检查网络
        if (MyNetworkInfo.isAvailable()){
            Log.d("Receiver", "doDownload: 有网络");

            //2.检查本地是否存在当天的壁纸
            //获取网络日期
//            String enddate  = DateFormat.netTime();
//            Log.d("Receiver", "doDownload: 网络日期"+enddate);
            //获取当天壁纸路径
            File file = new File(
                    context.getExternalFilesDir("image").getPath() + "/" + DateFormat.Today() + ".jpg");
            //如果本地壁纸不存在的话
            if (!file.exists()) {
                Log.d("Receiver", "doDownload: 本地无缓存");
                //3.开始下载壁纸
                //网请求回调数据
                CoreNet.sendResquest(new HttpCallbackListener() {
                    @Override
                    public void finish(String enddate, String copyright) {
                        Log.d("Receiver", "doDownload: 成功");

                        //显示下载完成通知
                        showFinishNotification(context);
                    }

                    @Override
                    public void error(Exception e) {
                        Log.d("Receiver", "doDownload: 失败");

                        showErrorNotification(context);
                    }
                });

            } else{
                Log.d("Receiver", "doDownload: 本地有缓存");

            }
        }else{
            Log.d("Receiver", "doDownload: 无网络");

        }
    }

    /**
     * 下载失败通知
     * @param context
     */
    private void showErrorNotification(Context context) {
        //弹出通知
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =new NotificationCompat.Builder(context,"default")
                .setContentTitle("每日壁纸")
                .setContentText("当天壁纸下载失败")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .build();

        manager.notify(1,notification);
    }

    /**
     * 下载成功通知
     * @param context
     */
    private void showFinishNotification(Context context){
        Intent mainActivity = new Intent(context,MainActivity.class);
        PendingIntent p1 =  PendingIntent.getActivity(context,0,mainActivity,0);
        //弹出通知
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =new NotificationCompat.Builder(context,"default")
                .setContentTitle("每日壁纸")
                .setContentText("已完成"+DateFormat.Today()+"壁纸缓存")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true)
                .setContentIntent(p1)
                .build();

        manager.notify(1,notification);
    }
}
