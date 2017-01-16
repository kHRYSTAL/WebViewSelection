package me.khrystal.selectionlib.dragview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e(TAG, "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        // restore the view;
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return mIsDragging;
    }

    public void cancelDrag() {
        endDrag();
    }

    private void endDrag() {
        if (mIsDragging) {
            mIsDragging = false;
            if (mOriginator != null) {
                mOriginator.setVisibility(View.VISIBLE);
            }
            if (mListener != null) {
                mListener.onDragEnd();
            }
            if (mDragView != null) {
                // recycle
                mDragView.remove();
                mDragView = null;
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            recordScreenSize();
        }
        final int screenX = clamp((int) ev.getRawX(), 0, mDisplayMetrics.widthPixels);
        final int screenY = clamp((int) ev.getRawY(), 0, mDisplayMetrics.heightPixels);
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                mMotionDownX = screenX;
                mMotionDownY = screenY;
                mLastDropTarget = null;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
        }
        return mIsDragging;
    }

    /**
     * Sets the view that should handle move events
     *
     * @param view target view
     */
    void setMoveTarget(View view) {
        mMoveTarget = view;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mMoveTarget != null && mMoveTarget.dispatchUnhandledMove(focused, direction);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        // if not dragging not handle drag
        if (!mIsDragging) {
            return false;
        }

        final int action = ev.getAction();
        final int screenX = clamp((int) ev.getRawX(), 0, mDisplayMetrics.widthPixels);
        final int screenY = clamp((int) ev.getRawY(), 0, mDisplayMetrics.heightPixels);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMotionDownX = screenX;
                mMotionDownY = screenY;
                break;
            case MotionEvent.ACTION_MOVE:
                mDragView.move((int) ev.getRawX(), (int) ev.getRawY());
                final int[] coordinates = mCoordinatesTemp;
                DropTarget dropTarget = findDropTarget(screenX, screenY, coordinates);
                if (dropTarget != null) {
                    if (mLastDropTarget == dropTarget) {
                        dropTarget.onDragOver(mDragSource, coordinates[0], coordinates[1],
                                (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                    } else {
                        if (mLastDropTarget != null) {
                            mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
                                    (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                        }
                        dropTarget.onDragEnter(mDragSource, coordinates[0], coordinates[1],
                                (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                    }
                } else {
                    if (mLastDropTarget != null) {
                        mLastDropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
                                (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                    }
                }
                mLastDropTarget = dropTarget;
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    drop(screenX, screenY);
                }
                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelDrag();
        }
        return true;

    }

    private boolean drop(int screenX, int screenY) {
        final int[] coordinates = mCoordinatesTemp;
        final DropTarget dropTarget = findDropTarget((int) screenX, (int) screenY, coordinates);
        if (dropTarget != null) {
            dropTarget.onDragExit(mDragSource, coordinates[0], coordinates[1],
                    (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);

            if (dropTarget.acceptDrop(mDragSource, coordinates[0], coordinates[1],
                    (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo)) {
                dropTarget.onDrop(mDragSource, coordinates[0], coordinates[1],
                        (int) mTouchOffsetX, (int) mTouchOffsetY, mDragView, mDragInfo);
                mDragSource.onDropCompleted((View) dropTarget, true);
            } else {
                mDragSource.onDropCompleted((View) dropTarget, false);
            }
            return true;
        }
        return false;
    }

    private DropTarget findDropTarget(int screenX, int screenY, int[] coordinates) {
        final Rect rect = mRectTemp;
        final ArrayList<DropTarget> dropTargets = mDropTargets;
        final int count = dropTargets.size();
        for (int i = count - 1; i >= 0 ; i--) {
            final DropTarget target = dropTargets.get(i);
            target.getHitRect(rect);
            target.getLocationOnScreen(coordinates);
            rect.offset(coordinates[0] - target.getLeft(), coordinates[1] - target.getTop());
            if (rect.contains(screenX, screenY)) {
                coordinates[0] = screenX - coordinates[0];
                coordinates[1] = screenY - coordinates[1];
                return target;
            }
        }
        return null;
    }

    private void recordScreenSize() {
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    private static int clamp(int val, int min, int max) {
        if (val < min)
            return min;
        else if (val >= max)
            return max - 1;
        else
            return val;
    }

    public void setDragListener(DragListener listener) {
        mListener = listener;
    }

    public void addDropTarget(DropTarget target) {
        mDropTargets.add(target);
    }

    public void removeDropTarget(DropTarget target) {
        mDropTargets.remove(target);
    }
}
