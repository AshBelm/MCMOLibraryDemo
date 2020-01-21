package com.mcmo.z.librarydemo.statusbar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcmo.z.library.sys.StatusBarUtil;
import com.mcmo.z.librarydemo.R;

/**
 * 切换Fragment时改变StatusBar的颜色或者是否全屏
 * 实现方式是添加一个StatusBar大小的view到视图顶部，然后就随心所欲了。
 * 这个方法在点击切换时可选如果是配合ViewPage来使用在滑动时效果就不好了，可以在先为Activity设置全屏并且透明状态栏然后每个Fragment中添加一个这样的View来实现
 */
public class MultiStatusBarVpFragment extends Fragment {
    private int index = 0;
    private CustomerStatusBar csb;
    private TextView tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt("index", 0);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_multi_sb_vp,container,false);
        csb = v.findViewById(R.id.csb);
        tv = v.findViewById(R.id.tv_title);
         if(index==1){
            csb.setBackgroundColor(0xff00ffff);
        }else if(index==2){
            csb.setBackgroundColor(0xffff0f00);
        }else if(index == 3){
            csb.setBackgroundColor(0xf0ffffff);
        }
        tv.setText(index+"");
        return v;
    }

    public static MultiStatusBarVpFragment getInstance(int index) {
        MultiStatusBarVpFragment fragment = new MultiStatusBarVpFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        fragment.setArguments(bundle);
        return fragment;
    }
}
