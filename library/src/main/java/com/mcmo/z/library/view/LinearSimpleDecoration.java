package com.mcmo.z.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 固定颜色的分割线，可以设置最前和最后是否显示分割线
 * Created by zhang wei on 2017/4/21.
 */

public class LinearSimpleDecoration extends RecyclerView.ItemDecoration {
    private int mDividerSize;
    private int mDividerColor;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private int mOrientation;
    private boolean hadFrame;
    private Rect mDividerRect;
    private Paint mPaint;

    /**
     * @param context
     * @param orientation 和layoutmanager的方向一致
     * @param dividerSizeRes 分割线的大小
     * @param colorRes 分割线的颜色
     * @param frame    最前和最后是否显示分割线
     */
    public LinearSimpleDecoration(Context context, int orientation, int dividerSizeRes, int colorRes, boolean frame) {
        hadFrame = frame;
        mDividerRect = new Rect();
        mPaint = new Paint();
        mDividerSize = context.getResources().getDimensionPixelSize(dividerSizeRes);
        mDividerColor = context.getResources().getColor(colorRes);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mDividerColor);
        setOrientation(orientation);
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        } else {
            mOrientation = orientation;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
//        super.onDraw(c, parent, state);
        if (mOrientation == HORIZONTAL_LIST) {
            drawHorizontal(c, parent);
        } else {
            drawVertical(c, parent);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);


    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);

        if (mOrientation == VERTICAL_LIST) {
            if (hadFrame) {
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.set(0, mDividerSize, 0, mDividerSize);
                } else {
                    outRect.set(0, 0, 0, mDividerSize);
                }
            } else {
                if (parent.getChildLayoutPosition(view) != state.getItemCount() - 1) {
                    outRect.set(0, 0, 0, mDividerSize);
                }
            }
        } else {
            if (hadFrame) {
                if (parent.getChildLayoutPosition(view) == 0) {
                    outRect.set(mDividerSize, 0, mDividerSize, 0);
                } else {
                    outRect.set(0, 0, mDividerSize, 0);
                }
            } else {
                if (parent.getChildLayoutPosition(view) != state.getItemCount() - 1) {
                    outRect.set(0, 0, mDividerSize, 0);
                }
            }
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = 0;
            int bottom = 0;
            if (hadFrame) {
                if (i == 0) {
                    bottom = child.getTop() - lp.topMargin;
                    top = bottom - mDividerSize;
                    mDividerRect.set(left, top, right, bottom);
                    c.drawRect(mDividerRect, mPaint);
                }
                top = child.getBottom() + lp.bottomMargin;
                bottom = top + mDividerSize;
                mDividerRect.set(left, top, right, bottom);
                c.drawRect(mDividerRect, mPaint);
            } else {
                if (i != childCount - 1) {
                    top = child.getBottom() + lp.bottomMargin;
                    bottom = top + mDividerSize;
                    mDividerRect.set(left, top, right, bottom);
                    c.drawRect(mDividerRect, mPaint);
                }
            }
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = 0;
            int right = 0;
            if (hadFrame) {
                if(i==0){
                    right = child.getLeft()-lp.leftMargin;
                    left = right -mDividerSize;
                    mDividerRect.set(left,top,right,bottom);
                    c.drawRect(mDividerRect,mPaint);
                }
                left = child.getRight() + lp.rightMargin;
                right = left + mDividerSize;
                mDividerRect.set(left, top, right, bottom);
                c.drawRect(mDividerRect, mPaint);
            } else {
                if(i!=childCount-1){
                    left = child.getRight() + lp.rightMargin;
                    right = left + mDividerSize;
                    mDividerRect.set(left, top, right, bottom);
                    c.drawRect(mDividerRect, mPaint);
                }
            }
        }
    }

}


