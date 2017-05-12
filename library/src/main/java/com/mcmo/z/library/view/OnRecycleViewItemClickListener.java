package com.mcmo.z.library.view;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by ZhangWei on 2017/5/12.
 */

public abstract class OnRecycleViewItemClickListener implements RecyclerView.OnItemTouchListener{
    private RecyclerView recyclerView;
    private GestureDetectorCompat mGestureDetector;

    public OnRecycleViewItemClickListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(),new ItemClickHelper());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public abstract void onItemClick(RecyclerView view, RecyclerView.ViewHolder holder, int position);
    public abstract void onItemLongClick(RecyclerView view,RecyclerView.ViewHolder holder,int position);

    private class ItemClickHelper extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if(child!=null){
                int position = recyclerView.getChildAdapterPosition(child);
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemClick(recyclerView,vh,position);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
            if(child!=null){
                int position = recyclerView.getChildAdapterPosition(child);
                RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
                onItemLongClick(recyclerView,vh,position);
            }

        }
    }
}
