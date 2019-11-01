package com.mcmo.z.librarydemo.appupdate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.mcmo.z.library.appupdate.APPDownLoadReceiver;
import com.mcmo.z.library.appupdate.APPDownLoadService;
import com.mcmo.z.library.appupdate.APPDownLoadUtil;
import com.mcmo.z.library.appupdate.AppUpdateParam;
import com.mcmo.z.library.appupdate.BaseAppUpdateDialog;
import com.mcmo.z.librarydemo.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class AppUpdateDialog extends BaseAppUpdateDialog {
    private TextView tvMsg;
    private TextView tvProgress;
    private Button btnUpdate;
    private ProgressBar pb;
    public static final String DOWNLOAD_PATH = "app" + File.separator + "xz";
//    private DownLoadReceiver mReceiver;

    private Context context;
    private String appFilePath;
    private String cacheDir;
    private String url;//下载地址
    private View.OnClickListener onAppInstallListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        cacheDir = context.getExternalCacheDir().getAbsolutePath();
    }
    public static AppUpdateDialog createInstance(String url){
        AppUpdateDialog dialog = new AppUpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putString("downloadSite",url);
        dialog.setArguments(bundle);
        return dialog;
    }
    private void parseArguments(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            url = bundle.getString("downloadSite");
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArguments();
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MiddleDialogStyle);
//        mReceiver = new DownLoadReceiver();
//        if (mReceiver != null) {
//            mReceiver.register(context);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_app_update, container, false);
        tvMsg = v.findViewById(R.id.tv_update_msg);
        tvProgress = v.findViewById(R.id.tv_update_progress);
        btnUpdate = v.findViewById(R.id.btn_update);
        pb = v.findViewById(R.id.pb_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdateParam p = new AppUpdateParam(url);
                p.setAutoInstall(true);
                p.setSaveFolder("app");
                p.setFileName("newApp.apk");
                p.disableNotification();
//                p.enableNotification(R.mipmap.ic_launcher,"cehngg","asfa","adfa",R.mipmap.ic_launcher,"fa","afa9","fasdfa");

                downloadOrInstall(p);
//                if (!TextUtils.isEmpty(appFilePath)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
//                        if (!haveInstallPermission) {
//                            startRequestPackageInstallActivity();
//                        } else {
//                            APPDownLoadUtil.installApk(context, appFilePath);
//                        }
//                    } else {
//                        APPDownLoadUtil.installApk(context, appFilePath);
//                    }
//                } else {
//                    downApp();
//                    changeDownloadingStatus(true);
//                }
            }
        });
        tvMsg.setText("新版本来了！");
        return v;
    }

    private void startRequestPackageInstallActivity() {
        Uri packageURI = Uri.parse("package:" + context.getApplicationContext().getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10001);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mReceiver != null) {
//            mReceiver.unRegister(context);
//        }
    }

    @Override
    protected void onDownloadStart() {
        changeDownloadingStatus(true);
    }

    @Override
    protected void onDownloadFailed(int error) {
        Toast.makeText(context, "下载失败请重试", Toast.LENGTH_SHORT).show();
        changeDownloadingStatus(false);

    }

    @Override
    protected void onDownloadCompleted(String filePath) {
        changeDownloadingStatus(false);
        btnUpdate.setText("安装");

    }

    @Override
    protected void onDownloadProgressChanged(int progress) {
        pb.setProgress(progress);
        tvProgress.setText(progress + "%");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if (resultCode == RESULT_OK) {
                APPDownLoadUtil.installApk(context, appFilePath);
            } else {
                Toast.makeText(context, "未打开'安装未知来源'开关,无法安装,请打开后重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void changeDownloadingStatus(boolean isDownloading) {
        btnUpdate.setVisibility(isDownloading ? View.INVISIBLE : View.VISIBLE);
        pb.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
        tvProgress.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
    }

    public void setOnAppInstallListener(View.OnClickListener onAppInstallListener) {
        this.onAppInstallListener = onAppInstallListener;
    }

    private void downApp() {
        AppUpdateParam p = new AppUpdateParam(url);
        p.setSaveFolder("app");
        p.setFileName("newApp.apk");
        p.disableNotification();
        Intent intent = APPDownLoadService.getIntent(getContext(),p);
        context.startService(intent);
    }

    private class DownLoadReceiver extends APPDownLoadReceiver {
        private static final String TAG = "DownLoadReceiver";

        @Override
        public void onDownLoadStart() {
            Log.e(TAG, "onDownLoadStart");
        }

        @Override
        public void onDownLoadFailed(int error) {
            Toast.makeText(context, "下载失败请重试", Toast.LENGTH_SHORT).show();
            changeDownloadingStatus(false);
        }

        @Override
        public void onDownLoadCompleted(String filePath) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!haveInstallPermission) {
                    startRequestPackageInstallActivity();
                }
            }
            appFilePath = filePath;
            changeDownloadingStatus(false);
            btnUpdate.setText("安装");
        }

        @Override
        public void onDownLoading(int progress) {
            pb.setProgress(progress);
            tvProgress.setText(progress + "%");
        }
    }
}
