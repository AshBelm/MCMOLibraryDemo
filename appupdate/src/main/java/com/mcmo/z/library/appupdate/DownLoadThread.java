package com.mcmo.z.library.appupdate;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZhangWei on 2017/5/10.
 */

public class DownLoadThread extends Thread {
    private static final String TAG = "DownLoadThread";
    private String uri;
    private String filePath;
    private String fileName;
    private String suffx;
    private boolean interceptFlag;
    private boolean deleteOldApk = true;//是否删除旧的安装包
    private DownLoadListener mListener;
    private static final int ERROR_INTERCEPT = 1;
    private static final int ERROR_RENAME_FAILED = 2;
    private static final int ERROR_EXCEPTION = 3;

    public DownLoadThread(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
        if (fileName != null) {
            int index = fileName.lastIndexOf(".apk");
            if (index != -1) {
                this.fileName = fileName.substring(0, index);
                this.suffx = fileName.substring(index);
            }else{
                this.suffx = ".apk";
            }
        }
        Log.d(TAG, "DownLoadThread: name=" + this.fileName + " suffx= " + suffx);
    }

    public void setDeleteOldApk(boolean deleteOldApk) {
        this.deleteOldApk = deleteOldApk;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDownLoadListener(DownLoadListener listener) {
        this.mListener = listener;
    }

    public void intercept() {
        interceptFlag = true;
    }

    @Override
    public void run() {
        super.run();
        String downLoadFile = filePath + File.separator + fileName + suffx;
        String downLoadTmp = filePath + File.separator + fileName + ".tmp";
        File path = new File(filePath);
        if (!path.exists() || !path.isDirectory()) {
            path.mkdirs();
        }
        File apk = new File(downLoadFile);
        if (apk.exists() && apk.isFile()) {
            if(deleteOldApk){
                apk.delete();//删除旧安装包
            }else{
                //重新命名
                String newFileName = getUseAbleFileName(filePath, fileName);
                downLoadFile = filePath + File.separator + newFileName + suffx;
                apk = new File(downLoadFile);
            }
        }
        File tmp = new File(downLoadTmp);
        if (tmp.exists() && tmp.isFile()) {
            tmp.delete();
        }
        Log.d(TAG, "download start");
        if (mListener != null) {
            mListener.onDownLoadStart();
        }
        try {
            FileOutputStream fos = new FileOutputStream(tmp);
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            int count = 0;
            byte buf[] = new byte[1024];
            int numread = 0;
            while ((numread = is.read(buf)) != -1 && !interceptFlag) {
                count += numread;
                fos.write(buf, 0, numread);
                int progress = (int) (((float) count / length) * 100);
                Log.d(TAG, "download progeress " + progress);
                if (mListener != null) {
                    mListener.onProgressChange(progress);
                }
            }
            fos.flush();
            fos.close();
            is.close();
            if (interceptFlag) {
                Log.d(TAG, "download intercept");
                if (mListener != null) {
                    mListener.onDownLoadFailed(ERROR_INTERCEPT);
                }
                return;
            }
            //下载完成 - 将临时下载文件转成APK文件
            if (tmp.renameTo(apk)) {
                Log.d(TAG, "download complete " + apk.getAbsolutePath());
                //通知安装
                if (mListener != null) {
                    mListener.onDownLoadComplete(apk.getAbsolutePath());
                }
            } else {
                Log.d(TAG, "download rename failed");
                if (mListener != null) {
                    mListener.onDownLoadFailed(ERROR_RENAME_FAILED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onDownLoadFailed(ERROR_EXCEPTION);
            }
        }

    }

    private String getUseAbleFileName(String path, String name) {
        String fileName = name;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            File file = new File(path, fileName + suffx);
            if (file.exists() && file.isFile()) {
                fileName = name + "-" + i;
            } else {
                return fileName;
            }
        }
        return null;
    }
}
