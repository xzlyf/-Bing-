package com.xz.bing.net;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.bing.R;
import com.xz.bing.util.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 运用 AsycTask异步任务来处理下载进度条等问题
 * <p>
 * 负责更新下载文件到本地
 */
public class UpdateDownloadTask extends AsyncTask<String, Integer, Boolean> {
    private String link;
    private ProgressBar downloadBar;
    private TextView tips;
    private Dialog dialog;



    public UpdateDownloadTask(String link, ProgressBar downloadBar, TextView tips, Dialog dialog) {
        this.link = link;
        this.downloadBar = downloadBar;
        this.tips = tips;
        this.dialog = dialog;
    }

    /**
     * 初始化
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }



    /**
     * 结束
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Context context = MyApplication.getContext();
        //下载成功
        if (aBoolean == true) {
            tips.setVisibility(View.INVISIBLE);
            dialog.dismiss();


            //打开apk安装器  ，因为7.0以上系统安装有点区别，所以这段代码是仿写别人的
//            作者：chxy_s
//            来源：CSDN
//            原文：https://blog.csdn.net/chxc_yy/article/details/81536875
//            版权声明：本文为博主原创文章，转载请附上博文链接！
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String filePath = context.getExternalFilesDir("temp") + "/apk.apk";
            File file = new File(filePath);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {//大于7.0使用此方法
//                Uri apkUri = FileProvider.getUriForFile(context, "com.xz.bing.fileprovider", file);///-----ide文件提供者名
//
//                //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                Uri contentUri = FileProvider.getUriForFile(context,
                        context.getPackageName() + ".fileprovider", file);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {//小于7.0就简单了
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }

            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

            //弹出通知
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = null;
            //如果版本大于安卓8.0  解决通知不显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Toast.makeText(context,"下载完成，下拉通知栏点击进入安装",Toast.LENGTH_SHORT).show();
                NotificationChannel mChannel = new NotificationChannel("change_1", "每日壁纸", NotificationManager.IMPORTANCE_LOW);
                manager.createNotificationChannel(mChannel);
                notification = new Notification.Builder(context)
                        .setChannelId("change_1")
                        .setContentTitle("下载成功")
                        .setSmallIcon(R.drawable.logo_max)
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .setContentText("已下载最新 [每日壁纸]安装包,点击安装吧！")
                        .setSmallIcon(R.mipmap.ic_launcher).build();

            }else{
                notification = new NotificationCompat.Builder(context, "default")
                        .setContentTitle("下载成功")
                        .setContentText("已下载最新 [每日壁纸]安装包,点击安装吧！")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.logo_max)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .build();
            }


            manager.notify(1, notification);

        } else {
            tips.setVisibility(View.INVISIBLE);
            dialog.dismiss();
            //弹出通知
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setContentTitle("下载失败")
                    .setContentText("当前网络异常，请重试哦！")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_max)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();

            manager.notify(1, notification);

        }
    }

    /**
     * ui操作
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        downloadBar.setProgress(values[0]);
        tips.setText(values[0] + "%");
    }

    /**
     * 耗时操作
     *
     * @param strings
     * @return true 下载成功
     * false 下载失败
     */
    @Override
    protected Boolean doInBackground(String... strings) {

        Context context = MyApplication.getContext();
        //用过全局Context获取到缓存目录
        String dir = context.getExternalFilesDir("temp").getPath() + "/apk.apk";
        //下载文件-流操作
        InputStream is = null;
        RandomAccessFile save = null;
        Response response = null;
        OkHttpClient client = new OkHttpClient();
        Request rquest = new Request.Builder().url(link).build();

        long fileLen = 0;//apk文件大小
        int progress = 0;
        try

        {
            response = client.newCall(rquest).execute();

            if (response != null) {
                is = response.body().byteStream();
                //获取文件大小
                fileLen = response.body().contentLength();
                save = new RandomAccessFile(dir, "rw");
                byte[] b = new byte[1024];
                int len;
                int total = 0;
                while ((len = is.read(b)) != -1) {
                    total += len;
                    save.write(b, 0, len);
                    //下载进度
                    progress = (int) ((total * 100) / fileLen);

                    //提交Ui更新操作
                    publishProgress(progress);

                }

            }
            response.body().close();


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            //关闭流
            try {
                if (save != null) {
                    save.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
