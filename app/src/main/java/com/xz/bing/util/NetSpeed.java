package com.xz.bing.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

/**
 * 返回实时网络
 */
public class NetSpeed {
    //实时网速  根据uid来查询流量使用情况
    private long lastTotalRxBytes = 0;
    private long nowTotalRxBytes;
    private long speed;
    //获取该软件uid
    private PackageManager pm = null;
    private ApplicationInfo ai = null;
    private int uid;

    public NetSpeed(){
        try {
            pm = MyApplication.getContext().getPackageManager();
            ai = pm.getApplicationInfo("com.xz.bing", PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (ai != null) {

            uid = ai.uid;
        }
    }

    /**
     * 获取实时网速
     * @return
     */
    public long getSpeed(){
        //当前网速
        nowTotalRxBytes = getTotalRxBytes(uid);
        speed = (nowTotalRxBytes - lastTotalRxBytes);//毫秒转换
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }
    /**
     * 根据uid获取当前流量
     *
     * @param uid
     * @return
     */
    public long getTotalRxBytes(int uid) {
        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }
}
