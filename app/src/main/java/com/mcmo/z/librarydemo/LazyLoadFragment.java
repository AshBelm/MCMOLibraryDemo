package com.mcmo.z.librarydemo;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LazyLoadFragment extends Fragment {
    private static final String TAG = "LazyLoadFragment";
    private TextView tv;
    private int index;
    /**
     * 视图是否初始化完成
     */
    private boolean isViewCreated;
    /**
     * 数据是否加载完整，为了避免重复加载
     */
    private boolean isLoadDataCompleted;
    protected void onLazyLoadData(){
        Log.e(TAG,"onLazyLoadData "+index);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach " + index);
    }

    public static LazyLoadFragment createInstance(int index) {
        LazyLoadFragment f = new LazyLoadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView " + index);
        View view = inflater.inflate(R.layout.fragment_test, null);
        tv = view.findViewById(R.id.tv);
        isViewCreated = true;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated " + index);
        if (tv != null) {
            tv.setText("tab " + index);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint "+isVisibleToUser+" "+index);
        if(isVisibleToUser && isViewCreated && !isLoadDataCompleted){
            isLoadDataCompleted = true;
            onLazyLoadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated " + index);
        if(getUserVisibleHint()){
            isLoadDataCompleted = true;
            onLazyLoadData();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index = getArguments().getInt("index") + 1;
        Log.d(TAG, "onCreate " + index);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart " + index);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume " + index);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause " + index);
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop " + index);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView " + index);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy " + index);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach " + index);
    }
}
