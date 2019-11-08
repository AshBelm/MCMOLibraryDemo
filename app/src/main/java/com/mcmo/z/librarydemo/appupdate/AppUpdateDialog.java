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

    private Context context;
    private String url;//下载地址
    private View.OnClickListener onAppInstallListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
                p.setEnableNotification(true);
                p.setDownLoadingNotication(R.mipmap.ic_launcher,"下下下","新版来喽");
                p.setSuccessNotiication(R.mipmap.ic_launcher,"完事","快看看","有啥新内容");
                downloadOrInstall(p);
            }
        });
        tvMsg.setText("新版本来了！");
        return v;
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

    private void changeDownloadingStatus(boolean isDownloading) {
        btnUpdate.setVisibility(isDownloading ? View.INVISIBLE : View.VISIBLE);
        pb.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
        tvProgress.setVisibility(isDownloading ? View.VISIBLE : View.INVISIBLE);
    }

}
