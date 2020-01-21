package com.mcmo.z.librarydemo.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mcmo.z.library.sys.StatusBarUtil;

public class CustomerStatusBar extends FrameLayout {
    public CustomerStatusBar(Context context) {
        this(context, null);
    }

    public CustomerStatusBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerStatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int statusBarHeight = StatusBarUtil.getHeight(context);
        setPadding(0, statusBarHeight, 0, 0);
    }

}
