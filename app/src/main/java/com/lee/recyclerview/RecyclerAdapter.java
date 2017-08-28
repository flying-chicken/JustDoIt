package com.lee.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.OrientationHelper;
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

public class RecyclerAdapter extends BaseRecyclerTouchAdapter<RecyclerAdapter.MViewHolder> {
    public static final int GRID = 11;
    public static final int LINEAR = 22;
    public static final int STAGGERED = 33;

    private Activity aty;
    private List<DummyContent.DummyItem> items = new ArrayList<>();
    private OnItemClickListener listener;
    private int type;
    private int orientation = OrientationHelper.VERTICAL;

    public RecyclerAdapter(Activity aty, List<DummyContent.DummyItem> items, int type){
        this.aty = aty;
        this.items = items;
        this.type = type;
    }

    public void setOrientation(int orientation){
        this.orientation = orientation;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

    public void setOnItemClickListener(OnItemClickListener l){
        listener = l;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int id = type != LINEAR ? R.layout.grid_recycler_item : R.layout.layout_main_recycler_item;
        View view = aty.getLayoutInflater().inflate(id, parent, false);
        MViewHolder vh = new MViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        //不要在这里修改item的宽高，每次滚动会导致高度变化
        final DummyContent.DummyItem item = items.get(position);
        setViewheight(holder.view);
        holder.textView.setText(item.id);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onItemClick(item);
            }
        });
    }

    private void setViewheight(View view){
        if(type == LINEAR) return;
        if(orientation == OrientationHelper.HORIZONTAL){
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            lp.height = RecyclerView.LayoutParams.MATCH_PARENT;
            if(type == GRID){
                lp.width = 300;
            }else if(type == STAGGERED){
                lp.width = 200 + (int) (Math.random() * 200);
            }
            view.setLayoutParams(lp);
        }else {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            lp.width = RecyclerView.LayoutParams.MATCH_PARENT;
            if(type == GRID){
                lp.height = 240;
            }else if(type == STAGGERED){
                lp.height = 80 + (int) (Math.random() * 200);
            }
            view.setLayoutParams(lp);
        }
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
            //如果为瀑布流式布局，可以在这里动态设置高度
//            setViewheight(view);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DummyContent.DummyItem item);
    }
}
