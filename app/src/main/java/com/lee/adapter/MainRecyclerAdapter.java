package com.lee.adapter;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.justdoit.R;

import java.util.List;

/**
 * Created by Administrator on 2017-03-22.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter {
    private List<String> items;
    private Context context;
    private MainItemClickListener itemClickListener;

    public interface MainItemClickListener{
        void onItemClickListener(View view,int position);
    }

    public MainRecyclerAdapter(Context context,List<String> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //此处必须这样添加布局，否则会导致item宽度不能全屏
        MViewHolder holder = new MViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_main_recycler_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MViewHolder mholder = (MViewHolder) holder;
        mholder.tv.setText(items.get(position));
        if(itemClickListener != null){
            mholder.tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClickListener(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class MViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public MViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_main_recycler_item);
        }
    }

    public void setOnItemClickListener(MainItemClickListener l){
        itemClickListener = l;
    }
}
