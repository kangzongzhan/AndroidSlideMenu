package com.zongzhan.androidslidemenu;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zongzhan.slidemenu.SlideMenuItemViewHolder;
import com.zongzhan.slidemenu.SlideMenuListAdapter;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration());
        recyclerView.setAdapter(adapter);
    }



    SlideMenuListAdapter adapter = new SlideMenuListAdapter<MyViewHolder>() {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(this, LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.setText("" + position);
        }

        @Override
        public int getItemCount() {
            return 200;
        }

    };

    class MyViewHolder extends SlideMenuItemViewHolder {
        TextView textView;
        public MyViewHolder(SlideMenuListAdapter adapter, View itemView) {
            super(adapter, itemView);
            textView = itemView.findViewById(R.id.text);
        }
        public void setText(String s) {
            textView.setText(s);
        }
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, 1);
        }
    }

}