package com.mcmo.z.librarydemo.mulitgesture;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mcmo.z.library.view.MultiGestureDetector;
import com.mcmo.z.librarydemo.MultiDemoView;
import com.mcmo.z.librarydemo.R;

public class MultiGestureActivity extends AppCompatActivity {
    private static final String TAG = "MultiGestureActivity";
    private MultiGestureDetector multiGestureDetector;
    private MultiDemoView mdv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multigesture);
        multiGestureDetector = new MultiGestureDetector(this,new Litener());
        mdv = (MultiDemoView) findViewById(R.id.mdv);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        multiGestureDetector.onTouchEvent(event);
        return true;
    }
    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Multi : " + msg);
    }
    private class Litener implements MultiGestureDetector.ClickListener, MultiGestureDetector.MovementListener, MultiGestureDetector.DoubleFingerListener{

        @Override
        public boolean onDown(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onMove(MotionEvent downEv, MotionEvent ev, float scrollX, float scrollY) {
            mdv.move((int)scrollX,(int)scrollY);
            return true;
        }

        @Override
        public boolean onUp(MotionEvent ev) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent downEv, MotionEvent ev, float velocityX, float velocityY) {
            Log.e(TAG, "onFling: "+velocityX+" "+velocityY );
            return false;
        }

        @Override
        public void onSingleTap(MotionEvent ev) {
            toast("SingleTop");
        }

        @Override
        public void onDoubleTap(MotionEvent ev) {
            toast("DoubleTap");
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            toast("LongPress");
        }

        @Override
        public boolean onScaleRotateBegin(MultiGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleRotate(MultiGestureDetector detector) {
            float scale = detector.getScaleFactorAll();
            float rotate = detector.getRotateFactorAll();
            Log.e(TAG, "onScaleRotate: "+scale );
            mdv.scale(rotate,scale);
            return true;
        }

        @Override
        public boolean onScaleRotateEnd(MultiGestureDetector detector) {
            return false;
        }
    }
}
