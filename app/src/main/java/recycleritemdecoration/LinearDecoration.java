package recycleritemdecoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017-03-22.
 */

public class LinearDecoration extends RecyclerView.ItemDecoration {
    private int[] attrs = new int[]{android.R.attr.listDivider};
    private Drawable divider;
    private int orientation;

    public static final int VERTICAL = 11;
    public static final int HORIZONTAL = 22;

    public LinearDecoration(Context context, int orientation){
        TypedArray a = context.obtainStyledAttributes(attrs);
        divider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int o){
        if( o != VERTICAL || o != HORIZONTAL){
            throw new IllegalArgumentException("invalid orientation");
        }
        orientation = o;
    }

    //在drawchild 之前执行
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if(orientation == VERTICAL){
            drawVertical(c,parent);
        }else{
            drawHorizontal(c,parent);
        }
    }

    private void drawVertical(Canvas c,RecyclerView parent){

    }

    private void drawHorizontal(Canvas c, RecyclerView parent){

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
