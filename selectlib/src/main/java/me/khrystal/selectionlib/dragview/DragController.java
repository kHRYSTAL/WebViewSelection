package me.khrystal.selectionlib.dragview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/10
 * update time:
 * email: 723526676@qq.com
 */

public class DragController {

    public enum DragBehavior {
        MOVE, // indicates the drag is move
        COPY  // indicates the drag is copy
    }

    public static final String TAG = "DragController";

    private Context mContext;
    private Rect mRectTemp = new Rect();
    private final int[] mCoordinatesTemp = new int[2];
    private boolean mIsDragging;
    private float mMotionDownX;
    private float mMotionDownY;
    private DisplayMetrics mDisplayMetrics = new DisplayMetrics();

    /** Original view that is being dragged */
    private View mOriginator;

    /** X offset from the upper-left corner of the cell to where we touched. */
    private float mTouchOffsetX;

    /** Y offset from the upper-left corner of the cell to where we touched. */
    private float mTouchOffsetY;

    /** Where the drag originated */
    private DragSource mDragSource;

    private Object mDragInfo;

    private DragView mDragView;

    /** Who can receive drop events */
    private ArrayList<DropTarget> mDropTargets = new ArrayList<DropTarget>();

    private DragListener mListener;

    /** The window token used as the parent for the dragView */
    private IBinder mWindowToken;

    private View mMoveTarget;

    private DropTarget mLastDropTarget;

    private InputMethodManager mInputMethodManager;

    public DragController(Context context) {
        this.mContext = context;
    }

    public void startDrag(View v, DragSource source, Object dragInfo, DragBehavior behavior) {
        if (source.allowDrag()) {
            mOriginator = v;
            final Bitmap b = getViewBitmap(v);
            if (b != null) {
                final int[] loc = mCoordinatesTemp;
                v.getLocationOnScreen(loc);
                final int screenX = loc[0];
                final int screenY = loc[1];
                startDrag(b, screenX, screenY, 0, 0, b.getWidth(), b.getHeight(),
                        source, dragInfo, behavior);
                b.recycle();
                if (behavior == DragBehavior.MOVE) {
                    v.setVisibility(View.GONE);
                }
            }
        }
    }

    private void startDrag(Bitmap b, int screenX, int screenY, int textureLeft,
                           int textureTop, int textureWidth, int textureHeight,
                           DragSource source, Object dragInfo, DragBehavior behavior) {
        if (mInputMethodManager == null)
            mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        // TODO HTC must use this method twice
        mInputMethodManager.hideSoftInputFromWindow(mWindowToken, 0);
        if (mListener != null)
            mListener.onDragStart(source, dragInfo, behavior);
        final int registrationX = ((int) mMotionDownX) - screenX;
        final int registrationY = ((int) mMotionDownY) - screenY;
        mTouchOffsetX = mMotionDownX - screenX;
        mTouchOffsetY = mMotionDownY - screenY;

        mIsDragging = true;
        mDragSource = source;
        mDragInfo = dragInfo;
        DragBean bean = new DragBean();
        bean.bitmap = b;
        bean.registrationX = registrationX;
        bean.registrationY = registrationY;
        bean.left = textureLeft;
        bean.top = textureTop;
        bean.width = textureWidth;
        bean.height = textureHeight;
        mDragView = new DragView(mContext, bean);
        mDragView.show(mWindowToken, (int) mMotionDownX, (int) mMotionDownY);
    }

    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        // reset drawing cache background color
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0x00000000);
        if (color != 0x00000000) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e(TAG, "failed getViewBitamp(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // restore the view;
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
}
