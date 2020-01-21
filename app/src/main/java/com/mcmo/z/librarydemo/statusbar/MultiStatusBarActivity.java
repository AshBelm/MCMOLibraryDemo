package com.mcmo.z.librarydemo.statusbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.mcmo.z.library.sys.StatusBarUtil;
import com.mcmo.z.librarydemo.R;

public class MultiStatusBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutl_status_bar);
        StatusBarUtil.setCustomStatusBar(getWindow(),0xffff0000,false);
    }

    public void onFragment1Click(View view) {
        replaceFramgnet(0);
    }

    public void onFragment2Click(View view) {
        replaceFramgnet(1);
    }

    public void onFragment3Click(View view) {
        replaceFramgnet(2);
    }

    public void onFragment4Click(View view) {
        replaceFramgnet(3);
    }
    private void replaceFramgnet(int index){
        Fragment fragment = MultiStatusBarFragment.getInstance(index);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

    }
}
