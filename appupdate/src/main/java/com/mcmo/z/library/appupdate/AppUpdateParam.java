package com.mcmo.z.library.appupdate;

/**
 * apk下载需要的参数
 */
public class AppUpdateParam {
    /**
     * 是否自动安装
     */
    protected boolean autoInstall;
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

    protected boolean useNotification;
    protected int successIcon;
    protected String successTicker;
    protected String successTitle;
    protected String successText;
    protected int downloadIcon;
    protected String downloadTicker;
    protected String downloadTitle;
    protected String downloadText;

    public AppUpdateParam(String url) {
        this.url = url;
    }

    public void setAutoInstall(boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public void disableNotification() {
        this.useNotification = false;
    }

    public void setSaveFolder(String saveFolder) {
        this.saveFolder = saveFolder;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void enableNotification(int successIcon, String successTicker, String successTitle, String successText, int downloadIcon, String downloadTicker, String downloadTitle, String downloadText) {
        this.useNotification = true;
        this.successIcon = successIcon;
        this.successTicker = successTicker;
        this.successTitle = successTitle;
        this.successText = successText;
        this.downloadIcon = downloadIcon;
        this.downloadTicker = downloadTicker;
        this.downloadTitle = downloadTitle;
        this.downloadText = downloadText;
    }
}
