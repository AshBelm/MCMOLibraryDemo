package com.mcmo.z.library.appupdate;

/**
 * Created by ZhangWei on 2017/5/10.
 */

public interface DownLoadListener {
    void onProgressChange(int progress);
    void onDownLoadStart();
    void onDownLoadFailed(int error);
    void onDownLoadComplete(String file);
}
