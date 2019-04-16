package com.xz.bing.util;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xz.bing.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import uk.co.senab.photoview.PhotoView;

/**
 * 图片查看器
 * 负责整个软件的图片查看
 * 传入imagePath路径
 */
public class PicView extends SwipeBackActivity {

    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_view);
        //获取Id
        photoView = (PhotoView) findViewById(R.id.pic_view);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //优化内存
        photoView.setImageDrawable(null);
        photoView = null;
    }
}
