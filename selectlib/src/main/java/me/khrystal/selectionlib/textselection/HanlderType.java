package me.khrystal.selectionlib.textselection;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/26
 * update time:
 * email: 723526676@qq.com
 */

public class HanlderType {

    @ModeTypeChecker
    public static final int TYPE_START = 1;

    @ModeTypeChecker
    public static final int TYPE_END = 2;

    @ModeTypeChecker
    public static final int TYPE_UNKNOW = 3;

    @IntDef({TYPE_START, TYPE_END, TYPE_UNKNOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModeTypeChecker{}
}
