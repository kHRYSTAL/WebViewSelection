package me.khrystal.selectionlib.textselection;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/14
 * update time:
 * email: 723526676@qq.com
 */

public interface TextSelectIonCtrlListener {

    void jsError(String error);

    void jsLog(String message);

    void startSelectionMode();

    void endSelectionMode();

    void selectionChanged(String range, String text, String handleBounds, boolean isReallyChanged);

    void setContentWidth(float contentWidth);

}
