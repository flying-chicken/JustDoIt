package com.lee.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017-08-28.
 */

public class BaseRecyclerTouchAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchHelperCallback.ItemTouchCallbackListener{
    @Override
    public int getItemCount() {
        return 0;
    }
    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
//        if(fromPosition < toPosition){
//            for(int i=fromPosition; i<toPosition; i++){
//                Collections.swap(new ArrayList<String>(), i, i+1);
//            }
//        }else{
//            for(int i=fromPosition; i>toPosition; i++){
//                Collections.swap(new ArrayList<String>(), i, i--);
//            }
//        }
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        notifyItemRemoved(position);
    }
}
