package com.mcmo.z.library.appupdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * Created by weizhang210142 on 2016/3/8.
 */
public class APPDownLoadUtil {
    public static final String MIME_APK = "application/vnd.android.package-archive";
    private static final String AUTHORITY = "com.mcmo.z.library.appupdate.fileprovider";

    /**
     * 获取当前客户端版本信息
     */
    public static PackageInfo getAppVersionInfo(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
            return -1;
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * 安装apk
     *
     * @param path
     */
    public static void installApk(Context context, String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), AUTHORITY, apkFile);
        } else {
            uri = Uri.parse("file://" + path);
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, MIME_APK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        context.startActivity(i);
    }

    public static Intent getInstallIntent(String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return null;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + path), MIME_APK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }
}
