package com.mcmo.z.library.module.appupdatedownload;

/**
 * Created by ZhangWei on 2017/6/1.
 */

public interface APPDownLoadConstant {
    public final String BROADCAST_ACTION="app.download.broadcast.action";
    public final String BROADCAST_STATUS="status";
    public final int BROADCAST_STATUS_START=1;
    public final int BROADCAST_STATUS_FAILED=2;
    public final int BROADCAST_STATUS_DOWNLOADING=3;
    public final int BROADCAST_STATUS_COMPLETE=4;
    public final String BROADCAST_PROGRESS="progress";
    public final String BROADCAST_APP_FILEPATH="appFilePath";
    public final String BROADCAST_ERROR="error";
}
