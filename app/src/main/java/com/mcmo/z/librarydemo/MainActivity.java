package com.mcmo.z.librarydemo;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends ListActivity {
    private ArrayList<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        items=new ArrayList<>();
        items.add(new Item("bottomDialog",BottomActivity.class));
        items.add(new Item("BlueToothScan",BuleToothScanActivity.class));
        items.add(new Item("NestedScrollDemo",NestedScrollActivity.class));
        items.add(new Item("ColorPick",ColorPickActivity.class));
        setListAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView=new TextView(MainActivity.this);
                convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 140));
            }
            TextView tv= (TextView) convertView;
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setText(items.get(position).text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent  intent=new Intent(MainActivity.this,items.get(position).clazz);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    private class Item{
        private String text;
        private Class clazz;

        public Item(String text, Class clazz) {
            this.text = text;
            this.clazz = clazz;
        }
    }
}
