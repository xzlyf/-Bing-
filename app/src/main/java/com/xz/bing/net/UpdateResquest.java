package com.xz.bing.net;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 负责软件更新请求
 * 1.检查更新
 * 2.根据更新等级来强制用户更新或可跳过不更新
 *  等级：
 *      0不强制更新
 *      1强制更新，不更则退出
 */
public class UpdateResquest {

//    private static final String UPDATE_LINK = "http://xzlyf.club/DayWallpaperUpdate/update.xml";
    //检查更新服务器
    private static final String UPDATE_LINK = "https://xzlyf.github.io/DayWallpaperUpdate/update.xml";

    public static void Resquest(UpdateCallbackListener callback){

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    //okhttp操作
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(UPDATE_LINK)
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

                            //回调获取到的版本信息
                            callback.finish(level,name,code,msg,link);
                        }
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
            //回调错误信息
            callback.error(e);
        }

    }
}
