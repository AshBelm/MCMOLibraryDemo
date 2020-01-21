package com.mcmo.z.librarydemo.statusbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.mcmo.z.library.sys.StatusBarUtil;
import com.mcmo.z.librarydemo.R;

public class StatusBarActivity extends AppCompatActivity {
    private static final String TAG = "StatusBarActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        setCustomDensity(this);
        //如果设置了上面这行代码会发现有context和没有两个获取到的值不一样。
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_bar);
        //设置状态栏颜色
//        StatusBarUtil.setColor(this,getResources().getColor(R.color.colorAccent));
        //设置状态栏为半透明
        StatusBarUtil.setTransparent(this,false);

        //适配齐刘海
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
//            getWindow().setAttributes(layoutParams);
//            //但是一些手机厂商的刘海屏手机系统版本是低于Android P的，不过也都提供了适配的方法。
//            // 适配方式是在AndroidManifest.xml文件里的application标签下添加如下代码：
//            /*
//            <!-- 允许绘制到小米刘海屏机型的刘海区域 -->
//                <meta-data
//                    android:name="notch.config"
//                    android:value="portrait" />
//                <!-- 允许绘制到华为刘海屏机型的刘海区域 -->
//                <meta-data
//                    android:name="android.notch_support"
//                    android:value="true" />
//                <!-- 允许绘制到oppo、vivo刘海屏机型的刘海区域 -->
//                <meta-data
//                    android:name="android.max_aspect"
//                    android:value="2.2" />
//             */
//        }
        //首先应该将activity的主题设置为“noActionBar"
        Log.e(TAG, "onCreate: " + StatusBarUtil.getHeight() + " = " + StatusBarUtil.getHeight(this));
    }

    private boolean a = true;

    public void onButton1Click(View view) {
        if (a) {
            StatusBarUtil.setColor(this, 0xffff0000);
        } else {
            StatusBarUtil.setTranslucent(this);
//            StatusBarUtil.setColor(this, 0xffffffff);
        }
        a = !a;
    }

    private void setCustomDensity(Activity activity) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        final float tagetDenisity = displayMetrics.widthPixels / 720;
        final int targetDenityDpi = (int) (160 * tagetDenisity);
        displayMetrics.density = displayMetrics.scaledDensity = tagetDenisity;
        displayMetrics.densityDpi = targetDenityDpi;
    }

    public void onGotoClick(View view) {
        Intent intent = new Intent(this,MultiStatusBarActivity.class);
        startActivity(intent);
    }

    public void onGotoClickVp(View view) {
        Intent intent = new Intent(this,MultiStatusBarViewPageActivity.class);
        startActivity(intent);

    }
}
