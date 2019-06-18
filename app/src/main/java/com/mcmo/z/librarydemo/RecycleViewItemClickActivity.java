package com.mcmo.z.librarydemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mcmo.z.library.view.LinearSimpleDecoration;
import com.mcmo.z.library.view.OnRecycleViewItemClickListener;

public class RecycleViewItemClickActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(new Adapter());
        recyclerView.addItemDecoration(new LinearSimpleDecoration(this,LinearSimpleDecoration.VERTICAL_LIST,R.dimen.divider,R.color.colorAccent,false));
        recyclerView.addOnItemTouchListener(new OnRecycleViewItemClickListener(recyclerView){

            @Override
            public void onItemClick(RecyclerView view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecycleViewItemClickActivity.this,"Click "+position,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(RecyclerView view, RecyclerView.ViewHolder holder, int position) {
                Toast.makeText(RecycleViewItemClickActivity.this,"Long "+position,Toast.LENGTH_SHORT).show();

            }
        });

        setContentView(recyclerView);
    }
    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(new TextView(RecycleViewItemClickActivity.this)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(position+" R");
            ((TextView)holder.itemView).setTextSize(20);
            holder.itemView.setBackgroundColor(0xff00ff00);
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }
}
