package com.mcmo.z.library.appupdate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public abstract class BaseAppUpdateDialog extends DialogFragment {
    private String mApkFilePath;//下载成功后apk路径
    private AppUpdateParam mUpdateParam;//下载需要的参数

    private DownLoadReceiver mReceiver;

    private final String[] needPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int PERMISSION_REQUEST_CODE = 111;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new DownLoadReceiver();
        mReceiver.register(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReceiver.unRegister(context);
    }

    public void downloadOrInstall(AppUpdateParam appUpdateParam) {
        mUpdateParam = appUpdateParam;
        if (TextUtils.isEmpty(mApkFilePath)) {
            startDownload();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                installApkV26(mApkFilePath);
            } else {
                APPDownLoadUtil.installApk(context, mApkFilePath);
            }
        }
    }

    private void startDownload() {
        if (checkPermission(needPermission)) {
            downloadApk();
        } else {
            if (shouldShowRequestPermissionRationale(needPermission[0]) || shouldShowRequestPermissionRationale(needPermission[1])) {
                // TODO: 2019/11/1  小米用户点击了不在询问就再也弹不出了
                showPermissionTipDialog();
            } else {
                requestPermissions(needPermission, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    onPermissionDenied();
                    return;
                }
            }
            downloadApk();
        }
    }

    private AlertDialog mTipDialog;

    private void showPermissionTipDialog() {
        if (mTipDialog == null) {
            mTipDialog = new AlertDialog.Builder(getContext()).setTitle(R.string.appupdate_tip_title).setMessage(R.string.appupdate_tip_msg).setPositiveButton(R.string.appupdate_tip_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(needPermission, PERMISSION_REQUEST_CODE);
                }
            }).setNegativeButton(R.string.appupdate_tip_cancel, null).create();
        }
        mTipDialog.show();
    }

    /**
     * 启动服务下载apk
     */
    private void downloadApk() {
        Intent intent = APPDownLoadService.getIntent(context, mUpdateParam);
        context.startService(intent);
    }

    protected boolean checkPermission(String[] permissiones) {
        for (int i = 0; i < permissiones.length; i++) {
            if (ActivityCompat.checkSelfPermission(context, permissiones[i]) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void startRequestPackageInstallActivity() {
        Uri packageURI = Uri.parse("package:" + context.getApplicationContext().getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                APPDownLoadUtil.installApk(context, mApkFilePath);
            } else {
                Toast.makeText(context, "未打开'安装未知来源'开关,无法安装,请打开后重试", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onPermissionDenied() {
    }

    protected abstract void onDownloadStart();

    protected abstract void onDownloadFailed(int error);

    protected abstract void onDownloadCompleted(String filePath);

    protected abstract void onDownloadProgressChanged(int progress);

    /**
     * 接收下载状态的广播
     */
    private class DownLoadReceiver extends APPDownLoadReceiver {

        @Override
        public void onDownLoadStart() {
            BaseAppUpdateDialog.this.onDownloadStart();
        }

        @Override
        public void onDownLoadFailed(int error) {
            BaseAppUpdateDialog.this.onDownloadFailed(error);
        }

        @Override
        public void onDownLoadCompleted(String filePath) {
            mApkFilePath = filePath;
            BaseAppUpdateDialog.this.onDownloadCompleted(filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                installApkV26(filePath);
            }
        }

        @Override
        public void onDownLoading(int progress) {
            BaseAppUpdateDialog.this.onDownloadProgressChanged(progress);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void installApkV26(String filePath) {
        boolean haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
        if (haveInstallPermission) {
            APPDownLoadUtil.installApk(context, filePath);
        } else {
            startRequestPackageInstallActivity();
        }
    }
}
