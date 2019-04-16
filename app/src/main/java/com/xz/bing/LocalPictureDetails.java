package com.xz.bing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xz.bing.util.MyShare;
import com.xz.bing.util.PicView;

import java.io.File;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 本地照片详情页
 */
public class LocalPictureDetails extends SwipeBackActivity {

    private ImageView detailImage;
    //图片信息
    private String enddate, uri, copyright;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailImage = null;
        enddate =null;
        uri=null;
        copyright=null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_picture_details);
        //获取对象传入的图片信息
        Intent intent = getIntent();
        enddate = intent.getStringExtra("enddate");
        uri = intent.getStringExtra("uri");
        copyright = intent.getStringExtra("copyright");

        //显示本地照片
        initImage();
        //底部按钮监听事件
        initButton();

    }
    /**
     * 底部按钮监听事件
     */
    private void initButton() {
        Button delete = (Button) findViewById(R.id.detail_delete);
        Button share = (Button) findViewById(R.id.detail_share);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //构建一个dialog

                AlertDialog.Builder builder = new AlertDialog.Builder(LocalPictureDetails.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除吗！\n以往的不可撤销哦！");
                //点击对话框以外的区域是否让对话框消失
                builder.setCancelable(true);
                //设置正面按钮
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除图片文件
                        File imageFIle = new File(uri);
                        if (imageFIle.isFile()) {
                            imageFIle.delete();
                        }
                        //删除xml文件
                        File dataFile = new File("/data/data/" + getPackageName() + "/shared_prefs", enddate+".xml");
                        if (dataFile.exists()){
                            dataFile.delete();
                            Toast.makeText(LocalPictureDetails.this, "删除成功", Toast.LENGTH_LONG).show();
                        }
                        //关闭活动
                        finish();
                    }
                });
                //设置反面按钮
                builder.setNegativeButton("不删了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();



            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //分享操作
                MyShare.showSelect(uri,LocalPictureDetails.this,getTitle().toString());

            }
        });
    }

    /**
     * 显示本地照片
     */
    private void initImage() {


        detailImage = (ImageView) findViewById(R.id.detail_pic_view);
        TextView detailText = (TextView) findViewById(R.id.details_copyright);
        //把日期设为标题
        setTitle(enddate);
        //加载图片
        Glide.with(this).load(uri).into(detailImage);
        //显示图片信息
        detailText.setText("版权来源:" + copyright);

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocalPictureDetails.this, PicView.class);
                //intent传输图片路径
                intent.putExtra("imagePath", uri);
                startActivity(intent);
            }
        });
    }

}
