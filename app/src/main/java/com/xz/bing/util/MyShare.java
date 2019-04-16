package com.xz.bing.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xz.bing.R;

import java.io.File;

/**
 * 负责整个应用的分享操作
 */
public class MyShare {
    /**
     *
     * @param path 图片路径
     * @param context 活动
     * @param title 活动标题
     */
    public static void showSelect(String path, Context context,String title){
        File file = new File(path);
        // intent.setType("text/plain"); //纯文本
        /*
         * 图片分享 it.setType("image/png");
         * 添加图片 File f = new File(图片路径);
         * Uri uri = Uri.fromFile(f);
         * intent.putExtra(Intent.EXTRA_STREAM,uri);
         */
        //执行分享操作
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        Uri uri = Uri.fromFile(file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.share_title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, R.string.share_msg);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent, title));
    }
}
