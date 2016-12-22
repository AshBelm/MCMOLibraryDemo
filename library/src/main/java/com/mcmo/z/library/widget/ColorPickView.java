package com.mcmo.z.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZhangWei on 2016/12/22.
 */

public class ColorPickView extends View {
    private Paint mColorPaint;//画颜色选择器
    private Paint mFramePaint;//画边框
    private LinearGradient mLGradient;
    private RadialGradient mWhiteRGradient;
    private RadialGradient mBlackRGradient;
    private int color=0xfffffff;
    private OnColorChangeListener mListener;


    public ColorPickView(Context context) {
        this(context,null);
    }

    public ColorPickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorPickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(){
//        LinearGradient linearGradient=new LinearGradient();
    }
}
