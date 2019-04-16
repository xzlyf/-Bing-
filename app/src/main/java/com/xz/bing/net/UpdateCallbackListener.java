package com.xz.bing.net;

public interface UpdateCallbackListener {
    void finish(String level,String name,String code,String msg,String link);
    void error(Exception e);
}
