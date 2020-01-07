package com.mcmo.z.library.appupdate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;


/**
 * Created by ZhangWei on 2017/6/1.
 */

public class APPDownLoadService extends Service implements DownLoadListener, APPDownLoadConstant {
    private String filePath;
    private AppUpdateParam mUpdateParam;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    public static final int NOTIFICATION_PROGRESS_ID = 158;
    public static final int NOTIFICATION_SUCCESS_ID = 159;
    public static final int NOTIFICATION_FAILED_ID = 160;
    private DownLoadThread mDownLoadThread;//因为在下载失败和成功是都会停止服务所以这里没用线程池之类的东西

    private final String CHANNEL_ID = "channel898";
    private final String DEFAULT_DOWNLOAD_FOLDER = "app";
    private final String DEFAULT_APK_NAME = "NewVersionApp.apk";

    private static final String KEY_PARAM = "appupdateparam";

    private RemoteViews mDownloadingRemoteViews;

    public static Intent getIntent(Context context, AppUpdateParam p) {
        Intent intent = new Intent(context, APPDownLoadService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_PARAM, p);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(false);
        }
    }

    private void parserIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        mUpdateParam = bundle.getParcelable(KEY_PARAM);
        filePath = createDownloadPath(mUpdateParam.saveFolder);
        setDefaultValueIfEmpty();
    }

    private void setDefaultValueIfEmpty() {
        if (mUpdateParam.fileName == null || mUpdateParam.fileName.trim().length() == 0) {
            mUpdateParam.fileName = DEFAULT_APK_NAME;
        }
        if (mUpdateParam.successIcon == -1) {
            mUpdateParam.successIcon = R.drawable.ic_file_download_black_24dp;
        }
        if (TextUtils.isEmpty(mUpdateParam.successTicker)) {
            mUpdateParam.successTicker = getString(R.string.appupdate_default_success_ticker);
        }
        if (TextUtils.isEmpty(mUpdateParam.successTitle)) {
            mUpdateParam.successTitle = getString(R.string.appupdate_default_success_title);
        }
        if (TextUtils.isEmpty(mUpdateParam.successText)) {
            mUpdateParam.successText = getString(R.string.appupdate_default_success_text);
        }
        if (mUpdateParam.downloadIcon == -1) {
            mUpdateParam.downloadIcon = R.drawable.ic_file_download_black_24dp;
        }
        if (TextUtils.isEmpty(mUpdateParam.downloadTicker)) {
            mUpdateParam.downloadTicker = getString(R.string.appupdate_default_downloading_ticker);
        }
        if (TextUtils.isEmpty(mUpdateParam.downloadTitle)) {
            mUpdateParam.downloadTitle = getString(R.string.appupdate_default_downloading_title);
        }
        if (mUpdateParam.failedIcon == -1) {
            mUpdateParam.failedIcon = R.drawable.ic_file_download_black_24dp;
        }
        if (TextUtils.isEmpty(mUpdateParam.failedTicker)) {
            mUpdateParam.failedTicker = getString(R.string.appupdate_default_failed_ticker);
        }
        if (TextUtils.isEmpty(mUpdateParam.failedTitle)) {
            mUpdateParam.failedTitle = getString(R.string.appupdate_default_failed_title);
            ;
        }
        if (TextUtils.isEmpty(mUpdateParam.failedText)) {
            mUpdateParam.failedText = getString(R.string.appupdate_default_failed_text);
            ;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mDownLoadThread == null) {
            parserIntent(intent);
            clearAllNotify();
            startThread(filePath, mUpdateParam.fileName, mUpdateParam.url, mUpdateParam.isDeleteOldApk, mUpdateParam.isTrustAllVerify);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startThread(String filePath, String fileName, String url, boolean deleteOld, boolean trustAllVerify) {
        mDownLoadThread = new DownLoadThread(filePath, fileName);
        mDownLoadThread.setUri(url);
        mDownLoadThread.setDownLoadListener(this);
        mDownLoadThread.setDeleteOldApk(deleteOld);
        mDownLoadThread.setTrustAllVerify(trustAllVerify);
        mDownLoadThread.start();
    }

    private String createDownloadPath(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            folderName = DEFAULT_DOWNLOAD_FOLDER;
        } else {
            if (folderName.startsWith("\\")) {
                folderName = folderName.substring(1);
            }
            if (folderName.endsWith("\\")) {
                folderName = folderName.substring(0, folderName.length() - 1);
            }
        }
        return getExternalCacheDir().getAbsolutePath() + File.separator + folderName;
    }


    private void clearProgressNotify() {
        mNotificationManager.cancel(NOTIFICATION_PROGRESS_ID);
    }

    private void clearSuccessNotify() {
        mNotificationManager.cancel(NOTIFICATION_SUCCESS_ID);
    }

    private void clearFailedNotify() {
        mNotificationManager.cancel(NOTIFICATION_FAILED_ID);
    }

    private void clearAllNotify() {
        clearProgressNotify();
        clearFailedNotify();
        clearSuccessNotify();
    }

    public void sendSuccessNotify(String file) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(mUpdateParam.successTitle)//设置通知栏标题
                .setContentText(mUpdateParam.successText) //设置通知栏显示内容
                .setTicker(mUpdateParam.successTicker) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(mUpdateParam.successIcon);
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                .setNumber(number) //设置通知集合的数量
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission

        Notification n = builder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent intent = APPDownLoadUtil.getInstallIntent(this, file);
        if (intent != null) {
            n.contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mNotificationManager.notify(NOTIFICATION_SUCCESS_ID, n);
    }

    public void sendFailedNotify() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setTicker(mUpdateParam.failedTicker)
                .setContentTitle(mUpdateParam.failedTitle)
                .setContentText(mUpdateParam.failedText)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(mUpdateParam.failedIcon);
        Notification n = builder.build();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        Intent intent = getIntent(this, mUpdateParam);
        n.contentIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager.notify(NOTIFICATION_FAILED_ID, n);
    }

    private RemoteViews getRemoteViews(int icon, String title, int progress) {
        if (mDownloadingRemoteViews == null) {
            mDownloadingRemoteViews = new RemoteViews(getPackageName(), R.layout.notifycation_download);
        }
        mDownloadingRemoteViews.setTextViewText(R.id.tv_notify_download_title, title);
        mDownloadingRemoteViews.setProgressBar(R.id.pb_notify_download, 100, progress, false);
        mDownloadingRemoteViews.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
        mDownloadingRemoteViews.setImageViewResource(R.id.iv_notify_icon, icon);
        return mDownloadingRemoteViews;
    }

    private void sendProgressNotify(int progress) {
        if (mNotification == null) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setTicker(mUpdateParam.downloadTicker)//通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
            builder.setSmallIcon(mUpdateParam.successIcon);
            RemoteViews remoteViews = getRemoteViews(mUpdateParam.downloadIcon, mUpdateParam.downloadTitle, progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setCustomContentView(remoteViews);
            } else {
                builder.setContent(remoteViews);
            }
            mNotification = builder.build();
            mNotification.defaults = Notification.DEFAULT_LIGHTS;
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;//正在运行中的事件
        } else {
            mNotification.contentView.setProgressBar(R.id.pb_notify_download, 100, progress, false);
            mNotification.contentView.setTextViewText(R.id.tv_notify_download_percent, progress + "%");
        }
        mNotificationManager.notify(NOTIFICATION_PROGRESS_ID, mNotification);
    }

    /**
     * 创建一个通知的Channel，如果Channel已经创建成功就不能修改，修改的话就删除原来的channel再重新创建，如：实现动态的开关震动功能(mNotificationManager.deleteNotificationChannel();)
     * @param isVibrate
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(boolean isVibrate) {
        CharSequence name = getString(R.string.appupdate_channel_name);
        String description = getString(R.string.appupdate_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        if (isVibrate) {
            channel.enableVibration(true);
        } else {
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
        }
        mNotificationManager.createNotificationChannel(channel);
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
        curTime = System.currentTimeMillis();
        if ((progress - mCurrentPro >= 1) && (curTime - preTime) > 500) {//限制更新的速度，如果过快机器会卡死
            preTime = curTime;
            mCurrentPro = progress;
            sendProgressBroadcast(progress);
            if (mUpdateParam.useNotification) {
                sendProgressNotify(progress);
            }
        }
    }


    @Override
    public void onDownLoadStart() {
        reset();
        sendStartBroadCast();
        if (mUpdateParam.useNotification) {
            sendProgressNotify(0);
        }
    }


    @Override
    public void onDownLoadFailed(int error) {
        sendFailedBroadcast(error);
        stopSelf();
        if (mUpdateParam.useNotification) {
            clearProgressNotify();
            sendFailedNotify();
        }
    }


    @Override
    public void onDownLoadComplete(String file) {
        //如果在pause中反注册广播接收器，那么如果自动安装当页面跳出时就会反注册，那么就收不到下载完成的事件了，所以广播发送写在前面
        sendCompletedBroadcast(file);
        stopSelf();
        if (mUpdateParam.useNotification) {
            if (mUpdateParam.autoInstall) {
                clearProgressNotify();
            } else {
                clearProgressNotify();
                sendSuccessNotify(file);
            }
        }
        if (mUpdateParam.autoInstall) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //8.0及以上因为安装应用需要权限，所以交由BaseAppUpdateDialog实现
                APPDownLoadUtil.installApk(this, file);
            }
        }
    }

    private void sendProgressBroadcast(int progress) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS, BROADCAST_STATUS_DOWNLOADING);
        intent.putExtra(BROADCAST_PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStartBroadCast() {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS, BROADCAST_STATUS_START);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendFailedBroadcast(int error) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS, BROADCAST_STATUS_FAILED);
        intent.putExtra(BROADCAST_ERROR, error);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendCompletedBroadcast(String file) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_STATUS, BROADCAST_STATUS_COMPLETED);
        intent.putExtra(BROADCAST_APP_FILEPATH, file);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
