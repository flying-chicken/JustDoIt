package com.lee.recyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2017-08-25.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private BaseRecyclerTouchAdapter adapter;

    //让Adapter实现该接口并调用对应方法
    public interface ItemTouchCallbackListener{
        void onItemMove(int fromPosition, int toPosition);
        void onItemSwipe(int position, int direction);
    }

    public ItemTouchHelperCallback(BaseRecyclerTouchAdapter ad){
        if(!(ad instanceof ItemTouchCallbackListener)){
            throw new IllegalStateException("RecyclerView.Adapter must implement ItemTouchHelperCallback.ItemTouchCallbackListener interface.");
        }
        adapter = ad;
    }

    //设置可滑动和拖动的方向，为0时，不可拖动或滑动
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        int swipeFlag = 0; //滑动方向
        int dragFlag = 0;  //拖动方向
        if(lm instanceof LinearLayoutManager){
            if(((LinearLayoutManager) lm).getOrientation() == OrientationHelper.HORIZONTAL) {
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }else{
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
        }else{
            swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    //这里可以控制RecyclerView 拖动item改变位置
    // 在onMove时，需要动态刷新RecyclerView 的 Adapter 内容，可以传入Adapter对象到该类，或者让Adapter继承该类，在数据
    //内容或者位置发生变化时通知UI更新
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPos = viewHolder.getAdapterPosition();
        int toPos = target.getAdapterPosition();
        //这里循环从当前位置变换到目标位置，分别把中间所有的item的位置重新交换，可平滑移动效果(额，试了下效果，还是不要循环的好)
        //也可以去掉循环，直接用Collections.swap()方法
        //放到Adapter里面执行
//        if(fromPos < toPos){
//            for(int i=fromPos; i<toPos; i++){
//                Collections.swap(new ArrayList<String>(), i, i+1);
//            }
//        }else{
//            for(int i=fromPos; i>toPos; i++){
//                Collections.swap(new ArrayList<String>(), i, i--);
//            }
//        }

        //通知Adapter发生变化
        adapter.onItemMove(fromPos,toPos);
        return true;
    }

    //这里可以控制RecyclerView 左右或上下 滑动删除item
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("---", "onSwiped: direction = "+direction);
        //通知Adapter发生变化
        adapter.onItemSwipe(viewHolder.getAdapterPosition(),direction);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return super.isItemViewSwipeEnabled();
    }

    // 设置是否长按item时可以拖动item，也可以手动调用ItemTouchHelper.startDrag(ViewHolder)方法开始拖动
    @Override
    public boolean isLongPressDragEnabled() {
        return super.isLongPressDragEnabled();
    }
}
