package com.mcmo.z.librarydemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by weizhang210142 on 2016/8/25.
 */
public class NestedScrollActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablayout);
        ListView lv= (ListView) findViewById(R.id.lv);
        ArrayList<HashMap<String,String>> data=new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            HashMap<String,String> m=new HashMap<>();
            m.put("num",i+" ");
            data.add(m);
        }
        lv.setAdapter(new SimpleAdapter(this,data,android.R.layout.simple_list_item_1,new String[]{"num"},new int[]{android.R.id.text1}));
    }

    private void floatButton() {
        setContentView(R.layout.activity_nested);
        FloatingActionButton fbtn= (FloatingActionButton) findViewById(R.id.fab);
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.rvToDoList),"haha",Snackbar.LENGTH_SHORT).setAction("Done", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });
    }
}
