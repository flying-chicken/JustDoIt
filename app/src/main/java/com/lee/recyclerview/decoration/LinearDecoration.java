package com.lee.recyclerview.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017-03-22.
 */

public class LinearDecoration extends RecyclerView.ItemDecoration {
    private int[] attrs = new int[]{android.R.attr.listDivider};
    private Drawable divider;
    private int orientation;
    private Rect mBounds = new Rect();

    public static final int VERTICAL = 11;
    public static final int HORIZONTAL = 22;
    public static final int GRID = 33;

    public LinearDecoration(Context context, int orientation){
        TypedArray a = context.obtainStyledAttributes(attrs);
        divider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int o){
//        if( o != VERTICAL || o != HORIZONTAL || o != GRID){
//            throw new IllegalArgumentException("invalid orientation");
//        }
        orientation = o;
    }

    public void setDivier(Drawable d){
        divider = d;
    }


    //在drawchild 之前执行
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(parent.getLayoutManager() == null) return;

        if(orientation == VERTICAL){
            drawVertical(c,parent);
        }else if(orientation == HORIZONTAL){
            drawHorizontal(c,parent);
        }else if(orientation == GRID){
            drawGrid(c,parent);
        }
    }

    private void drawVertical(Canvas c,RecyclerView parent){
        c.save();
        int left;
        int right;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && parent.getClipToPadding()){
            left = parent.getPaddingLeft();
//            right = parent.getWidth() - parent.getPaddingRight();
            c.clipRect(left,parent.getPaddingTop(),parent.getWidth() - parent.getPaddingRight(),parent.getHeight() - parent.getPaddingBottom());
        }else{
            left = 0;
//            right = parent.getWidth();
        }
        for(int i=0; i<parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child,mBounds);
            final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
            final int top = bottom - divider.getIntrinsicHeight();
            right = mBounds.right;
            divider.setBounds(left,top,right,bottom);
            divider.draw(c);
        }
        c.restore();
    }

    private void drawHorizontal(Canvas c, RecyclerView parent){
        c.save();
        final int top;
        int bottom;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && parent.getClipToPadding()){
            top = parent.getPaddingTop();
//            bottom = parent.getHeight() - parent.getPaddingBottom();
            c.clipRect(parent.getPaddingLeft(),top,parent.getWidth() - parent.getPaddingRight(),parent.getHeight() - parent.getPaddingBottom());
        }else{
            top = 0;
        }
        for(int i=0; i<parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child,mBounds);
            final int right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
            final int left = right - divider.getIntrinsicWidth();
            bottom = mBounds.bottom;
            divider.setBounds(left,top,right,bottom);
            divider.draw(c);
        }
        c.restore();
    }

    private void drawGrid(Canvas c,RecyclerView parent){
        if(parent.getLayoutManager() == null || !(parent.getLayoutManager() instanceof GridLayoutManager)) return;
        drawVertical(c,parent);
        drawHorizontal(c,parent);
//        c.save();
//        GridLayoutManager gm = (GridLayoutManager) parent.getLayoutManager();
//        gm.getSpanCount();
//        gm.getOrientation();
//        int left, top, right, bottom;
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && parent.getClipToPadding()){
//            left = parent.getPaddingLeft();
//            top = parent.getPaddingTop();
//            c.clipRect(parent.getPaddingLeft(),top,parent.getWidth() - parent.getPaddingRight(),parent.getHeight() - parent.getPaddingBottom());
//        }else{
//            left = 0;
//            top = 0;
//        }
//        for(int i=0; i<parent.getChildCount(); i++){
//            View child = parent.getChildAt(i);
//            parent.getDecoratedBoundsWithMargins(child,mBounds);
//        }
//        c.restore();
    }

    //在drawchild 之后执行 ， 与onDraw只需重写其中一个就行
//    @Override
//    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDrawOver(c, parent, state);
//    }

    //为每个item 设置一定偏移量
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }
}
