package com.xz.bing.net;

import android.util.Log;
import android.util.Xml;

import com.xz.bing.util.MyApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 负责软件更新
 * 1.检查更新
 * 2.根据更新等级来强制用户更新或可跳过不更新
 *  等级：
 *      1不强制更新
 *      2强制更新，不更则退出
 */
public class Update {
    public static void Resquest(UpdateCallbackListener callback){

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //okhttp操作
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://192.168.43.204/DayWallpaperUpdateTest/Update.xml")
                            .build();
                    Response response = client.newCall(request).execute();

                    //返回数据
                    String responseData = response.body().string();

                    //解析返回到的数据xml
                    parseXMLWithPull(responseData,callback);
                } catch (IOException e) {
//                    e.printStackTrace();
                    //网络连接出现异常
                    callback.error(e);
                }

            }
        }).start();
    }

    private static  void parseXMLWithPull(String responseDate, UpdateCallbackListener callback){
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseDate));
            int eventType = xmlPullParser.getEventType();
            //更新等级
            String level = null;
            //更新代号
            String name = null;
            //更新版本
            String code = null;
            //更新消息
            String msg = null;
            //下载地址
            String link = null;
            while (eventType !=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                    {
                        if ("level".equals(nodeName)){
                            level = xmlPullParser.nextText();
                        }else if("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        }else if("code".equals(nodeName)){
                            code = xmlPullParser.nextText();
                        }else if ("msg".equals(nodeName)){
                            msg = xmlPullParser.nextText();
                        }else if("link".equals(nodeName)){
                            link = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if ("DayWallpaper".equals(nodeName)){
//                            Log.d("Update", "parseXMLWithPull: "+level);
//                            Log.d("Update", "parseXMLWithPull: "+name);
//                            Log.d("Update", "parseXMLWithPull: "+code);
//                            Log.d("Update", "parseXMLWithPull: "+msg);

//                            callback.finish(level,name,code,msg);
                        }
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
