package com.xz.bing;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Start extends AppCompatActivity {
    private TextView skipText;
    private final int TIMECODE = 1;
    //时长 -秒 多久自动跳过
    private int time = 4;
    private boolean isRun = true;
    private final static String TAG = "Start";


    //更新ui
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMECODE:
                    Log.d(TAG, "onCreate: c");

                    time--;
                    skipText.setText(time + "跳过");

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.d(TAG, "onCreate: a");
        skipText = findViewById(R.id.skip_text);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRun = true) {
                    isRun = false;
                }
            }
        });

        //android 6.0以上获取运行时权限
        getpermission();


    }


    /**
     * 倒计时多少秒进入主页
     */
    private void countTime() {
        Log.d(TAG, "onCreate: a+");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    Log.d(TAG, "onCreate: b");

                    Message message = new Message();
                    message.what = TIMECODE;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (time <= 1)
                        break;

                }
                Log.d("Start", "run: 线程已销毁");

                goHome();

            }
        }).start();


    }

    /**
     * 获取运行时权限
     */
    private void getpermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //申请读写权限
            if (ContextCompat.checkSelfPermission(Start.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //显示对话框
                showGetRuntimePermission();

            } else {
                //倒计时进入主页
                countTime();
            }
        }else{
            //倒计时进入主页
            countTime();
        }
    }

    /**
     * 显示引导用户获取权限的对话框
     */
    private void showGetRuntimePermission() {
        //对话框指定样式
        Dialog guidePermission = new AlertDialog.Builder(
                Start.this, R.style.roundDialog).create();
        guidePermission.setCancelable(false);
        guidePermission.show();
        Window window = guidePermission.getWindow();
        window.setContentView(R.layout.guide_permisson_dialog);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button getPermissionButton = window.findViewById(R.id.get_permission);
        getPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //没有权限则申请权限
                ActivityCompat.requestPermissions(Start.this
                        , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                guidePermission.dismiss();
            }
        });
    }

    /**
     * 权限回调事件
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //倒计时进入主页
                    countTime();
                } else {
                    Toast.makeText(Start.this, "权限不足，无法运行", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * 进入主页
     */
    private void goHome() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: ui");
                    //有转场动画
                    startActivity(
                            new Intent(Start.this, MainActivity.class)
                            , ActivityOptions.makeSceneTransitionAnimation(Start.this).toBundle());
                    finish();
                } catch (Exception e) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Start.this);
                    dialog.setTitle("程序致命错误");
                    dialog.setMessage("程序内部已损坏，请前往官网:http://www.xzlyf.com 下载完整安装包");
                    dialog.show();
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        skipText = null;

    }


}
