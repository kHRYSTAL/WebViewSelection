package me.khrystal.selectionlib.textselection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import me.khrystal.selectionlib.R;
import me.khrystal.selectionlib.dragview.CopyAbsoluteLayout;
import me.khrystal.selectionlib.dragview.DragController;
import me.khrystal.selectionlib.dragview.DragLayer;
import me.khrystal.selectionlib.dragview.DragListener;
import me.khrystal.selectionlib.dragview.DragSource;
import me.khrystal.selectionlib.utils.KLog;

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


    private static final String TAG = TextSelectionSupport.class.getSimpleName();

    private static final float CENTERING_SHORTER_MARGIN_RATIO = 12.0f / 48.0f;
    private static final int JACK_UP_PADDING = 2;
    private static final int SCROLLING_THRESHOLD = 10;

    private Activity mActivity;
    private WebView mWebView;
    private SelectionListener mSelectionListener;
    private DragLayer mSelectionDragLayer;
    private DragController mDragController;
    private ImageView mStartSelectionHandle;
    private ImageView mEndSelectionHandle;
    private Rect mSelectionBounds = null;
    private final Rect mSelectionBoundsTemp = new Rect();
    private TextSelectionController mSelectionController = null;
    private int mContentWidth = 0;

    @HanlderType.ModeTypeChecker
    private  int mLastTouchedSelectionHandle = HanlderType.TYPE_UNKNOW;

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
                drawSelectionHandles();
                final int contentHeight = (int) Math.ceil(getDensityDependentValue(mWebView.getContentHeight(), mActivity));
                final int contentWidth = mWebView.getWidth();
                ViewGroup.LayoutParams layoutParams = mSelectionDragLayer.getLayoutParams();
                layoutParams.width = Math.max(contentWidth, mContentWidth);
                mSelectionDragLayer.setLayoutParams(layoutParams);
                if (mSelectionListener != null) {
                    mSelectionListener.startSelection();
                }
            }
        }
    };

    private Runnable endSelectionModeHandler = new Runnable() {
        @Override
        public void run() {
            mWebView.removeView(mSelectionDragLayer);
            mSelectionBounds = null;
            mLastTouchedSelectionHandle = HanlderType.TYPE_UNKNOW;
            // TODO need replace
            mWebView.loadUrl("javascript: android.selection.clearSelection();");
            if (mSelectionListener != null) {
                mSelectionListener.endSelection();
            }
        }
    };
    private boolean inSelectionMode;

    private TextSelectionSupport(Activity activity, WebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    public static TextSelectionSupport support(Activity activity, WebView webView) {
        final TextSelectionSupport selectionSupport = new TextSelectionSupport(activity, webView);
        selectionSupport.setup();
        return selectionSupport;
    }

    /**
     * javaScript Enable and setUp
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setup() {
        mScale = mActivity.getResources().getDisplayMetrics().density;
        mWebView.setOnLongClickListener(this);
        mWebView.setOnTouchListener(this);
        final WebSettings settings = mWebView.getSettings();
        // TODO: 17/1/5
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSelectionController = new TextSelectionController(this);
        // TODO: 17/1/5  need replace use jsBridge
        mWebView.addJavascriptInterface(mSelectionController, TextSelectionController.INTERFACE_NAME);
        createSelectionLayer(mActivity);
    }

    private void createSelectionLayer(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectionDragLayer = (DragLayer) inflater.inflate(R.layout.selection_drag_layer, null);
        mDragController = new DragController(context);
        mDragController.addDropTarget(mSelectionDragLayer);
        mSelectionDragLayer.setDragController(mDragController);
        mStartSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.startHandle);
        mStartSelectionHandle.setTag(HanlderType.TYPE_START);

        mEndSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.endHandle);
        mEndSelectionHandle.setTag(HanlderType.TYPE_END);

        final View.OnTouchListener handleTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handledHere = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handledHere = startDrag(v);
                   // mLastTouchedSelectionHandle =  (int)v.getTag();
                }
                return false;
            }


        };
    }

    private boolean startDrag(View v) {
        return false;
    }

    public void onScaleChanged(float oldScale, float newScale) {
        mScale = newScale;
    }

    public void setSelectionListener(SelectionListener listener) {
        mSelectionListener = listener;
    }


    @Override
    public boolean onLongClick(View v) {
        if (!isInSelectionMode()) {
            // TODO: 17/1/5 need jsbridge func
            mWebView.loadUrl("");
            mScrolling = true;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final Context context = mActivity;
        float xPoint = getDensityIndependentValue(event.getX(), context) /
                getDensityIndependentValue(mScale, context);
        float yPoint = getDensityIndependentValue(event.getY(), context) /
                getDensityIndependentValue(mScale, context);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO: 17/1/5 need jsbridge func
                final String startTouchUrl = String.format(Locale.getDefault(), "", xPoint, yPoint);
                mLastTouchX = xPoint;
                mLastTouchY = yPoint;
                mWebView.loadUrl(startTouchUrl);
                break;
            case MotionEvent.ACTION_UP:
                if (!mScrolling) {
                    endSelectionMode();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return false;
                }
                mScrollDiffX = 0;
                mScrollDiffY = 0;
                mScrolling = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isInSelectionMode()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mScrollDiffX += (xPoint - mLastTouchX);
                mScrollDiffY += (yPoint - mLastTouchY);
                mLastTouchX = xPoint;
                mLastTouchY = yPoint;
                if (Math.abs(mScrollDiffX) > SCROLLING_THRESHOLD || Math.abs(mScrollDiffY) > SCROLLING_THRESHOLD) {
                    mScrolling = true;
                }
                break;
        }
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, DragController.DragBehavior behavior) {

    }

    @Override
    public void onDragEnd() {
        // TODO: 17/1/5 maybe leak memory
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CopyAbsoluteLayout.LayoutParams startHandleParams =
                        (CopyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
                CopyAbsoluteLayout.LayoutParams endHandleParams =
                        (CopyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
                final Context context = mActivity;
                final float scale = getDensityIndependentValue(mScale, context);
                float startX = startHandleParams.x - mWebView.getScrollX() + mStartSelectionHandle.getWidth() * (1 - CENTERING_SHORTER_MARGIN_RATIO);
                float startY = startHandleParams.y - mWebView.getScrollY() - JACK_UP_PADDING;
                float endX = endHandleParams.x - mWebView.getScrollX() + mEndSelectionHandle.getWidth() * CENTERING_SHORTER_MARGIN_RATIO;
                float endY = endHandleParams.y - mWebView.getScrollY() - JACK_UP_PADDING;

                startX = getDensityIndependentValue(startX, context) / scale;
                startY = getDensityIndependentValue(startY, context) / scale;

                endX = getDensityIndependentValue(endX, context) / scale;
                endY = getDensityIndependentValue(endY, context) / scale;

                if (mLastTouchedSelectionHandle == HanlderType.TYPE_START && startX > 0 && startY > 0) {
                    // TODO: 17/1/5 need jsBridge func
                    String saveStartString = String.format(Locale.getDefault(), "", startX, startY);
                    mWebView.loadUrl(saveStartString);
                } else if (mLastTouchedSelectionHandle == HanlderType.TYPE_END && endX > 0 && endY > 0) {
                    // TODO: 17/1/5 need jsBridge func
                    String saveEndString = String.format(Locale.getDefault(), "", endX, endY);
                    mWebView.loadUrl(saveEndString);
                } else {
                    // TODO: 17/1/5 restore must replace to jsBridge func
                    mWebView.loadUrl("");
                }
            }
        });
    }

    @Override
    public void jsError(String error) {
        KLog.e(TAG, "JSError: " + error);
    }

    @Override
    public void jsLog(String message) {
        KLog.d(TAG, "JSLog: " + message);
    }

    @Override
    public void startSelectionMode() {
        mActivity.runOnUiThread(mStartSelectionModeHandler);
    }

    @Override
    public void endSelectionMode() {
        mActivity.runOnUiThread(endSelectionModeHandler);
    }

    @Override
    public void selectionChanged(String range, String text, String handleBounds, boolean isReallyChanged) {
        final Context context = mActivity;
        try {
            final JSONObject selectionBoundsObject = new JSONObject(handleBounds);
            final float scale = getDensityIndependentValue(mScale, context);
            Rect rect = mSelectionBoundsTemp;
            rect.left = (int) (getDensityDependentValue(selectionBoundsObject.getInt("left"), context) * scale);
            rect.top = (int) (getDensityDependentValue(selectionBoundsObject.getInt("top"), context) * scale);
            rect.right = (int) (getDensityDependentValue(selectionBoundsObject.getInt("right"), context) * scale);
            rect.bottom = (int) (getDensityDependentValue(selectionBoundsObject.getInt("bottom"), context) * scale);
            mSelectionBounds = rect;
            // TODO: 16/12/29
            if (!isInSelectionMode()) {
                startSelectionMode();
            }
            drawSelectionHandles();
            if (mSelectionListener != null && isReallyChanged) {
                mSelectionListener.selectionChanged(text);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setContentWidth(float contentWidth) {
        mContentWidth = (int) getDensityDependentValue(contentWidth, mActivity);
    }

    private void drawSelectionHandles() {
        if (mActivity != null) {
            mActivity.runOnUiThread(drawSelectionHandlesHandler);
        }
    }

    private Runnable drawSelectionHandlesHandler = new Runnable() {
        @Override
        public void run() {
            CopyAbsoluteLayout.LayoutParams startParams = (CopyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
            final int startWidth = mStartSelectionHandle.getDrawable().getIntrinsicWidth();
            startParams.x = (int) (mSelectionBounds.left - startWidth * (1.0f - CENTERING_SHORTER_MARGIN_RATIO));
            startParams.y = (int) (mSelectionBounds.top);
            final int startMinLeft = -(int) (startWidth * (1 - CENTERING_SHORTER_MARGIN_RATIO));
            startParams.x = (startParams.x < startMinLeft) ? startMinLeft : startParams.x;
            startParams.y = (startParams.y < 0) ? 0 : startParams.y;


            mStartSelectionHandle.setLayoutParams(startParams);

            CopyAbsoluteLayout.LayoutParams endParams = (CopyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
            final int endWidth = mEndSelectionHandle.getDrawable().getIntrinsicWidth();
            endParams.x = (int) (mSelectionBounds.right - endWidth * CENTERING_SHORTER_MARGIN_RATIO);
            endParams.y = (int) (mSelectionBounds.bottom);
            final int endMinLeft = -(int) (endWidth * (1 - CENTERING_SHORTER_MARGIN_RATIO));
            endParams.x = (endParams.x < endMinLeft) ? endMinLeft : endParams.x;
            endParams.y = (endParams.y < 0) ? 0 : endParams.y;
            mEndSelectionHandle.setLayoutParams(endParams);
        }
    };

    private float getDensityDependentValue(float val, Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val * (metrics.densityDpi / 160f);
    }

    private float getDensityIndependentValue(float val, Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val / (metrics.densityDpi / 160f);
    }

    //TODO
    public boolean isInSelectionMode() {
        return inSelectionMode;
    }

    public interface SelectionListener {
        void startSelection();

        void selectionChanged(String text);

        void endSelection();
    }
}
