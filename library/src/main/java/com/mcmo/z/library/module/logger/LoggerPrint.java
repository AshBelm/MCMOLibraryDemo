package com.mcmo.z.library.module.logger;

import android.util.Log;

/**
 * Created by ZhangWei on 2017/5/22.
 */

public class LoggerPrint {

    public static String getLogString(String msg) {
        StackTraceElement stackTraceElement[] = Thread.currentThread().getStackTrace();
        int index = 0;
        //获取代码所运行的位置
        for (StackTraceElement e : stackTraceElement) {
            String name = e.getClassName();
            if (!name.equals(LoggerPrint.class.getName())) {
                index++;
            } else {
                break;
            }
        }
        //进行方法位置偏移,这个和你的打印方法到系统的打印方法间的方法数有关。
        index += 2;
        //当我能准确获取到I时，本部分已经完结，以下代码都是废话，请不要关注
        String fullClassName = stackTraceElement[index].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        if (className.contains("$"))
            className = className.substring(0, className.lastIndexOf("$"));//处理匿名内部类
        String methodName = stackTraceElement[index].getMethodName();
        String lineNumber = String.valueOf(stackTraceElement[index].getLineNumber());
        //  代码定位格式：
        //（类名：代码行）
        //使用这种格式，打印出的内容就可以点击跳转代码
        return "(" + className + ".java:" + lineNumber + ")   " + msg;
    }

    public static void e(String tag, String msg) {
        Log.e(tag, getLogString(msg));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, getLogString(msg));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, getLogString(msg));
    }

    public static void w(String tag, String msg) {
        Log.w(tag, getLogString(msg));
    }

    public static void v(String tag, String msg) {
        Log.v(tag, getLogString(msg));
    }
}
