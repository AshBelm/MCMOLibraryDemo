package com.mcmo.z.librarydemo.statusbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mcmo.z.library.sys.StatusBarUtil;
import com.mcmo.z.librarydemo.R;

public class MultiStatusBarViewPageActivity extends AppCompatActivity {
    private ViewPager vp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutl_status_bar_vp);
        StatusBarUtil.setTransparent(this,true);
        vp = findViewById(R.id.vp);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i==0){
                    StatusBarUtil.setTextDarkMode(MultiStatusBarViewPageActivity.this);
                }else if (i==1){
                    StatusBarUtil.setTextModeAuto(MultiStatusBarViewPageActivity.this,0xff00ffff);
                }else if(i==2){
                    StatusBarUtil.setTextModeAuto(MultiStatusBarViewPageActivity.this,0xffff0f00);
                }else if(i==3){
                    StatusBarUtil.setTextModeAuto(MultiStatusBarViewPageActivity.this,0xf0ffffff);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        vp.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if(i==0){
                    return new MainFragment();
                }else{
                    return MultiStatusBarVpFragment.getInstance(i);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        });

    }
    public void onFragment1Click(View view) {
        vp.setCurrentItem(0);
    }

    public void onFragment2Click(View view) {
        vp.setCurrentItem(1);
    }

    public void onFragment3Click(View view) {
        vp.setCurrentItem(2);
    }

    public void onFragment4Click(View view) {
        vp.setCurrentItem(3);
    }
}

