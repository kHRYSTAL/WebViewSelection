package me.khrystal.selectionlib.dragview;

import android.view.View;

/**
 * usage: Interface defining an object where drag operations
 *          originate.
 * author: kHRYSTAL
 * create time: 16/12/10
 * update time:
 * email: 723526676@qq.com
 */
public interface DragSource {

    /**
     * This method is called to determine if the DragSource has something to drag.
     *
     * @return True if there is something to drag
     */
    boolean allowDrag();

    void setDragController(DragController dragController);

    void onDropCompleted(View target, boolean success);
}
