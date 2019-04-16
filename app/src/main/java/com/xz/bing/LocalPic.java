package com.xz.bing;

/**
 * 负责管理加载本地图片数据的类
 * 用于给本地缓存类的recyclerView列表提供适配器
 */
public class LocalPic {
    private String enddate;
    private String uri;
    private String copyright;

    public LocalPic(String enddate, String uri, String copyright) {
        this.copyright = copyright;
        this.uri = uri;
        this.enddate = enddate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
}
