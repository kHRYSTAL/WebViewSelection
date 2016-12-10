package me.khrystal.selectionlib.dragview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * usage:
 * author: kHRYSTAL
 * create time: 16/12/10
 * update time:
 * email: 723526676@qq.com
 */

public class DragView extends View {

    private static final int PADDING_TO_SCALE = 0;

    private DragBean mDragBean;

    private final int mRegistrationX;

    private final int mRegistrationY;

    private Bitmap mBitmap;

    private Paint mDebugPaint = new Paint();

    private WindowManager.LayoutParams mLayoutParams;

    private WindowManager mWindowManager;

    public DragView(Context context) throws Exception {
        super(context);
        mRegistrationX = 0;
        mRegistrationY = 0;
        throw new Exception("DragView constructor permits only programatical calling");
    }

    public DragView(Context context, DragBean dragBean) {
        super(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mDragBean = dragBean;
        mRegistrationX = dragBean.registrationX + (PADDING_TO_SCALE / 2);
        mRegistrationY = dragBean.registrationY + (PADDING_TO_SCALE / 2);
        final float scaleFactor = ((float) dragBean.width + PADDING_TO_SCALE) / (float) dragBean.width;
        final Matrix scale = new Matrix();
        scale.setScale(scaleFactor, scaleFactor);
        mBitmap = Bitmap.createBitmap(dragBean.bitmap, dragBean.left, dragBean.top,
                dragBean.width, dragBean.height, scale, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmap.recycle();
    }

    /**
     * todo 6.0 need require window permission
     * @param windowToken
     * @param touchX
     * @param touchY
     */
    void show(IBinder windowToken, int touchX, int touchY) {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                touchX - mRegistrationX, touchY - mRegistrationY,
                WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL,
                // NOTICE TEST MIUI need open permission in settings and OPPO cant find this permission
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.token = windowToken;
        lp.setTitle("DragView");
        mLayoutParams = lp;
        mWindowManager.addView(this, lp);
    }

    void move(int touchX, int touchY) {
        WindowManager.LayoutParams lp = mLayoutParams;
        lp.x = touchX - mRegistrationX;
        lp.y = touchY - mRegistrationY;
        mWindowManager.updateViewLayout(this, lp);
    }

    void remove() {
        mWindowManager.removeView(this);
    }
}
