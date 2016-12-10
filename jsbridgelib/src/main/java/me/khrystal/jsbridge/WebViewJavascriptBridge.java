package me.khrystal.jsbridge;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/11/28
 * update time:
 * email: 723526676@qq.com
 */

public interface WebViewJavascriptBridge {

    public void send(String data);
    public void send(String data, CallbackFunction responseCallback);
}
