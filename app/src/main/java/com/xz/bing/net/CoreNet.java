package com.xz.bing.net;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Core {

    public void sendResquest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //okhttp操作
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("https://cn.bing.com/HPImageArchive.aspx?format=jsonl&idx=0&n=1&mkt=zh-CN")
                            .build();
                    Response response = client.newCall(request).execute();

                    //返回数据
                    String responseData = response.body().string();

                    //解析返回到的数据xml
                    paresXMLWithPull(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void paresXMLWithPull(String responseData) {
        try{

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(responseData));
            int eventType = xmlPullParser.getEventType();

            String url = "";
            String enddate="";
            String headline="";

            while(eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:{
                        if ("url".equals(nodeName)){
                            url = xmlPullParser.nextText();
                        }else if("enddate".equals(nodeName)){
                            enddate = xmlPullParser.nextText();
                        }else if ("headline".equals(nodeName)){
                            headline = xmlPullParser.nextText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if ("image".equals(nodeName)){
                            Log.d("Core", "paresXMLWithPull: "+url);
                            Log.d("Core", "paresXMLWithPull: "+enddate);
                            Log.d("Core", "paresXMLWithPull: "+headline);
                        }
                        break;
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
