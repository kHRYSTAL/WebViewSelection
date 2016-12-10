package me.khrystal.selectionlib.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

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
}
