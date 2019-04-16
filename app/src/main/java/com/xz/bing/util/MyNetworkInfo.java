package com.xz.bing.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyNetworkInfo {
    //这是一个系统服务类，专门用于管理网络连接的
//    private static ConnectivityManager connectivityManager
//            = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//    //通过改对象获取一个叫isAvailable()方法来判断网络是否连通，此处需要声明权限
//    private static NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

    /**
     * 当前网络是否通畅
     * true - 通畅
     * false - 不通畅
     */
    public static boolean isAvailable() {

//        这是一个系统服务类，专门用于管理网络连接的
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

//        通过改对象获取一个叫isAvailable()方法来判断网络是否连通，此处需要声明权限
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }


}
