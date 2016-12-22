package com.mcmo.z.librarydemo;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mcmo.z.library.widget.ColorDetailView;
import com.mcmo.z.library.widget.LinearColorPickView;
import com.mcmo.z.library.widget.OnColorChangeListener;

/**
 * Created by ZhangWei on 2016/12/22.
 */

public class ColorPickActivity extends Activity {
    private LinearColorPickView lcp;
    private ColorDetailView cdv;
    private View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorpick);
        lcp= (LinearColorPickView) findViewById(R.id.lcp);
        cdv= (ColorDetailView) findViewById(R.id.cdv);
        v=findViewById(R.id.v);
        lcp.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void onColorChanged(int color, float x, float y) {
                cdv.setColor(color);
            }
        });
        cdv.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void onColorChanged(int color, float x, float y) {
                v.setBackgroundColor(color);
                Log.e("color", "onColorChanged "+Integer.toHexString(color));
            }
        });
    }
}
