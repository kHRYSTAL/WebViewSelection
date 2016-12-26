package me.khrystal.selectionlib.testselection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import me.khrystal.selectionlib.dragview.DragController;
import me.khrystal.selectionlib.dragview.DragLayer;
import me.khrystal.selectionlib.dragview.DragListener;
import me.khrystal.selectionlib.dragview.DragSource;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/26
 * update time:
 * email: 723526676@qq.com
 */

@SuppressLint("DefaultLocale")
public class TextSelectionSupport implements TextSelectIonCtrlListener, View.OnTouchListener,
        View.OnLongClickListener, DragListener {


    private static final String TAG = "SelectionSupport";
    private static final float CENTERING_SHORTER_MARGIN_RATIO = 12.0f / 48.0f;
    private static final int JACK_UP_PADDING = 2;
    private static final int SCROLLING_THRESHOLD = 10;

    private Activity mActivity;
    private WebView mWebView;
    private SelectionListener mSelectionListener;
    private DragLayer mSelectionDragLayer;
    private DragController mDragController;
    private ImageView mStartSelectionHandler;
    private ImageView mEndSelectionHandler;
    private Rect mSelectionBounds = null;
    private final Rect mSelectionBoundsTemp = new Rect();
    private TextSelectionController mSelectionController = null;
    private int mContentWidth = 0;

    @HanlderType.ModeTypeChecker
    private int mLastTouchedSelectionHandle = HanlderType.TYPE_UNKNOW;

    private boolean mScrolling = false;
    private float mScrollDiffY = 0;
    private float mLastTouchY = 0;
    private float mScrollDiffX = 0;
    private float mLastTouchX = 0;
    private float mScale = 1.0f;

    private Runnable mStartSelectionModeHandler = new Runnable() {
        @Override
        public void run() {
            if (mSelectionBounds != null) {
                mWebView.addView(mSelectionDragLayer);
                //TODO draw selection handle
            }
        }
    };


    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, DragController.DragBehavior behavior) {

    }

    @Override
    public void onDragEnd() {

    }

    @Override
    public void jsError(String error) {

    }

    @Override
    public void jsLog(String message) {

    }

    @Override
    public void startSelectionMode() {

    }

    @Override
    public void endSelectionMode() {

    }

    @Override
    public void selectionChanged(String range, String text, String handleBounds, boolean isReallyChanged) {

    }

    @Override
    public void setContentWidth(float contentWidth) {

    }

    public interface SelectionListener {
        void startSelection();
        void selectionChanged(String text);
        void endSelection();
    }
}
