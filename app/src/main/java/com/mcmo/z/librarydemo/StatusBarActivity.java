package com.mcmo.z.librarydemo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.mcmo.z.library.sys.StatusBarUtil;

public class StatusBarActivity extends AppCompatActivity {
    private static final String TAG = "StatusBarActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_bar);
        //半透明
        StatusBarUtil.translucent(this);
        //透明
//        StatusBarUtil.transparent(this);
        //改变颜色
//        StatusBarUtil.setColor(this,Color.MAGENTA);

//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Log.e(TAG, "onCreate: " + StatusBarUtil.getStatusBarUtil(this));
    }
    private boolean a = true;
    public void onButton1Click(View view) {
        if(a){
            StatusBarUtil.lightMode(this);
        }else {
            StatusBarUtil.darkMode(this);
        }
        a = !a;
    }
}
