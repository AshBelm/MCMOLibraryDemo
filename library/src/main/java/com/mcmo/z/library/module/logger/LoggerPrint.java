package com.mcmo.z.library.module.logger;

/**
 * Created by ZhangWei on 2017/5/22.
 */

public class LoggerPrint {
    /**
     * 代码定位
     *
     * @param type
     */
    private static String printLocation(char type, String... msg) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        int i = 0;
        //获取代码所运行的位置
        for (StackTraceElement e : stack) {
            String name = e.getClassName();
            if (!name.equals(LoggerPrint.class.getName())) {
                i++;
            } else {
                break;
            }
        }
        //进行方法位置偏移,这个和你的打印方法到系统的打印方法间的方法数有关。
        i += 3;
        //当我能准确获取到I时，本部分已经完结，以下代码都是废话，请不要关注
        String className = stack[i].getFileName();
        String methodName = stack[i].getMethodName();
        int lineNumber = stack[i].getLineNumber();
        StringBuilder sb = new StringBuilder();
        sb.append("   (").append(className).append(":").append(lineNumber).append(")# ").append(methodName);
        //  代码定位格式：
        //（类名：代码行）
        //使用这种格式，打印出的内容就可以点击跳转代码
        return sb.toString();
    }

}
