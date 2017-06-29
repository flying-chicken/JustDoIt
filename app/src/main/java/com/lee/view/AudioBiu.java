package com.lee.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import com.lee.justdoit.R;

/**
 * Created by Administrator on 2017-06-29.
 */

public class AudioBiu extends View{
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;

    private Paint mDotPanit;
    private Paint mImgPaint;
    private Paint mProgressPaint;
    private Paint mDashPaint;

    private Rect mDotRect;
    private Rect mImgRect;
    private Rect mProgressRect;

    private Path mDashPath;
    private float mProgress;
    private Bitmap mImage;

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
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        SCREEN_WIDTH = outMetrics.widthPixels;
        SCREEN_HEIGHT = outMetrics.heightPixels;

//        ViewConfiguration.getTapTimeout();
        initPaint();
        initRect();
        mDashPath = new Path();
        mProgress = 0;
        mImage = BitmapFactory.decodeResource(getResources(), R.mipmap.audio_16);

    }

    private void initPaint(){
        mDotPanit = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPanit.setStyle(Paint.Style.FILL);
        mImgPaint = new Paint();
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint = new Paint();
        PathEffect dash = new DashPathEffect(new float[]{8,8,8,8},1);//设置虚线的间隔和点的长度
        mDashPaint.setPathEffect(dash);
    }

    private void initRect(){
        mImgRect = new Rect();
        mDotRect = new Rect();
        mProgressRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width,height;
        ViewGroup parent = (ViewGroup) getParent();
        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSpec;
        }else{
            if(parent != null)
                width = parent.getMeasuredWidth();
            else
                width = SCREEN_WIDTH;
        }
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSpec;
        }else{
            height = mImage.getHeight();
        }
        setMeasuredDimension(width,height);
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    private int dip2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int px2dip( float px) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
