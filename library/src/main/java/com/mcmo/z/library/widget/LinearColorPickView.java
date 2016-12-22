package com.mcmo.z.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.EventLog;
import android.view.MotionEvent;
import android.view.View;

import com.mcmo.z.library.R;

/**
 * Created by ZhangWei on 2016/12/22.
 */

public class LinearColorPickView extends View {
    private LinearGradient mLGradient;
    private final int[] colors = {0xffff0000, 0xffffff00, 0xff00ff00, 0xff00ffff, 0xff0000ff, 0xffff00ff, 0xffff0000};
    private Bitmap mBitmap;
    private int colorAccent;
    private final int DEFAULT_COLOR=0xff909090;
    private Paint mPaint;
    private RectF mBound;
    private float DEFAULT_FRAME_WIDTH=6;
    private float DEFAULT_SELECT_WIDTH=2;
    private OnColorChangeListener mListener;

    public LinearColorPickView(Context context) {
        this(context, null);
    }

    public LinearColorPickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinearColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int[] attrsArray = { android.R.attr.colorAccent };
        TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
        colorAccent = typedArray.getColor(0, DEFAULT_COLOR);
        typedArray.recycle();

        mPaint = new Paint();
        mBound=new RectF();
    }

    public void setOnColorChangeListener(OnColorChangeListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        if (wMode != MeasureSpec.EXACTLY) {
            wSize = getResources().getDimensionPixelSize(R.dimen.color_pick_default_width);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(wSize, wMode);
        }
        if (hMode != MeasureSpec.EXACTLY) {
            hSize = getResources().getDimensionPixelSize(R.dimen.color_pick_default_height_linear);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(hSize, hMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        init(width, height);
    }

    private void init(int w, int h) {
        if(mBitmap!=null){
            mBitmap.recycle();
            mBitmap=null;
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        float frameSize=DEFAULT_FRAME_WIDTH/2.0f;
        mBound.set(0+frameSize, 0+frameSize, w-frameSize, h-frameSize);
        mLGradient = new LinearGradient(0, 0, mBound.width(), 0, colors, null, Shader.TileMode.CLAMP);
        Canvas canvas = new Canvas(mBitmap);
        //
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mLGradient);
        canvas.drawRect(mBound, mPaint);
        //
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorAccent);
        mPaint.setStrokeWidth(DEFAULT_FRAME_WIDTH);
        canvas.drawRect(0,0,w,h,mPaint);
        //
        mPaint.setStrokeWidth(DEFAULT_SELECT_WIDTH);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null){
            canvas.drawBitmap(mBitmap, getPaddingLeft(), getPaddingTop(), null);
            canvas.drawCircle(x,y,6,mPaint);
        }
    }
    private float x, y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=clamp(event.getX(),mBound.left+0.5f,mBound.right-0.5f);
        y=clamp(event.getY(),mBound.top+0.5f,mBound.bottom-0.5f);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        if(mListener!=null){
            mListener.onColorChanged(mBitmap.getPixel((int)x,(int)y),x,y);
        }
        invalidate();
        return true;
    }
    private float clamp(float x, float min, float max){
        if(x<min){
            return min;
        }else if(x>max){
            return max;
        }else{
            return x;
        }
    }
}
