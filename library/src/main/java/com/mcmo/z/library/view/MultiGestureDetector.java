package com.mcmo.z.library.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * Created by ZhangWei on 2016/11/17.
 */

public class MultiGestureDetector {
    private static final String TAG = "MultiGestureDetector";
    private int mTouchSlopSquare;
    private int mDoubleTapTouchSlopSquare;
    private int mDoubleTapSlopSquare;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;

    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private static final int DOUBLE_TAP_MIN_TIME = 40;//ViewConfiguration.getDoubleTapMinTime();为隐藏方法

    private float mLastFocusX;
    private float mLastFocusY;
    private float mDownFocusX;
    private float mDownFocusY;

    private boolean mInProgress = true;
    private float mCurrSpan;
    private float mPrevSpan;
    private float mInitialSpan;
    private float mCurrSpanX;
    private float mCurrSpanY;
    private float mPrevSpanX;
    private float mPrevSpanY;

    private float mCurrK;
    private float mPrevK;
    private float mInitialK;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    private boolean mStillDown;
    private boolean mAlwaysInTapRegion;
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mInLongPress;
    private boolean mIsDoubleTapping;
    private boolean mDeferConfirmSingleTap;


    private final int WHAT_TAP = 1;
    private final int WHAT_LONG_PRESS = 2;

    private Handler mHandler;
    private VelocityTracker mVelocityTracker;
    private MovementListener mMovementListener;
    private ClickListener mClickListener;
    private DoubleFingerListener mDoubleFingerListener;

    public MultiGestureDetector(Context context, MultiGestureListener listener) {
        this(context, listener, null);
    }

    public MultiGestureDetector(Context context, MultiGestureListener listener, Handler handler) {
        if (handler == null) {
            mHandler = new GestureListener();
        } else {
            mHandler = new GestureListener(handler);
        }
        if (listener != null) {
            if (listener instanceof MovementListener) {
                mMovementListener = (MovementListener) listener;
            }
            if (listener instanceof ClickListener) {
                mClickListener = (ClickListener) listener;
            }
            if (listener instanceof DoubleFingerListener) {
                mDoubleFingerListener = (DoubleFingerListener) listener;
            }
        }
        init(context);
    }

