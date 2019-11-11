package com.mcmo.z.librarydemo.appupdate;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RemoteViews;

import com.mcmo.z.library.appupdate.APPDownLoadUtil;
import com.mcmo.z.librarydemo.R;

public class AppUpdateActivity extends AppCompatActivity {
    private AppUpdateDialog mDialog;
    private int progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);
        Button btn1,btn2;
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn1.setOnClickListener(v -> {
            if(mDialog == null){
                String url = "https://120.27.229.21//download/xz-app-1.1.apk";
//                String url = "https://alissl.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_1848829_web_seo_baidu_homepage.apk";
                mDialog = AppUpdateDialog.createInstance(url);
            }
            mDialog.show(getSupportFragmentManager(),"update");
//            sendno();
        });
        btn2.setOnClickListener(v -> {
            if(progress>100){
                progress=0;
            }
            sendCustomNotify(progress);
            progress++;
        });
    }

    private void sendno(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("这里是标题")//设置通知栏标题
                .setContentText("这里是内容") //设置通知栏显示内容
                .setTicker("这里是Ticker") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//                .setNumber(number) //设置通知集合的数量
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission

        //smallIcon 显示在通知的右下角, largeIcon 显示在左侧
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setLargeIcon(Icon.createWithResource(getApplicationContext(), R.mipmap.ic_launcher));
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
        }

        Notification n = builder.getNotification();
        n.flags = Notification.FLAG_AUTO_CANCEL;
//        Intent intent = APPDownLoadUtil.getInstallIntent(file);
//        if (intent != null) {
//            n.contentIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
        NotificationManagerCompat.from(this).notify(111, n);
    }
    private Notification mNotification;
    private void sendCustomNotify(int progress) {
        if (mNotification == null) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setTicker("下载中...")//通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON
            mNotification = builder.getNotification();
            mNotification.contentView = new RemoteViews(getPackageName(), com.mcmo.z.library.appupdate.R.layout.notifycation_download);
            mNotification.contentView.setImageViewResource(com.mcmo.z.library.appupdate.R.id.iv_notify_icon,R.mipmap.ic_launcher);
            mNotification.contentView.setTextViewText(com.mcmo.z.library.appupdate.R.id.tv_notify_download_title, "下载");
            mNotification.contentView.setProgressBar(com.mcmo.z.library.appupdate.R.id.pb_notify_download, 100, progress, false);
            mNotification.contentView.setTextViewText(com.mcmo.z.library.appupdate.R.id.tv_notify_download_percent, progress + "%");
            mNotification.defaults = Notification.DEFAULT_LIGHTS;
            mNotification.flags = Notification.FLAG_ONGOING_EVENT;
            NotificationManagerCompat.from(this).notify(111, mNotification);
        } else {
            mNotification.contentView.setProgressBar(com.mcmo.z.library.appupdate.R.id.pb_notify_download, 100, progress, false);
            mNotification.contentView.setTextViewText(com.mcmo.z.library.appupdate.R.id.tv_notify_download_percent, progress + "%");
            NotificationManagerCompat.from(this).notify(111, mNotification);
        }
    }
}
