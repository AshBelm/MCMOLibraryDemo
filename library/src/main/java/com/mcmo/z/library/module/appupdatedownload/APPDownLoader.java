package com.mcmo.z.library.module.appupdatedownload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.RemoteViews;


import com.mcmo.z.library.R;

import java.lang.ref.WeakReference;

/**
 * Created by weizhang210142 on 2016/3/8.
 */
public class APPDownLoader implements DownLoadListener {
    private WeakReference<Context> mContext;
    private DownLoadThread mDownLoadThread;
    private Handler handler;
    private NotificationManager mNotificationManager;
    private Notification mCustomNotification;
    public static final int NOTIFICATION_ID = 124;
    private Builder mBuilder;

    public APPDownLoader(Context context, Builder builder) {
        this.mContext = new WeakReference<Context>(context.getApplicationContext());
        handler = new ProcessHandler();
        mBuilder = builder;
    }

    public void start() {
        mDownLoadThread = new DownLoadThread(mBuilder.filePath,mBuilder.fileName);
        mDownLoadThread.setUri(mBuilder.uri);
        mDownLoadThread.setDownLoadListener(this);
        mDownLoadThread.start();
    }
    public void clearNotify(){
        Context context = mContext.get();
        if(context==null){
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }
    public void sendSuccessNotify(String file) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(mBuilder.successTitle)//设置通知栏标题
                .setContentText(mBuilder.successText) //设置通知栏显示内容
                .setTicker(mBuilder.successTicker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                .setNumber(number) //设置通知集合的数量
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(mBuilder.successIcon);//设置通知小ICON
        Notification n = builder.getNotification();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent intent = APPDownLoadUtil.getInstallIntent(file);
        if (intent != null) {
            n.contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        nm.notify(NOTIFICATION_ID, n);
    }

    private void sendCustomNotify(int progress) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (mCustomNotification == null) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setTicker(mBuilder.downLoadTicker)//通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(mBuilder.downLoadIcon);//设置通知小ICON
            mCustomNotification = builder.getNotification();
            mCustomNotification.contentView = new RemoteViews(context.getPackageName(), R.layout.notifycation_download);
            mCustomNotification.contentView.setTextViewText(R.id.tv_notify_download_title, mBuilder.downLoadTitle);
            mCustomNotification.contentView.setProgressBar(R.id.pb_notify_download, 100, progress, false);
            mCustomNotification.contentView.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
            mCustomNotification.defaults = Notification.DEFAULT_LIGHTS;
            mCustomNotification.flags = Notification.FLAG_ONGOING_EVENT;
            mNotificationManager.notify(NOTIFICATION_ID, mCustomNotification);
        } else {
            mCustomNotification.contentView.setProgressBar(R.id.pb_notify_download, 100, progress, false);
            mCustomNotification.contentView.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
            mNotificationManager.notify(NOTIFICATION_ID, mCustomNotification);
        }
    }

    public static final int WHAT_SDCARD_NO_FOUND = 1;
    public static final int WHAT_FILE_EXIST = 2;
    public static final int WHAT_PROCESS = 3;
    public static final int WHAT_DOWNLOAD_SUCCESS = 4;

    @Override
    public void onProgressChange(int progress) {
        Message msg = Message.obtain();
        msg.what = WHAT_PROCESS;
        msg.arg1 = progress;
        handler.sendMessage(msg);
    }

    @Override
    public void onDownLoadStart() {
        Message msg = Message.obtain();
        msg.what = WHAT_PROCESS;
        msg.arg1 = 0;
        handler.sendMessage(msg);
    }

    @Override
    public void onDownLoadFailed(int error) {
        clearNotify();
    }

    @Override
    public void onDownLoadComplete(String file) {
        Message msg = Message.obtain();
        msg.what = WHAT_DOWNLOAD_SUCCESS;
        Bundle data = new Bundle();
        data.putString("file", file);
        msg.setData(data);
        handler.sendMessage(msg);
    }


    private class ProcessHandler extends Handler {
        long preTime = 0;
        long curTime = 0;
        int mCurrentPro = -2;

        private void reset() {
            preTime = 0;
            curTime = 0;
            mCurrentPro = -2;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SDCARD_NO_FOUND:
                    break;
                case WHAT_FILE_EXIST:
                    break;
                case WHAT_DOWNLOAD_SUCCESS:
                    String file = msg.getData().getString("file");
                    if (mBuilder.autoInstall) {
                        Context context = mContext.get();
                        if (context != null && file != null)
                            APPDownLoadUtil.installApk(context, file);
                        clearNotify();
                    } else {
                        sendSuccessNotify(file);
                    }
                    reset();
                    break;
                case WHAT_PROCESS:
                    int progress = msg.arg1;
                    curTime = System.currentTimeMillis();
                    if ((progress - mCurrentPro >= 1) && (curTime - preTime) > 500) {//限制更新的速度，如果过快机器会卡死
                        preTime = curTime;
                        mCurrentPro = progress;
                        sendCustomNotify(progress);
                    }
                    break;
            }
        }
    }

    public static class Builder {
        private String downLoadTicker="";
        private String downLoadTitle="";
        private int downLoadIcon;
        private String successTicker="";
        private String successTitle="";
        private String successText="";
        private int successIcon;
        private String uri;
        private String filePath;
        private String fileName;
        private boolean autoInstall = false;
        public Builder() {
        }

        public Builder setDownLoadTicker(String downLoadTicker) {
            this.downLoadTicker = downLoadTicker;
            return this;
        }

        public Builder setDownLoadTitle(String downLoadTitle) {
            this.downLoadTitle = downLoadTitle;
            return this;
        }

        public Builder setDownLoadIcon(int downLoadIcon) {
            this.downLoadIcon = downLoadIcon;
            return this;
        }

        public Builder setSuccessTicker(String successTicker) {
            this.successTicker = successTicker;
            return this;
        }

        public Builder setSuccessTitle(String successTitle) {
            this.successTitle = successTitle;
            return this;
        }

        public Builder setSuccessIcon(int successIcon) {
            this.successIcon = successIcon;
            return this;
        }

        public Builder setSuccessText(String successText) {
            this.successText = successText;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setAutoInstall(boolean autoInstall) {
            this.autoInstall = autoInstall;
            return this;
        }

        public APPDownLoader create(Context context){
            final APPDownLoader loader = new APPDownLoader(context,this);
            if(successIcon==0){
                throw new IllegalArgumentException("no success icon");
            }
            if(downLoadIcon==0){
                throw new IllegalArgumentException("no downLoad icon");
            }
            if(uri==null||uri.length()==0){
                throw new IllegalArgumentException("download uri is empty");
            }
            if(fileName==null ||fileName.length()==0||filePath==null||filePath.length()==0){
                throw new IllegalArgumentException("fileName or filePath is empty");
            }
            return loader;
        }
    }

}
