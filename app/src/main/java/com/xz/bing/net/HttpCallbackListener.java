package com.xz.bing.net;

import java.io.InputStream;

/**
 * 网络请求回调结果
 */
public interface HttpCallbackListener {
    void finish( String enddate, String copyright);
    void error(Exception e);

}
