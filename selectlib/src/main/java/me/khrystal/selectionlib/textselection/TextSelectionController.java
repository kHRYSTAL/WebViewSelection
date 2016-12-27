package me.khrystal.selectionlib.textselection;

import android.webkit.JavascriptInterface;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/26
 * update time:
 * email: 723526676@qq.com
 */

public class TextSelectionController {

    public static final String TAG = "TextSelectionController";
    public static final String INTERFACE_NAME = "TextSelection";

    private TextSelectIonCtrlListener mListener;

    public TextSelectionController(TextSelectIonCtrlListener listener) {
        mListener = listener;
    }

    //TODO need replace
    @JavascriptInterface
    public void jsLog(String message) {
        if (mListener != null) {
            mListener.jsLog(message);
        }
    }

    // TODO need replace
    @JavascriptInterface
    public void jsError(String error) {
        if (mListener != null) {
            mListener.jsError(error);
        }
    }

    //TODO need replace
    @JavascriptInterface
    public void startSelectionMode() {
        if (mListener != null) {
            mListener.startSelectionMode();
        }
    }

    //TODO need replace
    @JavascriptInterface
    public void endSelectionMode() {
        if (mListener != null) {
            mListener.endSelectionMode();
        }
    }

    //TODO need replace
    @JavascriptInterface
    public void selectionChanged(String range, String text, String hanldeBounds,
                                 boolean isReallyChanged) {
        if (mListener != null) {
            mListener.selectionChanged(range, text, hanldeBounds, isReallyChanged);
        }
    }

    //TODO need replace
    @JavascriptInterface
    public void setContentWidth(float contentWidth) {
        if (mListener != null) {
            mListener.setContentWidth(contentWidth);
        }
    }

}
