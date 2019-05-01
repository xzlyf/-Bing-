package com.xz.bing;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.bing.net.UpdateCallbackListener;
import com.xz.bing.net.UpdateDownloadTask;
import com.xz.bing.net.UpdateResquest;
import com.xz.bing.service.AutoDownload;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 设置界面
 * 继承与第三方activity 实现左滑返回 依赖：'me.imid.swipebacklayout.lib:library:1.1.0'
 * github:https://github.com/ikew0ng/SwipeBackLayout
 *
 */
public class Setting extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = "Setting";
    //本地版本
    int localVersionCode;
    //本地软件版本代号
    String localVersionName;

    //一堆开关属性
    private Switch to_album;
    private Switch auto_update;
    private Switch backgroundSwitch;
    private TextView local_path;
    private TextView local_version;
    private Button checkUpdate;

    /**
     * 关闭活动前的操作
     */

    @Override
    protected void onPause() {
        //保存按钮操作
        SharedPreferences.Editor editor = getSharedPreferences("UserHabit", 0).edit();
        //存入按钮最后的转态
        editor.putBoolean("to_album", to_album.isChecked());
        editor.putBoolean("auto_update", auto_update.isChecked());
        editor.putBoolean("background_switch", backgroundSwitch.isChecked());
        //写入
        if (editor.commit()) {
            super.onPause();
        } else {
            Toast.makeText(Setting.this, "配置未保存成功", Toast.LENGTH_SHORT).show();
            super.onPause();
        }
    }

    /**
     * 标题按钮监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    /**
     * 活动显示前的准备操作
     */
    @Override
    protected void onStart() {
        super.onStart();
        //读取用户设置的转态
        SharedPreferences preferences = getSharedPreferences("UserHabit", 0);
        //设置按钮状态
        to_album.setChecked(preferences.getBoolean("to_album", false));
        auto_update.setChecked(preferences.getBoolean("auto_update", false));
        backgroundSwitch.setChecked(preferences.getBoolean("background_switch",false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //进退场动画
        getWindow().setEnterTransition(new Fade().setDuration(500));
        getWindow().setExitTransition(new Fade().setDuration(500));
        //设置toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        setTitle("设置");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        //初始化开关对象并添加监听事件
        to_album = (Switch) findViewById(R.id.to_album);
        auto_update = (Switch) findViewById(R.id.auto_update);
        backgroundSwitch = (Switch) findViewById(R.id.background_switch);
        local_path = (TextView) findViewById(R.id.local_path);
        local_version = (TextView) findViewById(R.id.local_version);
        checkUpdate = (Button) findViewById(R.id.check_update);
        to_album.setOnClickListener(this);
        auto_update.setOnClickListener(this);
        checkUpdate.setOnClickListener(this);
        backgroundSwitch.setOnClickListener(this);

        //初始化本地版本信息
        localVersion();

        //获取传入的intent信息
        Intent intent = getIntent();
        local_path.setText(intent.getStringExtra("local_path"));
        intent = null;
        local_version.append(localVersionName);

        //此活动touch事件


    }
    /**
     * 检查更新
     */
    private void updateApk() {


        //请求检查更新
        UpdateResquest.Resquest(new UpdateCallbackListener() {
            @Override
            public void finish(String level, String name, String code, String msg, String link) {
                //类型格式化
                int cloudVersionCode = Integer.valueOf(code);
                int mlevel = Integer.valueOf(level);

                //比较云端版本和本地版本
                if (cloudVersionCode > localVersionCode) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //弹出是否更新对话框
                            Dialog updateDialog = new AlertDialog.Builder(Setting.this).create();
                            updateDialog.show();
                            updateDialog.setCancelable(false);//不能返回
                            Window window = updateDialog.getWindow();
                            window.setContentView(R.layout.update_dialog);
                            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            //dialog控件属性
                            TextView updateMsg = window.findViewById(R.id.update_msg);
                            updateMsg.setText("更新内容:" + msg);
                            TextView updateVersionName = window.findViewById(R.id.update_versionname);

                            Button cancelUpdate = window.findViewById(R.id.cancel_update);
                            //判断是否为强制更新  等于0不强制更新 等于1强制更新

                            if (mlevel == 0) {

                                cancelUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        updateDialog.dismiss();

                                    }
                                });
                                updateVersionName.setText(name);

                            } else if (mlevel == 1) {
                                updateVersionName.setText(name + "(本次为重要更新不可忽略)");
                                updateVersionName.setTextColor(Color.RED);
                                cancelUpdate.setText("退出");
                                cancelUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        updateDialog.dismiss();
                                        Setting.this.finish();
                                    }
                                });

                            }

                            Button doUpdate = window.findViewById(R.id.do_update);
                            doUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateDialog.dismiss();
                                    Dialog updateDownloadDialog = new AlertDialog.Builder(Setting.this).create();
                                    updateDownloadDialog.setCancelable(false);//不能返回退出
                                    updateDownloadDialog.show();
                                    Window window = updateDownloadDialog.getWindow();
                                    window.setContentView(R.layout.update_download_diaog);
                                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //进度条
                                    ProgressBar downloadBar = window.findViewById(R.id.update_download_bar);
                                    TextView tips = window.findViewById(R.id.downloadbar_tips);
                                    TextView miniSize = window.findViewById(R.id.update_cancel_download);
                                    //监听最小化按钮
                                    miniSize.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            updateDownloadDialog.dismiss();
                                        }
                                    });
                                    //执行下载动作
                                    new UpdateDownloadTask(link, downloadBar, tips, updateDownloadDialog).execute();


                                }
                            });


                        }
                    });


                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Setting.this,"最新版本啦！",Toast.LENGTH_LONG).show();

                        }
                    });
                }


            }

            @Override
            public void error(Exception e) {
                //失败几种可能
                //1.网络连接失败
                //2.检查更新服务器地址错误
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Setting.this,"服务器异常",Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }

    /**
     * 获取本地软件版本信息
     */
    private void localVersion() {
        //避免重复操作
        if (localVersionCode != 0 && localVersionName != null) {
            return;
        }
        //获取本地软件的版本信息
        PackageManager manager = Setting.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(Setting.this.getPackageName(), 0);
            localVersionCode = info.versionCode;
            localVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现控件点击事件方法
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.to_album:
                break;
            case R.id.auto_update:
                autoDownload(auto_update.isChecked());
                if (auto_update.isChecked()){
                    showSnackbar(view,"将在深夜网络通畅时自动缓存壁纸");
                }
                break;
            case R.id.background_switch:
                showSnackbar(view,"返回主页下拉刷新");
                break;
            case R.id.local_path:
                break;
            case R.id.check_update:
                updateApk();
                break;

        }

    }

    /**
     * 显示SnackBar
     * @param view
     * @param msg 内容
     */
    private void showSnackbar(View view,String msg){
        Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }

    /**
     * 打开服务或关闭服务
     * sw 开关状态
     */
    private void autoDownload(boolean sw) {

        if(sw){
            Intent start = new Intent(Setting.this,AutoDownload.class);
            startService(start);
        }else{
            Intent stop = new Intent(Setting.this,AutoDownload.class);
            stopService(stop);
        }

    }
}
