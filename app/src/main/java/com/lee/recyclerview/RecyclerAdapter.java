package com.lee.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.justdoit.R;
import com.lee.tablayout.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-06-21.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MViewHolder> {
    private Activity aty;
    private List<DummyContent.DummyItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public RecyclerAdapter(Activity aty, List<DummyContent.DummyItem> items){
        this.aty = aty;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener l){
        listener = l;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = aty.getLayoutInflater().inflate(R.layout.layout_main_recycler_item,parent,false);
        MViewHolder vh = new MViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final DummyContent.DummyItem item = items.get(position);
        holder.textView.setText(item.id);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class MViewHolder extends ViewHolder{
        private View view;
        private TextView textView;
        public MViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = (TextView) itemView.findViewById(R.id.tv_main_recycler_item);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DummyContent.DummyItem item);
    }
}
