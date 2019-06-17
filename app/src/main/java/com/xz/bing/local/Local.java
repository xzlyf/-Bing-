package com.xz.bing.local;

/**
 * 全局变量
 */
public class Local {

    public static boolean ifFirstRun = true;//是否第一次运行
    public static final String UPDATE_LINK = "https://xzlyf.github.io/DayWallpaperUpdate/update.xml";//更新服务器地址

    /**
     * 设置界面按钮状态
     */
    public static boolean to_album = false;//显示在系统相册
    public static boolean auto_update = false;//开启自动更新
    /**
     * 版本信息
     */
    public static int localVersionCode = -1;//本地版本代号
    public static String localVersionName = "";//本地版本名称
    public static int cloudVersionCode = -1;//云端版本代号
    public static String clodVersionName = "";//云端版本名称


}
