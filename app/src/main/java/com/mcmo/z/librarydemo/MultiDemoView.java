package com.mcmo.z.librarydemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MultiDemoView extends View {
    private int sideLength = 300;
    private Rect rect;
    private Paint paint;
    private Matrix matrix;

    private int offsetX,offsetY;

    public MultiDemoView(Context context) {
        this(context,null);
    }

    public MultiDemoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MultiDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        rect = new Rect();
        paint = new Paint();
        paint.setColor(0xff00ff00);
        paint.setStyle(Paint.Style.FILL);
        matrix = new Matrix();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        calcRect();
    }
    public void move(int x,int y){
        offsetX += x;
        offsetY += y;
        calcRect();
        invalidate();
    }
    public void scale(float rotate,float scale){
        matrix.setRotate(rotate,getWidth()/2,getHeight()/2);
        matrix.postScale(scale,scale,getWidth()/2,getHeight()/2);
        invalidate();
    }
    private void calcRect() {
        int w = getWidth();
        int h = getHeight();
        int t = (h - sideLength) / 2 + offsetY;
        int b = t + sideLength;
        int l = (w - sideLength) / 2 + offsetX;
        int r = l + sideLength;
        rect.set(l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.setMatrix(matrix);
        canvas.drawRect(rect, paint);
        canvas.restore();
    }
}
