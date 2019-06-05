package com.mcmo.z.librarydemo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcmo.z.library.module.appupdatedownload.APPDownLoadReceiver;
import com.mcmo.z.library.module.appupdatedownload.APPDownLoadService;
import com.mcmo.z.librarydemo.mulitgesture.MultiGestureActivity;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private ArrayList<Item> items;
    private DownLoadReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        items=new ArrayList<>();
        items.add(new Item("bottomDialog",BottomActivity.class));
        items.add(new Item("BlueToothScan",BuleToothScanActivity.class));
        items.add(new Item("NestedScrollDemo",NestedScrollActivity.class));
        items.add(new Item("ColorPick",ColorPickActivity.class));
        items.add(new Item("MultiGesture", MultiGestureActivity.class));
        items.add(new Item("StatusBar", StatusBarActivity.class));
        setListAdapter(new MyAdapter());

 String uri = "http://a.wdjcdn.com/release/files/phoenix/5.52.20.13520/wandoujia-wandoujia-web_inner_referral_binded_5.52.20.13520.apk?remove=2&append=%8E%00eyJhcHBEb3dubG9hZCI6eyJkb3dubG9hZFR5cGUiOiJkb3dubG9hZF9ieV9wYWNrYWdlX25hbWUiLCJwYWNrYWdlTmFtZSI6ImNvbS5zb2h1LnNvaHV2aWRlbyJ9fQWdj01B00007e0647";
//        String imaguri="http://img0.imgtn.bdimg.com/it/u=1017725297,3845479697&fm=11&gp=0.jpg";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"mcmo";
//        new APPDownLoader.Builder().setUri(uri).setFilePath(path).setFileName("myapp.apk")
//                .setSuccessText("myapp-v2.5.1").setSuccessTicker("下载完成").setSuccessTitle("下载完成，点击安装").setSuccessIcon(R.mipmap.ic_launcher)
//                .setDownLoadTicker("myapp开始下载").setDownLoadTitle("MyApp-v2.5.1").setDownLoadIcon(R.mipmap.ic_launcher).create(this).start();
//        DownLoadThread t=new DownLoadThread(path,"aa.jpg");
//        t.setUri(imaguri);
//        t.start();
        Intent intent= APPDownLoadService.getIntent(this,path,"myapp.apk",uri,R.mipmap.ic_launcher,"appv2.5","DownLoad Success","DownLoadComplete click to install",R.mipmap.ic_launcher,"start download","appv2.5downloading",true,true);
        startService(intent);
        mReceiver =new DownLoadReceiver();
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
    private class DownLoadReceiver extends APPDownLoadReceiver{
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
        public void onDownLoadComplete(String file) {
            Log.e(TAG, "onDownLoadComplete "+file);
        }

        @Override
        public void onDownLoading(int progress) {
            Log.e(TAG, "onDownLoading "+progress);
        }
    }
}
