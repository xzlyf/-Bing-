package com.xz.bing.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xz.bing.util.MyApplication;
import com.xz.bing.util.DateFormat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 处理返回的数据并解析xml
 */
public class CoreNet {

    //全局Context
    private static Context context;

    /**
     * 连接bing的api
     * @param callbackListener
     */
    public static void sendResquest(HttpCallbackListener callbackListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //初始化全局Context
                context = MyApplication.getContext();

                try {
                    //okhttp操作
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://cn.bing.com/HPImageArchive.aspx?format=json&idx=0&n=1&mkt=zh-CN")
                            .build();
                    Response response = client.newCall(request).execute();

                    //返回数据
                    String responseData = response.body().string();

                    //解析返回到的数据xml
                    paresXMLWithPull( responseData, callbackListener);


                } catch (IOException e) {
//                    e.printStackTrace();
                    //网络连接出现异常
                    callbackListener.error(e);
                }
            }
        }).start();

    }

    private static void paresXMLWithPull( String responseData, HttpCallbackListener callbackListener) {
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseData));
            int eventType = xmlPullParser.getEventType();

            //链接
            String url = "";
            //日期
            String enddate = "";
            //版权
            String copyright = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("url".equals(nodeName)) {
                            url = xmlPullParser.nextText();
                        } else if ("enddate".equals(nodeName)) {
                            enddate = xmlPullParser.nextText();
                        } else if ("copyright".equals(nodeName)) {
                            copyright = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("image".equals(nodeName)) {
//                            Log.d("Core", "paresXMLWithPull: "+url);
//                            Log.d("Core", "paresXMLWithPull: "+enddate);
//                            Log.d("Core", "paresXMLWithPull: "+copyright);


                            //检测本地是否存相同文件
                            if (!checkImages()) {
                                Log.d("core", "paresXMLWithPull: 图片未下载将执行下载");

                                //没有本地文件就开始下载文件
                                downloadImageTolocal(enddate, url,copyright);

                            }else{
                                Log.d("core", "paresXMLWithPull: 图片已下载");


                            }


                            //回调数据
                            callbackListener.finish(enddate, copyright);

                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载图片的方法保存到壁纸
     * 路径：storage/emulated/0/Android/data/com.xz.bing/files/image
     *
     * @param enddate 返回true表示下载完成，反之
     */
    private static boolean downloadImageTolocal(String enddate, String url,String copyright) {
        //用过全局Context获取到缓存目录并以当前日期设置为文件名
        String dir = context.getExternalFilesDir("image").getPath() + "/" + enddate + ".jpg";
        //下载图片-流操作
        InputStream is = null;
        RandomAccessFile save = null;
        Response response = null;

        OkHttpClient client = new OkHttpClient();
        Request rquest = new Request.Builder().url("https://cn.bing.com" + url).build();
        try {
            response = client.newCall(rquest).execute();
            if (response != null) {
                is = response.body().byteStream();
                save = new RandomAccessFile(dir, "rw");
                byte[] b = new byte[1024];
                int len;
                int total = 0;
                while ((len = is.read(b)) != -1) {
                    total += len;
                    save.write(b, 0, len);
                }
            }

        } catch (IOException e) {
            //报错则返回false表明下载异常
            return false;
        } finally {
            //关闭流
            try {
                if (save != null) {
                    save.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            response.body().close();
        }

        //将图片信息保存到本地 每张照片保存一个新的xml文件 已日期作为文件名
        //存入三个参数 1.日期 2.版权来源 3.图片本地位置
        SharedPreferences.Editor editor =  context.getSharedPreferences(enddate,0).edit();
        editor.putString("enddate",enddate);
        editor.putString("copyright",copyright);
        editor.putString("uri",dir);
        editor.apply();

        return true;

    }

    /**
     * 检测照片是否在本地存在
     *
     * @return true 存在  false 不存在
     */
    private static boolean checkImages() {
        //下载目录
        String dir = context.getExternalFilesDir("image").getPath();
        //获取当前日期(20190101)格式的图片地址
        File file = new File(dir + "/" + DateFormat.Today() + ".jpg");

        //检测图片是否存在，存在返回true,不存在返回false
        return file.exists();
    }


}
