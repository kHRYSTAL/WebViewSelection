package me.khrystal.selectionlib.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/13
 * update time:
 * email: 723526676@qq.com
 */

public class DragLayer extends CopyAbsoluteLayout implements DragSource, DropTarget {

    private DragController mDragController;

    public DragLayer (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragController.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDragController.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return mDragController.dispatchUnhandledMove(focused, direction);
    }

    // Interfaces of DragSource
    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void setDragController(DragController controller) {
        mDragController = controller;
    }

    @Override
    public void onDropCompleted(View target, boolean success) {

    }

    // Interfaces of DropTarget
    @Override
    public void onDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                       DragView dragView, Object dragInfo) {

        final View v = (View) dragInfo;
        final int w = v.getWidth();
        final int h = v.getHeight();
        final int left = x - xOffset;
        final int top = y - yOffset;
        final DragLayer.LayoutParams lp = new DragLayer.LayoutParams (w, h, left, top);
        updateViewLayout(v, lp);
    }

    @Override
    public void onDragEnter(DragSource source, int x, int y, int xOffset, int yOffset,
                            DragView dragView, Object dragInfo) {

    }

    @Override
    public void onDragOver(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo) {

    }

    @Override
    public void onDragExit(DragSource source, int x, int y, int xOffset, int yOffset,
                           DragView dragView, Object dragInfo) {
    }
    @Override
    public boolean acceptDrop(DragSource source, int x, int y, int xOffset, int yOffset,
                              DragView dragView, Object dragInfo) {
        return true;
    }
    @Override
    public Rect estimateDropLocation(DragSource source, int x, int y, int xOffset, int yOffset,
                                     DragView dragView, Object dragInfo, Rect recycle) {
        return null;
    }
}
