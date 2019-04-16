package com.xz.bing.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化日期
 */
public class DateFormat {
    //日期类
//    private static Date date = new Date();
    /**
     * 格式化输出今天的日期
     * 20190101
     *
     * @return
     */
    public static String Today() {
        //获取当前日期(20190101)格式
        Date date = new Date();
        SimpleDateFormat date1 = new SimpleDateFormat("yyyyMMdd");
        return date1.format(date);
    }

    /**
     * 获取网络日期
     * @return
     */
    public static String netTime() {

        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL("http://bjtime.cn/");
            connection = url.openConnection();
            long ld = connection.getDate();
            Date date = new Date(ld);
            SimpleDateFormat date1 = new SimpleDateFormat("yyyyMMdd");
            return date1.format(date);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
