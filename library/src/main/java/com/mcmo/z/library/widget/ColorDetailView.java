package com.mcmo.z.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mcmo.z.library.R;

/**
 * Created by ZhangWei on 2016/12/22.
 */

public class ColorDetailView extends View {
    private RadialGradient mRGradient;
    private LinearGradient mLGradient;
    private Bitmap mBitmap;
    private int colorAccent;
    private final int DEFAULT_COLOR = 0xff909090;

    private Paint mPaint;
    private RectF mBound;
    private float DEFAULT_FRAME_WIDTH = 6;
    private float DEFAULT_SELECT_WIDTH = 2;
    private final int[] colors = {0xffffffff, 0xff000000};

    private int color = 0xffff0000;

    private OnColorChangeListener mListener;

    public ColorDetailView(Context context) {
        this(context, null);
    }

    public ColorDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int[] attrsArray = {android.R.attr.colorAccent};
        TypedArray typedArray = context.obtainStyledAttributes(attrsArray);
        colorAccent = typedArray.getColor(0, DEFAULT_COLOR);
        typedArray.recycle();

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorDetailView);
        color = typedArray.getColor(R.styleable.ColorDetailView_detailColor, DEFAULT_COLOR);

        mPaint = new Paint();
        mBound = new RectF();
    }

    public void setColor(int color) {
        this.color = color;
        if (getWidth() != 0 && getHeight() != 0)
            changeColorBitmap(getWidth(), getHeight());
        invalidate();
    }

    public void setOnColorChangeListener(OnColorChangeListener mListener) {
        this.mListener = mListener;
    }

    private float heightScale = 0.9f;

    private void init(int w, int h) {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        float frameSize = DEFAULT_FRAME_WIDTH / 2.0f;
        mBound.set(0 + frameSize, 0 + frameSize, w - frameSize, h - frameSize);

        mLGradient = new LinearGradient(0, 0, 0, mBound.height()-2, colors, null, Shader.TileMode.CLAMP);

        changeColorBitmap(w, h);

    }

    private void changeColorBitmap(int w, int h) {
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(0xffffffff, PorterDuff.Mode.CLEAR);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mLGradient);
        canvas.drawRect(mBound, mPaint);
        //
        float radius=mBound.width()*0.94f;
        mRGradient = new RadialGradient(w, 0, radius, color, color & 0x00ffffff, Shader.TileMode.CLAMP);
        mPaint.setShader(mRGradient);
        float scale = (h * heightScale) / radius;
        canvas.save();
        canvas.scale(1, scale);
        canvas.drawRect(mBound.left, mBound.top, mBound.right, mBound.bottom / scale, mPaint);
        canvas.restore();
        //
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorAccent);
        mPaint.setStrokeWidth(DEFAULT_FRAME_WIDTH);
        canvas.drawRect(0, 0, w, h, mPaint);
        //
        mPaint.setStrokeWidth(DEFAULT_SELECT_WIDTH);
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
            hSize = getResources().getDimensionPixelSize(R.dimen.color_pick_default_height_detail);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, getPaddingLeft(), getPaddingTop(), null);
            canvas.drawCircle(x, y, 6, mPaint);
        }
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = clamp(event.getX(), mBound.left + 0.5f, mBound.right - 0.5f);
        y = clamp(event.getY(), mBound.top + 0.5f, mBound.bottom - 0.5f);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        if (mListener != null) {
            mListener.onColorChanged(mBitmap.getPixel((int) x, (int) y), x, y);
        }
        invalidate();
        return true;
    }

    private float clamp(float x, float min, float max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return x;
        }
    }
}
