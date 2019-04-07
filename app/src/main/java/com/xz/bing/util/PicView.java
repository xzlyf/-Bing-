package com.xz.bing;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xz.bing.util.MyApplication;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片查看器
 * 负责整个软件的图片查看
 */
public class PicView extends AppCompatActivity {

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_view);
        //获取Id
        photoView = findViewById(R.id.pic_view);
        //获取传入数据
        String dir = getIntent().getStringExtra("imagePath");

        //使用glide加载壁纸
        Glide.with(PicView.this)
                .load(dir)
                .override(1920, 1080)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoView);


    }
}
