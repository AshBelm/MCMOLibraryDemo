package com.mcmo.z.librarydemo;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcmo.z.library.appupdate.APPDownLoadReceiver;
import com.mcmo.z.library.appupdate.APPDownLoadService;
import com.mcmo.z.librarydemo.appupdate.AppUpdateActivity;
import com.mcmo.z.librarydemo.mulitgesture.MultiGestureActivity;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private ArrayList<Item> items;
    private DownLoadReceiver mReceiver;
    String[] per = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        items=new ArrayList<>();
        items.add(new Item("bottomDialog",BottomActivity.class));
        items.add(new Item("bestfragment",BestFragmentActivity.class));
        items.add(new Item("BlueToothScan",BuleToothScanActivity.class));
        items.add(new Item("NestedScrollDemo",NestedScrollActivity.class));
        items.add(new Item("ColorPick",ColorPickActivity.class));
        items.add(new Item("MultiGesture", MultiGestureActivity.class));
        items.add(new Item("StatusBar", StatusBarActivity.class));
        items.add(new Item("RecycleViewItemClick", RecycleViewItemClickActivity.class));
        items.add(new Item("TheOnlyCode", TheOnlyCodeActivity.class));
        items.add(new Item("APP Update", AppUpdateActivity.class));
        setListAdapter(new MyAdapter());

 String uri = "https://alissl.ucdl.pp.uc.cn/fs01/union_pack/Wandoujia_1848829_web_seo_baidu_homepage.apk";
//        String imaguri="http://img0.imgtn.bdimg.com/it/u=1017725297,3845479697&fm=11&gp=0.jpg";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"mcmo";
//        new APPDownLoader.Builder().setUri(uri).setFilePath(path).setFileName("myapp.apk")
//                .setSuccessText("myapp-v2.5.1").setSuccessTicker("下载完成").setSuccessTitle("下载完成，点击安装").setSuccessIcon(R.mipmap.ic_launcher)
//                .setDownLoadTicker("myapp开始下载").setDownLoadTitle("MyApp-v2.5.1").setDownLoadIcon(R.mipmap.ic_launcher).create(this).start();
//        DownLoadThread t=new DownLoadThread(path,"aa.jpg");
//        t.setUri(imaguri);
//        t.start();
//        if(checkPermissions()){
//            Intent intent= APPDownLoadService.getIntent(this,path,"myapp.apk",uri,R.mipmap.ic_launcher,"appv2.5","DownLoad Success","DownLoadComplete click to install",R.mipmap.ic_launcher,"start download","appv2.5downloading",true,true);
//            startService(intent);
//        }else{
//            ActivityCompat.requestPermissions(this,per,101);
//        }
        mReceiver =new DownLoadReceiver();
    }

    private boolean checkPermissions(){
        for (int i = 0; i < per.length; i++) {
            if(ActivityCompat.checkSelfPermission(this,per[i])== PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReceiver.unRegister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView=new TextView(MainActivity.this);
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 140));
            }
            TextView tv= (TextView) convertView;
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setText(items.get(position).text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent=new Intent(MainActivity.this,items.get(position).clazz);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    private class Item{
        private String text;
        private Class clazz;

        public Item(String text, Class clazz) {
            this.text = text;
            this.clazz = clazz;
        }
    }
    private class DownLoadReceiver extends APPDownLoadReceiver {
        private static final String TAG = "DownLoadReceiver";
        @Override
        public void onDownLoadStart() {
            Log.e(TAG, "onDownLoadStart");
        }

        @Override
        public void onDownLoadFailed(int error) {
            Log.e(TAG, "onDownLoadFailed");
        }

        @Override
        public void onDownLoadCompleted(String filePath) {
            Log.e(TAG, "onDownLoadCompleted "+ filePath);
        }

        @Override
        public void onDownLoading(int progress) {
            Log.e(TAG, "onDownLoading "+progress);
        }
    }
}
