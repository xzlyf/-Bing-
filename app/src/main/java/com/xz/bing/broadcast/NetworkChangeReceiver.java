package com.xz.bing.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xz.bing.util.MyApplication;
import com.xz.bing.util.MyNetworkInfo;

/**
 * 接收广播
 * 当发生网络改变时进行提醒
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        /*
        //这是一个系统服务类，专门用于管理网络连接的
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //通过改对象获取一个叫isAvailable()方法来判断网络是否连通，此处需要声明权限
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo!=null &&networkInfo.isAvailable()){
                //通畅也不同提醒
//            Toast.makeText(MyApplication.getContext(), "当前网络通畅", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MyApplication.getContext(), "当前网络异常", Toast.LENGTH_SHORT).show();
        }
        */

        if (MyNetworkInfo.isAvailable()){
//            Toast.makeText(MyApplication.getContext(), "当前网络通畅", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MyApplication.getContext(), "当前网络异常", Toast.LENGTH_SHORT).show();
        }
    }
}
