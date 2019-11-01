package com.mcmo.z.library.appupdate;

/**
 * Created by ZhangWei on 2017/6/1.
 */

public interface APPDownLoadConstant {
    String BROADCAST_ACTION = "app.download.broadcast.action";
    String BROADCAST_STATUS = "status";
    int BROADCAST_STATUS_START = 1;
    int BROADCAST_STATUS_FAILED = 2;
    int BROADCAST_STATUS_DOWNLOADING = 3;
    int BROADCAST_STATUS_COMPLETED = 4;
    String BROADCAST_PROGRESS = "progress";
    String BROADCAST_APP_FILEPATH = "appFilePath";
    String BROADCAST_ERROR = "error";
}
