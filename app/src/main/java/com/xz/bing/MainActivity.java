package com.xz.bing;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xz.bing.NetWork.Login;
import com.xz.bing.broadcast.NetworkChangeReceiver;
import com.xz.bing.net.CoreNet;
import com.xz.bing.net.HttpCallbackListener;
import com.xz.bing.net.UpdateCallbackListener;
import com.xz.bing.net.UpdateDownloadTask;
import com.xz.bing.net.UpdateResquest;
import com.xz.bing.service.AutoDownload;
import com.xz.bing.util.DateFormat;
import com.xz.bing.util.ImageBitmap;
import com.xz.bing.util.MyNetworkInfo;
import com.xz.bing.util.MyShare;
import com.xz.bing.util.NetSpeed;
import com.xz.bing.util.PicView;
import com.xz.bing.util.ServiceInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 程序入口
 */
public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    //滑动菜单对象
    private DrawerLayout mDrawerLayout;

    //处理广播的对象
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    //图像对象
    private ImageView mainImage;
    //文本对象
    private TextView endDate;
    private TextView copyRight;

    //下拉菜单对象
    private  SwipeRefreshLayout swipeRefreshLayout;

    //主壁纸加载是否完成-true完成 false未完成
    private boolean isImageLogin = false;
    //主来源是本地还是网络-true本地 false网络
    private boolean islocalImage = true;
    //退出标识
    private static boolean mBackKey = false;

    //上一次的设置状态
    private boolean to_album = false;
    private boolean auto_update = false;

    //本地版本
    private int localVersionCode;
    //本地软件版本代号
    private  String localVersionName;

    //实时网速what字段
    private static final int NET_SPEED = 0;
    private TextView netSpeedText;
    //Handler刷新ui
    Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NET_SPEED:
                    netSpeedText.setText(msg.getData().get("byte_text")+"kb/s");
                    break;

            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播
        unregisterReceiver(networkChangeReceiver);

    }

    /**
     * 设置状态对应逻辑
     */
    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //延迟两秒是为了让xml可以存储成功,否则读取的还是存储之前的配置
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //读取用户设置状态
                SharedPreferences preferences = getSharedPreferences("UserHabit", 0);

                /*
                解决重复操作
                --如果读取到的状态不等于上一次的状态就执行此域并替换此次状态为上一次状态
                 */
                //在相册显示缓存壁纸
                if (preferences.getBoolean("to_album", false) != to_album) {
                    //更新状态
                    to_album = preferences.getBoolean("to_album", false);
                    //检查状态标识
                    if (to_album){
                        Log.d(TAG, "run: "+"file://"+getExternalFilesDir("image").getPath());
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+getExternalFilesDir("image").getPath())));
                    }
                }
                //每天0点自动更新缓存壁纸
                if (preferences.getBoolean("auto_update", false) != auto_update) {
                    //更新状态
                    auto_update = preferences.getBoolean("auto_update", false);
                    //如果状态标识为开启
                    if (auto_update){
                        //检查服务开启没有，没有则开启服务
                        if (ServiceInfo.isServiceRunning("AutoDownload",MainActivity.this)){
                        }else{
                            Intent start = new Intent(MainActivity.this,AutoDownload.class);
                            startService(start);
                        }
                    }else{
                        //如果检测到状态标识为关闭而服务却打开了那就关闭服务
                        if (ServiceInfo.isServiceRunning("AutoDownload",MainActivity.this)){
                            Intent stop = new Intent(MainActivity.this,AutoDownload.class);
                            stopService(stop);
                        }else{

                        }
                    }

                }


            }
        }).start();


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //进场动画
//        getWindow().setEnterTransition(new Fade().setDuration(1000));
//        getWindow().setEnterTransition(new Explode().setDuration(1000));
        getWindow().setEnterTransition(new Slide().setDuration(500));

        //设置toolbar为默认标题栏
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置此活动标题--在java代码中应用xml资源
        setTitle(R.string.home_title);

        //初始化滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //让标题栏显示滑动菜单的按钮显出来
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);//设置图标
        }
        //初始化对象
        mainImage = findViewById(R.id.main_image);
        endDate = findViewById(R.id.enddate);
        copyRight = findViewById(R.id.copyright);

        //检查是否第一次运行
        isFirstRun();
        //初始化服务
        init_services();
        //初始化监听回调
        init_listener();
        //初始化主视图
        init_main_view();
        //检查更新
        updateApk();


    }

    /**
     * 检查更新
     */
    private void updateApk() {

        //初始化本地版本信息
        localVersion();

        //检查本地是否存在软件的安装包，有就删除
        deleteApk();

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
                            Dialog updateDialog = new AlertDialog.Builder(MainActivity.this).create();
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
                                        MainActivity.this.finish();
                                    }
                                });

                            }

                            Button doUpdate = window.findViewById(R.id.do_update);
                            doUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateDialog.dismiss();
                                    Dialog updateDownloadDialog = new AlertDialog.Builder(MainActivity.this).create();
                                    updateDownloadDialog.setCancelable(false);//不能返回退出
                                    updateDownloadDialog.show();
                                    Window window = updateDownloadDialog.getWindow();
                                    window.setContentView(R.layout.update_download_diaog);
                                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    //进度条
                                    ProgressBar downloadBar = window.findViewById(R.id.update_download_bar);
                                    TextView tips = window.findViewById(R.id.downloadbar_tips);
                                    TextView miniSize = window.findViewById(R.id.update_cancel_download);
                                    netSpeedText = window.findViewById(R.id.net_speed);
                                    //监听最小化按钮
                                    miniSize.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            updateDownloadDialog.dismiss();
                                        }
                                    });
                                    //执行下载动作
                                    new UpdateDownloadTask(link, downloadBar, tips, updateDownloadDialog).execute();
                                    //实时网速
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NetSpeed s = new NetSpeed();

                                            //通过Bundle来传递消息
                                            Bundle bundle = new Bundle();
                                            //通过判断tips文本框是否隐藏了来判断是否下载完成跳出循环.......
                                            while(!(tips.getVisibility()==View.INVISIBLE)) {
                                                //使用handler更新ui
                                                Message message = new Message();
                                                message.what = NET_SPEED;
                                                bundle.clear();
                                                bundle.putString("byte_text", String.valueOf(s.getSpeed()));
                                                message.setData(bundle);
                                                mhandler.sendMessage(message);
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    }).start();


                                }
                            });


                        }
                    });

                }


            }

            @Override
            public void error(Exception e) {
                //失败几种可能
                //1.网络连接失败
                //2.检查更新服务器地址错误
            }
        });

    }

    /**
     * 删除本地软件安装包
     */
    private void deleteApk() {
        //获取安装包位置
        File file = new File(getExternalFilesDir("temp")+"/apk.apk");
        if(file.exists()){
            file.delete();
        }
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
        PackageManager manager = MainActivity.this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(MainActivity.this.getPackageName(), 0);
            localVersionCode = info.versionCode;
            localVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测是否第一次运行
     */
    private void isFirstRun() {


        SharedPreferences preferences = getSharedPreferences("isFirst", 0);
        //默认是没有这个值，那就是找不到就给个默认值为true表示第一次运行
        boolean isfirstRun = preferences.getBoolean("isFirst", true);
        if (isfirstRun) {
            //设置为不是第一次运行
            preferences.edit().putBoolean("isFirst", false).apply();
            //显示关于对话框
            showAboutDialog();
        }
    }






    /**
     * 初始化主视图
     */
    private void init_main_view() {
        //判断有无网络
        if (MyNetworkInfo.isAvailable()) {
            //有网络操作

            //检查本地是否存在当天照片,优先使用本地照片
            if (!loadingImageFromlocal()) {
                //网络操作

                //把islocalImage标识改为false
                islocalImage = false;
                //加载中gif
                Glide.with(mainImage)
                        .load(R.drawable.loading)
                        .into(mainImage);
                //网请求回调数据
                CoreNet.sendResquest(new HttpCallbackListener() {
                    @Override
                    public void finish(String enddate, String copyright) {
                        //获取当天壁纸路径
                        File file = new File(
                                getExternalFilesDir("image").getPath() + "/" + DateFormat.Today() + ".jpg");
                        //加载图片
                        loadingImage(file.getPath(), enddate, copyright);
                    }

                    @Override
                    public void error(Exception e) {

                        loadingError();
                    }
                });
            }

        } else {
            //无网络操作

            //如果本地壁纸不存在的话
            if (!loadingImageFromlocal()) {
                loadingError();
            }
        }
    }

    /**
     * 初始化各种服务
     */
    private void init_services() {

        //接收网络变化广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);//进行注册


    }

    /**
     * 各种监听回调
     */
    private void init_listener() {
        //NavigationView滑动菜单初始化及监听按钮事件
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.setting_nav_item: {
                        Intent intent = new Intent(MainActivity.this, Setting.class);
                        intent.putExtra("local_path", getExternalFilesDir("image").toString());
                        //带动画效果启动活动
                        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
//                        startActivity(new Intent(MainActivity.this, Setting.class));
                    }
                    break;
                    case R.id.about_nav_item: {
                        showAboutDialog();

                        break;
                    }
                    //家族软件
                    case R.id.family_app:
                        startActivity(new Intent(MainActivity.this,FamilyAPP.class));
                        break;
                    case R.id.share_my_friend_nav:
                        //推荐给好友
                        startActivity(new Intent(MainActivity.this,ShareMyself.class));
                        break;

                    case R.id.contact_us_nav:
                        showContactAsDialog();
                        break;
                    case R.id.local_pic: {
                        startActivity(
                                new Intent(MainActivity.this, LocalPicture.class)
                                ,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());


                    }
                    break;

                    default:
                        break;
                }
                return true;
            }
        });

        //滑动菜单头部点击事件
        View drawView = navView.inflateHeaderView(R.layout.nav_header);//由于已经动态添加了，所以xml中下面这句必须删掉，不然就会出现两个头部的情况
        ImageView userPicture = drawView.findViewById(R.id.user_picture);
        userPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加载用户登录界面
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
            }
        });



        //悬浮按钮监听-share按钮
        FloatingActionButton shareButton = findViewById(R.id.float_share);
        shareButton.setOnClickListener(view -> {
            //检测主壁纸是否加载成功
            if (isImageLogin) {
                /**
                 * 分享操作
                 */

                //下载目录
                String dir = getExternalFilesDir("image") + "/" + DateFormat.Today() + ".jpg";
                //打开系统分享器
                MyShare.showSelect(dir, MainActivity.this, getTitle().toString());
            } else {
                Toast.makeText(MainActivity.this, R.string.share_error, Toast.LENGTH_SHORT).show();
            }

        });

        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);//下拉菜单颜色
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();

            }
        });

        //图片点击事件
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检测主壁纸是否加载成功
                if (isImageLogin) {
                    Intent intent = new Intent(MainActivity.this, PicView.class);
                    //intent传输图片路径
                    String dir = getExternalFilesDir("image").getPath() + "/" + DateFormat.Today() + ".jpg";
                    intent.putExtra("imagePath", dir);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 下拉刷新事件监听
     */
    private void refreshView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //加载主视图
                        init_main_view();
                        //关闭刷新
                        swipeRefreshLayout.setRefreshing(false);
                        //弹出提示信息
                        Snackbar.make(getWindow().getDecorView(), "刷新完成！", Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        }).start();

    }

    /**
     * 壁纸加载失败的方法
     */
    private void loadingError() {
        mainImage.setImageResource(R.drawable.error);
        endDate.setText(R.string.msg_title);
        copyRight.setText(R.string.msg_message);
    }

    /**
     * 接在本地图片到主视图
     * true - 图片存在且加载到主视图
     * false  - 图片不存在
     */
    private boolean loadingImageFromlocal() {
        //更改islocalImage标识
        islocalImage = true;
        //获取当天壁纸路径
        File file = new File(
                getExternalFilesDir("image").getPath() + "/" + DateFormat.Today() + ".jpg");

        //如果本地壁纸存在的话
        if (file.exists()) {
            //从本地读取当天壁纸的信息
            SharedPreferences pre = getSharedPreferences(DateFormat.Today(), 0);
            //开始加载壁纸
            loadingImage(
                    pre.getString("uri", "null"),
                    pre.getString("enddate", "null"),
                    pre.getString("copyright", "null"));

            return true;

        } else {
            return false;
        }
    }

    /**
     * 加载图片到主视图
     *
     * @param dir
     * @param enddate
     * @param copyright
     */
    private void loadingImage(String dir, String enddate, String copyright) {
        //读取用户设置状态-设置是否打开被背景图开关
        SharedPreferences preferences = getSharedPreferences("UserHabit", 0);
        boolean mbackgroundSwitch = preferences.getBoolean("background_switch",false);
        //获取布局id
        RelativeLayout layout = findViewById(R.id.relative_layout);
        //回到主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mbackgroundSwitch){
                    //设置布局背景为当前壁纸的高斯模糊
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(dir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //获取bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    //使用别人的类来处理模糊照片 ImageBitmao
                    Drawable drawable =
                            new BitmapDrawable(ImageBitmap.blurBitmap(
                                    MainActivity.this
                                    , bitmap
                                    , 20f));
                    //设置透明度
                    drawable.setAlpha(180);

                    //设置drawable
                    layout.setBackground(drawable);
                }else{
                    //设置布局背景为空白
                    layout.setBackground(null);
                }


                //使用Glide加载图片
                Glide.with(MainActivity.this)
                        .load(dir)
                        .placeholder(R.mipmap.loding)
                        .error(R.mipmap.error)
//                        .circleCrop()圆形图片
                        .autoClone()
//                        .fitCenter()
                        //加载过程中回调
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.d(TAG, "onLoadFailed: 加载失败");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "onResourceReady: 加载成功");
                                //检测是否为本地图片
                                if (!islocalImage) {
                                    //弹出缓存成功的消息
                                    Snackbar.make(getWindow().getDecorView(), "已缓存至本地哦！", Snackbar.LENGTH_LONG).show();
                                }


                                //设置图片加载状态
                                isImageLogin = true;
                                return false;
                            }
                        })
                        .into(mainImage);

                //加载图片信息
                endDate.setText(enddate);
                copyRight.setText("版权来源：" + copyright);
            }
        });


    }

    /**
     * 显示关于对话框
     */
    private void showAboutDialog() {
        Dialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.about_dialog);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    private void showContactAsDialog(){
        Dialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.contact_dialog);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button getEmail = window.findViewById(R.id.get_email_string);
        getEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clip.setText(getString(R.string.email));
                Toast.makeText(MainActivity.this,"已复制邮箱地址",Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 在标题栏加载按钮
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 返回按钮事件
     */
    @Override
    public void onBackPressed() {
        //判断是否打开了滑动菜单，是就关闭掉滑动菜单，否则判断退出
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!mBackKey) {
//            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
                Snackbar.make(getWindow().getDecorView(), "再按一次退出！", Snackbar.LENGTH_SHORT).show();
                mBackKey = true;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        //延时2秒，如果超过则恢复标识为false
                        mBackKey = false;
                    }
                }, 2000);
            } else {
                finish();
            }
        }


    }

    /**
     * 标题栏按钮事件监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_itme:
                finish();
                break;

            case R.id.about_item: {
                showAboutDialog();
                break;
            }
            //监听主页键
            case android.R.id.home:
                //显示出滑动菜单
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;

        }
        return true;
    }
}
