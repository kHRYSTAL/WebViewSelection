package me.khrystal.selectionlib.dragview;

/**
 * usage: interface to receive notifications when a drag starts or stops
 * author: kHRYSTAL
 * create time: 16/12/13
 * update time:
 * email: 723526676@qq.com
 */

public interface DragListener {
    void onDragStart(DragSource source, Object info, DragController.DragBehavior behavior);

    void onDragEnd();
}
