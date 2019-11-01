package com.mcmo.z.librarydemo.appupdate;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AppUpdateActivity extends AppCompatActivity {
    private AppUpdateDialog mDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button btn = new Button(this);
        btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(btn);
        btn.setOnClickListener(v -> {
            if(mDialog == null){
                String url = "https://alissl.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_1848829_web_seo_baidu_homepage.apk";
                mDialog = AppUpdateDialog.createInstance(url);
            }
            mDialog.show(getSupportFragmentManager(),"update");
        });
    }

}
