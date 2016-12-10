package me.khrystal.jsbridge;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/11/28
 * update time:
 * email: 723526676@qq.com
 */

public class DefaultHandler implements BridgeHandler {

    private static final String TAG = "DefaultHandler";

    @Override
    public void handler(String data, CallbackFunction function) {
        if (function != null) {
            function.onCallback("DefaultHandler response data");
        }
    }
}
