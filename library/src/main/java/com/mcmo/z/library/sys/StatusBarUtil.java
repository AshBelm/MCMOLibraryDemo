package com.mcmo.z.library.sys;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class StatusBarUtil {
    /**
     * 设置为半透明
     *
     * @param activity
     */
    public static void translucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    /**
     * 设置为透明
     *
     * @param activity
     */
    public static void transparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transparent_21(activity);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void transparent_21(Activity activity) {
        //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 使布局可用延申到StatusBar下面，可配合xml布局的 fitsSystemWindows 使用
        //SYSTEM_UI_FLAG_LAYOUT_STABLE 在系统UI出现或隐藏时，不改变应用布局
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    //4.0未测试，在5.0上会被体统的statusbar挡住
    public static void transparent_20(Activity activity){
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View statusBarView = new View(activity.getApplication());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getStatusBarUtil(activity));
        statusBarView.setBackgroundColor(Color.TRANSPARENT);
        decorView.addView(statusBarView,lp);
    }
    public static void setColor(@NonNull Activity activity, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //如果要设置statusBar颜色必须保障bar有背景，并且不是半透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加一个view到statusbar位置改变颜色，由于没有机器没法测试所以先空着。
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    private static void setDarkMode(Activity activity, boolean dark){
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        if(dark){
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }else{
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }
    public static void darkMode(@NonNull Activity activity){
        if(isFlyme4()){
            setModeForFlyme4(activity.getWindow(),true);
        }else if(isMIUI6()){
            setModeForMIUI6(activity.getWindow(),true);
        }else{
            setDarkMode(activity,true);
        }
    }
    public static void lightMode(@NonNull Activity activity){
        if(isFlyme4()){
            setModeForFlyme4(activity.getWindow(),false);
        }else if(isMIUI6()){
            setModeForMIUI6(activity.getWindow(),false);
        }else{
            setDarkMode(activity,false);
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
        Class<? extends Window> clazz = window.getClass();
        try {
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
     * 设置Flyme4+的状态栏的darkMode,darkMode时候字体颜色及icon
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
    public static int getStatusBarUtil(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
