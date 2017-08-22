package com.lee.recyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-08-22.
 */

public class GridDecoration extends RecyclerView.ItemDecoration {
    public static int VERTICAL = 11;
    public static int HORIZONTAL = 22;

    private int attrs[] = new int[]{android.R.attr.listDivider};
    private Drawable divider;
    private Rect bounds = new Rect();
    private int orientation;

    public GridDecoration(Context context,int orientation){
        TypedArray a = context.obtainStyledAttributes(attrs);
        divider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int o){
        orientation = o;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        drawV(c, parent, spanCount);
        drawH(c, parent, spanCount);
    }

    private int getSpanCount(RecyclerView parent){
        int spanCount = 0;
        if(parent.getLayoutManager() instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        }else if(parent.getLayoutManager() instanceof StaggeredGridLayoutManager){
            spanCount = ((StaggeredGridLayoutManager) parent.getLayoutManager()).getSpanCount();
        }
        return spanCount;
    }

    private void drawV(Canvas c,RecyclerView parent,int spanCount){
        c.save();
        int l=0, t=0, r=0, b=0;
        for(int i=0; i<parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child,bounds);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            l = bounds.left - lp.leftMargin;
            t = bounds.bottom + lp.bottomMargin;
            r = bounds.right + lp.rightMargin;
            b = t + divider.getIntrinsicHeight();
            divider.setBounds(l, t, r, b);
            divider.draw(c);
        }
        c.restore();
    }

    private void drawH(Canvas c,RecyclerView parent, int spanCount){
        c.save();
        int l=0, t=0, r=0, b=0;
        for(int i=0; i<parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, bounds);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            l = bounds.right + lp.rightMargin;
            t = bounds.top - lp.topMargin;
            r = l + divider.getIntrinsicWidth();
            //如果不加 height，在瀑布流式布局时，会导致间隔线右下角有个正方形空白
            b = bounds.bottom + lp.bottomMargin + divider.getIntrinsicHeight();
            divider.setBounds(l, t, r, b);
            divider.draw(c);
        }
        c.restore();
    }

    //由于绘制间隔线（宽度和高度明显占据一部分界面）的时候，只绘制了 下边和右边，所以这里就要对item的位置（左边和上边）进行偏移设置，
    //让item显示在间隔区域内
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int ap = parent.getChildAdapterPosition(view);
//        int lp = parent.getChildLayoutPosition(view);
        if(orientation == VERTICAL) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            parent.getDecoratedBoundsWithMargins(view, bounds);
            int l = ap % spanCount == 0 ? 0 : divider.getIntrinsicWidth();//不适用于瀑布流式布局
            int t = ap < spanCount ? 0 : divider.getIntrinsicHeight();
            outRect.set(l, t, 0, 0);
        }
    }

}
