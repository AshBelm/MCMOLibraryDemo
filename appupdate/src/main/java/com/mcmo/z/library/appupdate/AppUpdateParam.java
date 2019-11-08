package com.mcmo.z.library.appupdate;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * apk下载需要的参数
 */
public class AppUpdateParam implements Parcelable {
    /**
     * 是否自动安装
     */
    protected boolean autoInstall = true;
    /**
     * 保存apk文件夹的名称
     */
    protected String saveFolder;
    /**
     * 保存的apk名称
     */
    protected String fileName;
    /**
     * 下载地址
     */
    protected String url;

    protected boolean useNotification = true;
    protected int successIcon = -1;
    protected String successTicker;
    protected String successTitle;
    protected String successText;
    protected int downloadIcon = -1;
    protected String downloadTicker;
    protected String downloadTitle;
    protected int failedIcon = -1;
    protected String failedTicker;
    protected String failedTitle;
    protected String failedText;
    protected boolean isDeleteOldApk = true;

    public AppUpdateParam(String url) {
        this.url = url;
    }

    protected AppUpdateParam(Parcel in) {
        autoInstall = in.readByte() != 0;
        saveFolder = in.readString();
        fileName = in.readString();
        url = in.readString();
        useNotification = in.readByte() != 0;
        successIcon = in.readInt();
        successTicker = in.readString();
        successTitle = in.readString();
        successText = in.readString();
        downloadIcon = in.readInt();
        downloadTicker = in.readString();
        downloadTitle = in.readString();
        failedIcon = in.readInt();
        failedTicker = in.readString();
        failedTitle = in.readString();
        failedText = in.readString();
        isDeleteOldApk = in.readByte() != 0;
    }

    public static final Creator<AppUpdateParam> CREATOR = new Creator<AppUpdateParam>() {
        @Override
        public AppUpdateParam createFromParcel(Parcel in) {
            return new AppUpdateParam(in);
        }

        @Override
        public AppUpdateParam[] newArray(int size) {
            return new AppUpdateParam[size];
        }
    };

    public void setAutoInstall(boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public void setEnableNotification(boolean enable) {
        this.useNotification = enable;
    }

    public void setSaveFolder(String saveFolder) {
        this.saveFolder = saveFolder;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDeleteOldApk() {
        return isDeleteOldApk;
    }

    public void setDeleteOldApk(boolean deleteOldApk) {
        isDeleteOldApk = deleteOldApk;
    }

    /**
     * 设置下载成功是通知的文案
     *
     * @param icon    图标
     * @param ticker  上升显示时的文字
     * @param title
     * @param content
     */
    public void setSuccessNotiication(int icon, String ticker, String title, String content) {
        this.successIcon = icon;
        this.successTicker = ticker;
        this.successTitle = title;
        this.successText = content;
    }

    /**
     * 设置下载时通知的文案
     *
     * @param icon   图标
     * @param ticker 上升显示的文字，开始下载时显示一次
     * @param title  下载时通知显示的标题
     */
    public void setDownLoadingNotication(int icon, String ticker, String title) {
        this.downloadIcon = icon;
        this.downloadTicker = ticker;
        this.downloadTitle = title;
    }

    public void setFialedNotication(int icon, String ticker, String title, String content) {
        this.failedIcon = icon;
        this.failedTicker = ticker;
        this.failedTitle = title;
        this.failedText = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (autoInstall ? 1 : 0));
        dest.writeString(saveFolder);
        dest.writeString(fileName);
        dest.writeString(url);
        dest.writeByte((byte) (useNotification ? 1 : 0));
        dest.writeInt(successIcon);
        dest.writeString(successTicker);
        dest.writeString(successTitle);
        dest.writeString(successText);
        dest.writeInt(downloadIcon);
        dest.writeString(downloadTicker);
        dest.writeString(downloadTitle);
        dest.writeInt(failedIcon);
        dest.writeString(failedTicker);
        dest.writeString(failedTitle);
        dest.writeString(failedText);
        dest.writeByte((byte) (isDeleteOldApk ? 1 : 0));
    }
}
