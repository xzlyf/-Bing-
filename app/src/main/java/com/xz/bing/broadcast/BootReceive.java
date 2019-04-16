package com.xz.bing.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xz.bing.service.AutoDownload;
import com.xz.bing.util.ServiceInfo;

/**
 * 开机启动监听
 * 实现开机自启并检测[自动下载壁纸]服务开启没有，没有则开启服务
 */
public class BootReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //判断服务开启没有
        if (ServiceInfo.isServiceRunning("AutoDownload",context)){
            Log.d("Boot", "onReceive: 活动启动了");
        }else{
            Log.d("Boot", "onReceive: 活动未启动");
            Intent start = new Intent(context,AutoDownload.class);
            context.startService(start);
        }
    }

}
