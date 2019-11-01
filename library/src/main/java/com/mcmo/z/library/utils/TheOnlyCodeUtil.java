package com.mcmo.z.library.utils;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TheOnlyCodeUtil {
    private static final String TAG = "TheOnlyCodeUtil";

    /**
     * 获取应用的 ssaid(Android_ID)
     * 这个Android_ID在8.0时有一次行为变更 - - 在8.0系统这个Android_ID根据用户的应用签名和设备等生成（也就是说在8.0如果你改了应用签名这个Android_ID会改变）
     * <p>
     * 特点：
     * 1.应用卸载再安装不会变更（如果是在8.0之前安装后卸载，升级8.0之后再安装会变更，ps:看文档理解是这样的具体没验证）
     * 2.手机系统升级不会变更
     * 3.设备恢复出厂设置时会变更
     * <p>
     * 摘自网络：
     * 设备刷机wipe数据或恢复出厂设置时ANDROID_ID值会被重置。
     * 现在网上已有修改设备ANDROID_ID值的APP应用。
     * 某些厂商定制的系统可能会导致不同的设备产生相同的ANDROID_ID。
     * 某些厂商定制的系统可能导致设备返回ANDROID_ID值为空。
     * CDMA设备，ANDROID_ID和DeviceId返回的值相同
     * 在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
     *
     * @param context
     * @return
     */
    public static String getAndroidID(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 需要 android.permission.ACCESS_WIFI_STATE 权限
     * 这种方法虽然能在当前Wifi状态为关闭的情况下获取到MAC地址，但前提是在手机开机后要打开过一次Wifi，如果在某次开机后没打开过Wifi就调用这段代码，获取地址也是为空
     *
     * @param context
     * @return
     */
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    public static String getMacAddress(Context context) {
        String address = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (null != info) {
            address = info.getMacAddress();
        }
        return address;
    }

    public static String getMacAddressLinux() {
        String macAddress = "";
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            LineNumberReader numReader = new LineNumberReader(isr);
            for (; null != str; ) {
                str = numReader.readLine();
                if (null != str) {
                    macAddress = str.trim();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    public static void printMacAddress() throws SocketException {
        String address = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            byte[] by = netWork.getHardwareAddress();
            if (by == null || by.length == 0) {
                continue;
            }
            StringBuffer sb = new StringBuffer();
            for (byte b : by) {
                sb.append(String.format("%02X:", b));
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            String mac = sb.toString();
            Log.e(TAG, "printMacAddress: " + netWork.getName() + " " + mac);
        }
    }

    public static String getMacAddressV23() throws SocketException {
        String address = "";
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            byte[] by = netWork.getHardwareAddress();
            if (!netWork.getName().equalsIgnoreCase("wlan0") || by == null || by.length == 0) {
                continue;
            }
            StringBuffer sb = new StringBuffer();
            for (byte b : by) {
                sb.append(String.format("%02X:", b));
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            address = sb.toString();
        }
        return address;
    }

    /**
     * 需要 android.permission.READ_PHONE_STATE 权限
     * 这两个是有电话功能的移动设备才具有
     * 且在某些设备上getDeviceId()会返回垃圾数据。
     * @param context
     * @return String[]{IMEI,IMEI2,IMSI}.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    public static String[] getIMEIAndIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = telephonyManager.getSubscriberId();
        String imei = "";
        String imei2 = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = telephonyManager.getImei();
            imei2 = telephonyManager.getImei(1);
            telephonyManager.getMeid();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            imei = telephonyManager.getDeviceId();// 网上说有些设备会返回垃圾值
            imei2 = telephonyManager.getDeviceId(2);// TODO: 2019/10/24 在华为5.1.1上调用正常，也不报错?????
        } else {
            imei = telephonyManager.getDeviceId();
        }
        return new String[]{imei, imei2, imsi};
    }
}
