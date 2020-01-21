package com.mcmo.z.library.sys;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mcmo.z.library.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/*
全屏的3中方式
    //方式一
    //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //方式二
    //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    //方式三 style.xml中配置
    //<style name="fullScreen" parent="Theme.AppCompat.DayNight.NoActionBar">
    //        <item name="android:windowFullscreen">true</item>
    //</style>
 */
public class StatusBarUtil {

    public static void setColor(@NonNull Activity activity, int color) {
        setColor(activity.getWindow(), color);
    }

    public static void setColor(@NonNull Window window, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//如果要设置statusBar颜色必须保障bar有背景，并且不是半透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(color);
            setTextDark(window, !isDarkColor(color));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //在设置纯颜色时，我们还需要将该颜色与黑色进行1:1的混合。为什么要这么设置呢？因为状态栏的文字和图标颜色默认是白色的，并且在Android 5.0以下是不能修改的，所以如果修改成较浅的颜色，就会导致状态栏文字看不清的现象，因此做一个比较暗的浮层效果更好一些。
            // TODO: 2020/1/20 优化这里的效果，如果不是特别白不要混合
            setColor(window, ColorUtils.blendARGB(Color.TRANSPARENT, color, 0.5f), false);
        }
    }

    private static void setTextDark(Activity activity, boolean isDark) {
        setTextDark(activity.getWindow(), isDark);
    }

    private static void setTextDark(Window window, boolean isDark) {
        View decorView = window.getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (isDark) {
                decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//在5.0-6.0系统小米和魅族提供的方法
            if (isFlyme4()) {
                setModeForFlyme4(window, isDark);
            } else if (isMIUI6()) {
                setModeForMIUI6(window, isDark);
            }
        }
    }

    /**
     * 这种状态栏文字为亮色主题
     *
     * @param activity
     */
    public static void setTextLightMode(@Nullable Activity activity) {
        setTextDark(activity.getWindow(), false);
    }

    /**
     * 这种状态栏文字为暗色主题
     *
     * @param activity
     */
    public static void setTextDarkMode(@Nullable Activity activity) {
        setTextDark(activity.getWindow(), true);
    }

    public static void setTextModeAuto(@Nullable Activity activity, @ColorInt int color) {
        boolean isDark = isDarkColor(color);
        setTextDark(activity.getWindow(), !isDark);
    }

    /**
     * 判断颜色是否为深色
     *
     * @param color 颜色值
     * @return
     */
    private static boolean isDarkColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    public static void setTransparent(@NonNull Window window, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
            ViewGroup contentView = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
            contentView.getChildAt(0).setFitsSystemWindows(!fullScreen);//这行代码会改变在xml中的fitsSystemWindows的设置。如果希望在xml中配置请删除这行代码
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setColor(window, 0x00000000, true);
        }
    }

    public static void setTransparent(@NonNull Activity activity, boolean fullScreen) {
        setTransparent(activity.getWindow(), fullScreen);
    }

    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.fake_status_bar_view;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setColor(@Nullable Window window, @ColorInt int color, boolean isTransparent) {
        Context context = window.getContext();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View contentView = decorView.findViewById(android.R.id.content);
        if (contentView != null) {
            contentView.setPadding(0, isTransparent ? 0 : getHeight(context), 0, 0);
        }
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setBackgroundColor(color);
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
        } else {
            View statusBarView = new View(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getHeight(context));
            statusBarView.setLayoutParams(layoutParams);
            statusBarView.setBackgroundColor(color);
            statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
            decorView.addView(statusBarView);
        }
    }

    /**
     * 设置为半透明
     *
     * @param activity
     */
    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }


    /**
     * 设置MIUI6+的状态栏的darkMode,darkMode时候字体颜色及icon
     * http://dev.xiaomi.com/doc/p=4769/
     *
     * @param window 目标window
     * @param dark   亮色 or 暗色
     */
    private static void setModeForMIUI6(Window window, boolean dark) {
        try {
            Class<? extends Window> clazz = window.getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            Log.e("StatusBar", "darkIcon: failed");
        }

    }

    /**
     * 设置Flyme4+(魅族)的状态栏的darkMode,darkMode时候字体颜色及icon
     * http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI
     *
     * @param window 目标window
     * @param dark   亮色 or 暗色
     */
    private static void setModeForFlyme4(Window window, boolean dark) {
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            Log.e("StatusBar", "darkIcon: failed");
        }
    }

    /**
     * 判断是否Flyme4以上
     */
    private static boolean isFlyme4() {
        return Build.FINGERPRINT.contains("Flyme_OS_4") || Build.VERSION.INCREMENTAL.contains("Flyme_OS_4")
                || Pattern.compile("Flyme OS [4|5]", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find();
    }

    /**
     * 判断是否MIUI6以上
     */
    private static boolean isMIUI6() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method mtd = clz.getMethod("get", String.class);
            String val = (String) mtd.invoke(null, "ro.miui.ui.version.name");
            val = val.replaceAll("[vV]", "");
            int version = Integer.parseInt(val);
            return version >= 6;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取statusBar高度
     *
     * @return
     */
    public static int getHeight() {
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 获取statusBar高度
     *
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    //<editor-fold desc="这3个方法感觉用处不大">
    public static void setCustomStatusBar(Window window, @ColorInt int color, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Context context = window.getContext();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            View contentView = decorView.findViewById(Window.ID_ANDROID_CONTENT);
            if (contentView != null) {
                contentView.setPadding(0, fullScreen ? 0 : getHeight(context), 0, 0);
            }
            View statusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (statusBarView == null) {
                statusBarView = new View(context);
                statusBarView.setBackgroundColor(color);
                statusBarView.setVisibility(fullScreen ? View.GONE : View.VISIBLE);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getHeight(context));
                statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
                statusBarView.setLayoutParams(lp);
                decorView.addView(statusBarView);
            }
        }
    }

    public static void setCustomStatusBarColor(Window window, @ColorInt int color) {
        Context context = window.getContext();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        View contentView = decorView.findViewById(Window.ID_ANDROID_CONTENT);
        if (contentView != null) {
            contentView.setPadding(0, getHeight(context), 0, 0);
        }
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(View.VISIBLE);
            fakeStatusBarView.setBackgroundColor(color);
        }
    }

    public static void setCustomStatusBarVisibility(Window window, boolean gone) {
        Context context = window.getContext();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        View contentView = decorView.findViewById(Window.ID_ANDROID_CONTENT);
        if (contentView != null) {
            contentView.setPadding(0, gone ? 0 : getHeight(context), 0, 0);
        }
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(gone ? View.GONE : View.VISIBLE);
        }
    }
    //</editor-fold>

}
