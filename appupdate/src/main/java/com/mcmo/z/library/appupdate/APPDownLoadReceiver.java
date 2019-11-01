package com.mcmo.z.library.appupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 下载状态广播接收器
 * Created by ZhangWei on 2017/6/1.
 */

public abstract class APPDownLoadReceiver extends BroadcastReceiver implements APPDownLoadConstant{
    public void register(Context context){
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(this,intentFilter);
    }
    public void unRegister(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BROADCAST_STATUS,-1);
        switch (status){
            case BROADCAST_STATUS_START:
                onDownLoadStart();
                break;
            case BROADCAST_STATUS_FAILED:
                onDownLoadFailed(intent.getIntExtra(BROADCAST_ERROR,-1));
                break;
            case BROADCAST_STATUS_COMPLETED:
                onDownLoadCompleted(intent.getStringExtra(BROADCAST_APP_FILEPATH));
                break;
            case BROADCAST_STATUS_DOWNLOADING:
                onDownLoading(intent.getIntExtra(BROADCAST_PROGRESS,-1));
                break;
        }
    }
    public abstract void onDownLoadStart();
    public abstract void onDownLoadFailed(int error);
    public abstract void onDownLoadCompleted(String filePath);
    public abstract void onDownLoading(int progress);
}