    private void init(Context context) {
        //TO-DO 没有监听报异常
        int touchSlop, doubleTapTouchSlop;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
            doubleTapTouchSlop = touchSlop;
            mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
        } else {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
            doubleTapTouchSlop = configuration.getScaledDoubleTapSlop();
            mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        }
        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapTouchSlopSquare = doubleTapTouchSlop * doubleTapTouchSlop;
    }

    /**
     * 获取两次触摸事件之间的缩放比例
     *
     * @return
     */
    public float getScaleFactor() {
        return mPrevSpan > 0 ? mCurrSpan / mPrevSpan : 1;
    }

    /**
     * 获取从缩放开始到本次触摸事件的缩放比例
     *
     * @return
     */
    public float getScaleFactorAll() {
        return mInitialSpan > 0 ? mCurrSpan / mInitialSpan : 1;
    }

    /**
     * 获取两次触摸事件之间的旋转角度(degree)
     *
     * @return
     */
    public float getRotateFactor() {
        boolean curNaN = Float.isNaN(mCurrK);
        boolean preNaN = Float.isNaN(mPrevK);
        if (curNaN && preNaN) {
            return 0;
        }
        if (curNaN || preNaN) {
            if (curNaN) {
                curNaN = mPrevK > 0;
            }
            if (preNaN) {
                preNaN = mCurrK > 0;
            }
            float currAngle = Float.isNaN(mCurrK) ? curNaN ? 90 : -90 : (float) Math.toDegrees(Math.atan(mCurrK));
            float prevAngle = Float.isNaN(mPrevK) ? preNaN ? 90 : -90 : (float) Math.toDegrees(Math.atan(mPrevK));
            return currAngle - prevAngle;
        } else {
            float o = (mCurrK - mPrevK) / (1 + mCurrK * mPrevK);
            return (float) Math.toDegrees(Math.atan(o));
        }
    }

    /**
     * 获取从缩放开始到本次触摸事件的旋转角度(degree)
     *
     * @return
     */
    public float getRotateFactorAll() {
        float currAngle = Float.isNaN(mCurrK) ? 90 : (float) Math.toDegrees(Math.atan(mCurrK));
        float initAngle = Float.isNaN(mInitialK) ? 90 : (float) Math.toDegrees(Math.atan(mInitialK));
        return currAngle - initAngle;
    }

    /**
     * 获取两指间距离
     *
     * @return
     */
    public float getCurrSpan() {
        return mCurrSpan;
    }

    /**
     * 获取上一次移动是两指间距离
     *
     * @return
     */
    public float getPrevSpan() {
        return mPrevSpan;
    }

    /**
     * 获取两指操作开始时两指间距离
     *
     * @return
     */
    public float getInitialSpan() {
        return mInitialSpan;
    }

    /**
     * 获取两指间水平方向距离
     *
     * @return
     */
    public float getCurrSpanX() {
        return mCurrSpanX;
    }

    /**
     * 获取两指间垂直方向距离
     *
     * @return
     */
    public float getCurrSpanY() {
        return mCurrSpanY;
    }

    /**
     * 获取上一次触摸事件两指间水平距离
     *
     * @return
     */
    public float getPrevSpanX() {
        return mPrevSpanX;
    }

    /**
     * 获取上一次触摸事件两指间垂直距离
     *
     * @return
     */
    public float getPrevSpanY() {
        return mPrevSpanY;
    }

    /**
     * 获取两指间连线的斜率
     *
     * @return
     */
    public float getCurrK() {
        return mCurrK;
    }

    /**
     * 获取上一次两指间连线的斜率
     *
     * @return
     */
    public float getPrevK() {
        return mPrevK;
    }

    /**
     * 获取两指事件触发是两指连线的斜率
     *
     * @return
     */
    public float getInitialK() {
        return mInitialK;
    }

    /**
     * 取消掉所有的点击事件
     */
    private void clearTaps() {
        mHandler.removeMessages(WHAT_TAP);
        mHandler.removeMessages(WHAT_LONG_PRESS);
        mAlwaysInTapRegion = false;
    }

    private void clearDoubleFingerData() {
        mInProgress = false;
        mCurrSpan = 0;
        mPrevSpan = 0;
        mInitialSpan = 0;
        mCurrSpanX = 0;
        mCurrSpanY = 0;
        mPrevSpanX = 0;
        mPrevSpanY = 0;
        mInitialK = 0;
        mCurrK = 0;
        mPrevK = 0;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final boolean pointerUp = (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? ev.getActionIndex() : -1;
        float sumX = 0, sumY = 0;
        final int count = ev.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += ev.getX(i);
            sumY += ev.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final boolean isDoublePointer = div == 2;//是否是双指
        final float focusX = sumX / div;//所以手指的中点x
        final float focusY = sumY / div;//所以手指的中点y

        boolean handled = false;
        handled |= (mClickListener != null || mDoubleFingerListener != null);//如果需要检测点击事件和scale rotate必须要返回true

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://第一根手指落下
                boolean hadTapMessage = mHandler.hasMessages(WHAT_TAP);
                if (hadTapMessage) mHandler.removeMessages(WHAT_TAP);
                clearDoubleFingerData();
                if (mCurrentDownEvent != null && mPreviousUpEvent != null && hadTapMessage
                        && isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent, ev)) {
                    //双击事件的开始，这里可以添加双击的按下事件
                    mIsDoubleTapping = true;
                }
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                if (mCurrentDownEvent != null) {
                    mCurrentDownEvent.recycle();
                }
                mCurrentDownEvent = MotionEvent.obtain(ev);
                mAlwaysInTapRegion = true;
                mAlwaysInBiggerTapRegion = true;
                mStillDown = true;
                mInLongPress = false;
                mDeferConfirmSingleTap = false;
                //启动长按计时
                mHandler.removeMessages(WHAT_LONG_PRESS);
                mHandler.sendEmptyMessageAtTime(WHAT_LONG_PRESS, mCurrentDownEvent.getDownTime() + LONGPRESS_TIMEOUT);

                if (mMovementListener != null) {
                    handled |= mMovementListener.onDown(ev);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN://除第一根外，其他手指落下
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                clearTaps();
                break;
            case MotionEvent.ACTION_POINTER_UP://除最后一根外，其他手指离开
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                if (mInProgress && mDoubleFingerListener != null) {
                    mDoubleFingerListener.onScaleRotateEnd(this);
                }
                clearDoubleFingerData();
                // Check the dot product of current velocities.
                // If the pointer that left was opposing another velocity vector, clear.
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                final int upIndex = ev.getActionIndex();
                final int id1 = ev.getPointerId(upIndex);
                final float x1 = mVelocityTracker.getXVelocity(id1);
                final float y1 = mVelocityTracker.getYVelocity(id1);
                for (int i = 0; i < count; i++) {
                    if (i == upIndex) continue;

                    final int id2 = ev.getPointerId(i);
                    final float x = x1 * mVelocityTracker.getXVelocity(id2);
                    final float y = y1 * mVelocityTracker.getYVelocity(id2);

                    final float dot = x + y;
                    if (dot < 0) {
                        mVelocityTracker.clear();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDoublePointer && mDoubleFingerListener != null) {
                    float devSumX = 0, devSumY = 0;
                    for (int i = 0; i < count; i++) {
                        if (skipIndex == i) continue;
                        devSumX += Math.abs(ev.getX(i) - focusX);
                        devSumY += Math.abs(ev.getY(i) - focusY);
                    }
                    final float devX = devSumX / div;
                    final float devY = devSumY / div;
                    final float spanX = devX * 2;
                    final float spanY = devY * 2;
                    final float span = (float) Math.hypot(spanX, spanY);

                    final float deltaX = ev.getX(0) - ev.getX(1);
                    final float deltaY = ev.getY(0) - ev.getY(1);
                    float k = deltaX == 0 ? Float.NaN : deltaY / deltaX;

                    if (!mInProgress) {
                        mInProgress = true;
                        mInitialSpan = span;
                        mPrevSpanX = mCurrSpanX = spanX;
                        mPrevSpanY = mCurrSpanY = spanY;
                        mPrevSpan = mCurrSpan = span;
                        mInitialK = k;
                        mPrevK = mCurrK = k;
                        mDoubleFingerListener.onScaleRotateBegin(this);
                    } else {
                        mCurrSpanX = spanX;
                        mCurrSpanY = spanY;
                        mCurrSpan = span;
                        mCurrK = k;

                        boolean updatePrev = mDoubleFingerListener.onScaleRotate(this);

                        if (updatePrev) {
                            mPrevSpanX = mCurrSpanX;
                            mPrevSpanY = mCurrSpanY;
                            mPrevSpan = mCurrSpan;
                            mPrevK = k;
                        }
                    }
                } else {
                    final float scrollX = focusX - mLastFocusX;
                    final float scrollY = focusY - mLastFocusY;
                    if (mAlwaysInTapRegion) {
                        final int deltaX = (int) (focusX - mDownFocusX);
                        final int deltaY = (int) (focusY - mDownFocusY);
                        int distance = deltaX * deltaX + deltaY * deltaY;
                        if (distance > mTouchSlopSquare) {
                            mLastFocusX = focusX;
                            mLastFocusY = focusY;
                            clearTaps();
                        }
                        if (distance > mDoubleTapTouchSlopSquare) {
                            mAlwaysInBiggerTapRegion = false;
                        }
                    } else if (Math.abs(scrollX) >= 1 || Math.abs(scrollY) >= 1) {
                        if (mMovementListener != null) {
                            handled = mMovementListener.onMove(mCurrentDownEvent, ev, scrollX, scrollY);
                        }
                        mLastFocusX = focusX;
                        mLastFocusY = focusY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP://最后一根手指离开
                mStillDown = false;
                if (mInLongPress) {
                    mInLongPress = false;
                } else if (mIsDoubleTapping && mAlwaysInBiggerTapRegion && mClickListener != null) {
                    mClickListener.onDoubleTap(ev);
                } else if (mAlwaysInTapRegion) {
                    //可以添加点击事件抬起，但还这时还不能确定是一次点击
                    if (mDeferConfirmSingleTap) {
                        if(mClickListener!=null)
                        mClickListener.onSingleTap(ev);
                    }else{
                        mHandler.sendEmptyMessageDelayed(WHAT_TAP,DOUBLE_TAP_TIMEOUT);
                    }
                } else if (mMovementListener != null) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    final int pointerId = ev.getPointerId(0);
                    velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                    final float velocityX = velocityTracker.getXVelocity(pointerId);
                    final float velocityY = velocityTracker.getYVelocity(pointerId);
                    if (Math.abs(velocityX) > mMinimumFlingVelocity || Math.abs(velocityY) > mMinimumFlingVelocity) {
                        handled |= mMovementListener.onFling(mCurrentDownEvent, ev, velocityX, velocityY);
                    }else{
                        mMovementListener.onUp(ev);
                    }
                }
                clearDoubleFingerData();
                if (mPreviousUpEvent != null) {
                    mPreviousUpEvent.recycle();
                }
                mPreviousUpEvent = MotionEvent.obtain(ev);
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mIsDoubleTapping = false;
                mDeferConfirmSingleTap = false;
                mHandler.removeMessages(WHAT_LONG_PRESS);
                break;
            case MotionEvent.ACTION_CANCEL:
                cancel();
                break;
        }
        return handled;
    }

    private void cancel() {
        clearTaps();
        clearDoubleFingerData();
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        mIsDoubleTapping = false;
        mStillDown = false;
        mAlwaysInTapRegion = false;
        mAlwaysInBiggerTapRegion = false;
        mDeferConfirmSingleTap = false;
        mInLongPress = false;
    }

    private class GestureListener extends Handler {
        public GestureListener() {
            super();
        }

        public GestureListener(Handler handler) {
            super(handler.getLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_TAP:
                    //双击超时后执行，
                    if (mIsDoubleTapping) {
                        mDeferConfirmSingleTap = true;
                    } else {
                        if (!mStillDown && mClickListener != null) {
                            mClickListener.onSingleTap(mCurrentDownEvent);
                        }
                    }
                    break;
                case WHAT_LONG_PRESS:
                    dispatchLongPress();
                    break;
            }
        }
    }

    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp,
                                          MotionEvent secondDown) {
        if (!mAlwaysInBiggerTapRegion) {
            return false;
        }

        final long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > DOUBLE_TAP_TIMEOUT || deltaTime < DOUBLE_TAP_MIN_TIME) {
            return false;
        }

        int deltaX = (int) firstDown.getX() - (int) secondDown.getX();
        int deltaY = (int) firstDown.getY() - (int) secondDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mDoubleTapTouchSlopSquare);
    }

    private void dispatchLongPress() {
        if (mAlwaysInTapRegion && mClickListener != null) {
            mHandler.removeMessages(WHAT_TAP);
            mInLongPress = true;
            mClickListener.onLongPress(mCurrentDownEvent);
        }
    }

    //<editor-fold desc="interface">
    public interface MultiGestureListener {
    }

    public interface MovementListener extends MultiGestureListener {
        boolean onDown(MotionEvent ev);

        boolean onMove(MotionEvent downEv, MotionEvent ev, float scrollX, float scrollY);

        boolean onUp(MotionEvent ev);

        boolean onFling(MotionEvent downEv, MotionEvent ev, float velocityX, float velocityY);
    }

    public interface ClickListener extends MultiGestureListener {
        void onSingleTap(MotionEvent ev);

        void onDoubleTap(MotionEvent ev);

        void onLongPress(MotionEvent ev);
    }

    public interface DoubleFingerListener extends MultiGestureListener {
        boolean onScaleRotateBegin(MultiGestureDetector detector);

        boolean onScaleRotate(MultiGestureDetector detector);

        boolean onScaleRotateEnd(MultiGestureDetector detector);
    }
    //</editor-fold>

}