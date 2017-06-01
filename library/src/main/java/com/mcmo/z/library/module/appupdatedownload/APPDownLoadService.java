package com.mcmo.z.library.module.appupdatedownload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.RemoteViews;

import com.mcmo.z.library.R;

/**
 * Created by ZhangWei on 2017/6/1.
 */

public class APPDownLoadService extends Service implements DownLoadListener,APPDownLoadConstant{
    private String filePath, fileName;
    private String url;
    private String successText, successTicker, successTitle, downloadTicker, downloadTitle;
    private int successIcon, downloadIcon;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    public static final int NOTIFICATION_ID = 158;
    private DownLoadThread mDownLoadThread;
    private boolean useNotification;
    private boolean autoInstall;

    private static final String KEY_FILEPATH = "filePath";
    private static final String KEY_FILENAME = "fileName";
    private static final String KEY_URL = "url";
    private static final String KEY_S_TITLE = "successTitle";
    private static final String KEY_S_TICKER = "successTicker";
    private static final String KEY_S_TEXT = "successText";
    private static final String KEY_S_ICON = "successIcon";
    private static final String KEY_D_TITLE = "downloadTitle";
    private static final String KEY_D_TICKER = "downloadTicker";
    private static final String KEY_D_ICON = "downloadIcon";
    private static final String KEY_NOTIFICATION = "useNotification";
    private static final String KEY_AUTOINSTALL = "autoInstall";

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static Intent getIntent(Context context, @NonNull String filePath, @NonNull String fileName, @NonNull String url, int successIcon, String successText, String successTicker, String successTitle, int downloadIcon, String downloadTicker, String downloadTitle, boolean useNotification,boolean autoInstall) {
        if (isEmpty(successTitle))
            successTitle = "下载完成，点击安装";
        if (isEmpty(successText))
            successText = fileName;
        if (isEmpty(successTicker))
            successTicker = "下载完成";
        if (isEmpty(downloadTicker))
            downloadTicker = "开始下载";
        if (isEmpty(downloadTitle))
            downloadTitle = fileName;

        Intent intent = new Intent(context, APPDownLoadService.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FILEPATH, filePath);
        bundle.putString(KEY_FILENAME, fileName);
        bundle.putString(KEY_URL, url);
        bundle.putInt(KEY_S_ICON, successIcon);
        bundle.putString(KEY_S_TITLE, successTitle);
        bundle.putString(KEY_S_TICKER, successTicker);
        bundle.putString(KEY_S_TEXT, successText);
        bundle.putInt(KEY_D_ICON, downloadIcon);
        bundle.putString(KEY_D_TITLE, downloadTitle);
        bundle.putString(KEY_D_TICKER, downloadTicker);
        bundle.putBoolean(KEY_NOTIFICATION, useNotification);
        bundle.putBoolean(KEY_AUTOINSTALL,autoInstall);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mDownLoadThread==null){
            parserIntent(intent);
            startThread(filePath,fileName,url);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public void startThread(String filePath,String fileName,String url) {
        mDownLoadThread = new DownLoadThread(filePath,fileName);
        mDownLoadThread.setUri(url);
        mDownLoadThread.setDownLoadListener(this);
        mDownLoadThread.start();
    }

    private void parserIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        filePath = bundle.getString(KEY_FILEPATH);
        fileName = bundle.getString(KEY_FILENAME);
        url = bundle.getString(KEY_URL);
        useNotification = bundle.getBoolean(KEY_NOTIFICATION, true);
        successIcon = bundle.getInt(KEY_S_ICON, -1);
        successText = bundle.getString(KEY_S_TEXT);
        successTitle = bundle.getString(KEY_S_TITLE);
        successTicker = bundle.getString(KEY_S_TICKER);
        downloadIcon = bundle.getInt(KEY_D_ICON, -1);
        downloadTitle = bundle.getString(KEY_D_TITLE);
        downloadTicker = bundle.getString(KEY_D_TICKER);
        autoInstall = bundle.getBoolean(KEY_AUTOINSTALL,false);
    }
    public void clearNotify(){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }
    public void sendSuccessNotify(String file) {

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(successTitle)//设置通知栏标题
                .setContentText(successText) //设置通知栏显示内容
                .setTicker(successTicker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                .setNumber(number) //设置通知集合的数量
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(successIcon);//设置通知小ICON
        Notification n = builder.getNotification();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent intent = APPDownLoadUtil.getInstallIntent(file);
        if (intent != null) {
            n.contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mNotificationManager.notify(NOTIFICATION_ID, n);
    }

    private void sendCustomNotify(int progress) {
        if (mNotification == null) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setTicker(downloadTicker)//通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(downloadIcon);//设置通知小ICON
            mNotification = builder.getNotification();
            mNotification.contentView = new RemoteViews(getPackageName(), R.layout.notifycation_download);
            mNotification.contentView.setTextViewText(R.id.tv_notify_download_title, downloadTitle);
            mNotification.contentView.setProgressBar(R.id.pb_notify_download, 100, progress, false);
            mNotification.contentView.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
            mNotification.defaults = Notification.DEFAULT_LIGHTS;
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        } else {
            mNotification.contentView.setProgressBar(R.id.pb_notify_download, 100, progress, false);
            mNotification.contentView.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    long preTime = 0;
    long curTime = 0;
    int mCurrentPro = -2;

    private void reset() {
        preTime = 0;
        curTime = 0;
        mCurrentPro = -2;
    }
    @Override
    public void onProgressChange(int progress) {
        if(useNotification){
            curTime = System.currentTimeMillis();
            if ((progress - mCurrentPro >= 1) && (curTime - preTime) > 500) {//限制更新的速度，如果过快机器会卡死
                preTime = curTime;
                mCurrentPro = progress;
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(BROADCAST_STATUS,BROADCAST_STATUS_DOWNLOADING);
                intent.putExtra(BROADCAST_PROGRESS,progress);
                sendBroadcast(intent);
                sendCustomNotify(progress);
            }
        }
    }

    @Override
    public void onDownLoadStart() {
        if(useNotification){
            sendCustomNotify(0);
        }
        reset();
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS,BROADCAST_STATUS_START);
        sendBroadcast(intent);
    }

    @Override
    public void onDownLoadFailed(int error) {
        if(useNotification){
            clearNotify();
        }
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS,BROADCAST_STATUS_FAILED);
        intent.putExtra(BROADCAST_ERROR,error);
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDownLoadComplete(String file) {
        if(useNotification){
            if(autoInstall){
                APPDownLoadUtil.installApk(this,file);
                clearNotify();
            }else{
                sendSuccessNotify(file);
            }
        }
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS,BROADCAST_STATUS_COMPLETE);
        intent.putExtra(BROADCAST_APP_FILEPATH,file);
        sendBroadcast(intent);
        stopSelf();
    }
}
