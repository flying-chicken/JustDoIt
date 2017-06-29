package com.lee.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by Administrator on 2017-06-29.
 */

public class AudioBiu extends View implements View.OnTouchListener{
    private Paint mDotPanit;
    private Paint mImgPaint;
    private Paint mProgressPaint;
    private Paint mDashPaint;

    private Rect mDotRect;
    private Rect mImgRect;
    private Rect mProgressRect;

    private Path mDashPath;

    private float mProgress;

    public AudioBiu(Context context) {
        super(context);
        init(context);
    }

    public AudioBiu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AudioBiu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        ViewConfiguration.getTapTimeout();
        mDotPanit = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPanit.setStyle(Paint.Style.FILL);
        mImgPaint = new Paint();
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint = new Paint();
        PathEffect dash = new DashPathEffect(new float[]{8,8,8,8},1);//设置虚线的间隔和点的长度
        mDashPaint.setPathEffect(dash);
        mDashPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    private void drawImg(Canvas c){

    }

    private void drawDash(Canvas c){

    }

    private void drawProgress(Canvas c){

    }

    private void drawDot(Canvas c){

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}
