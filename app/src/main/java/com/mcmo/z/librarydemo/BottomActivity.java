package com.mcmo.z.librarydemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.mcmo.z.library.widget.dialog.BottomDialog;

/**
 * Created by weizhang210142 on 2016/4/25.
 */
public class BottomActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button=new Button(this);
        setContentView(button);
        button.setText("dialog");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view= LayoutInflater.from(BottomActivity.this).inflate(R.layout.dialog_bottom,null);
                new BottomDialog(BottomActivity.this).setView(view).show();
            }
        });
    }
}
