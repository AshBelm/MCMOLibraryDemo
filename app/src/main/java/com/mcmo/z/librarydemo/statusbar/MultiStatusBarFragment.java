package com.mcmo.z.librarydemo.statusbar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcmo.z.library.sys.StatusBarUtil;

/**
 * 切换Fragment时改变StatusBar的颜色或者是否全屏
 * 实现方式是添加一个StatusBar大小的view到视图顶部，然后就随心所欲了。
 * 这个方法在点击切换时可选如果是配合ViewPage来使用在滑动时效果就不好了，可以在先为Activity设置全屏并且透明状态栏然后每个Fragment中添加一个这样的View来实现
 */
public class MultiStatusBarFragment extends Fragment {
    private int index = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt("index", 0);
        if(index ==0){
            StatusBarUtil.setCustomStatusBarColor(getActivity().getWindow(),0xaa0f0f0f);
        }else if(index==1){
            StatusBarUtil.setCustomStatusBarColor(getActivity().getWindow(),0xff00ff00);
        }else if(index==2){
            StatusBarUtil.setCustomStatusBarColor(getActivity().getWindow(),0xff00fff0);
        }else if(index == 3){
            StatusBarUtil.setCustomStatusBarVisibility(getActivity().getWindow(),true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(container.getContext());
        textView.setTextSize(30);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        textView.setGravity(Gravity.CENTER);
        textView.setText(index+"");
        textView.setBackgroundColor(0xff00ff00);
        return textView;
    }

    public static MultiStatusBarFragment getInstance(int index) {
        MultiStatusBarFragment fragment = new MultiStatusBarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }
}
