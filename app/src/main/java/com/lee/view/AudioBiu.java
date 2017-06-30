package com.lee.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lee.justdoit.R;

/**
 * Created by Administrator on 2017-06-29.
 */

public class AudioBiu extends View{
    private static final String TAG = "AUDIOBIU~";
    private static final int OBJECT_IMG = 11;
    private static final int OBJECT_DOT = 22;
    private static final int OBJECT_NULL = 0;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int PADDING;

    private int mTouchObject;
    private boolean mImgTouched;

    private Paint mDotPanit;
    private Paint mImgPaint;
    private Paint mProgressPaint;
    private Paint mDashPaint;

    private RectF mDotRect;
    private PointF mDotCenter;
    private int mDotRadius;
    private RectF mImgRect;
    private RectF mProgressRect;
    private PointF mTouchPoint;

    private Path mDashPath;
    private float mProgress;
    private Bitmap mImage;
    private int mColor;

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
        PADDING = dip2px(8);
        mTouchPoint = new PointF();
        mDashPath = new Path();
        mProgress = 0;
        mImage = BitmapFactory.decodeResource(getResources(), R.mipmap.audio_32);
        Palette.from(mImage).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if(swatch != null){
                    mColor = swatch.getRgb();
                }else{
                    mColor = getResources().getColor(R.color.colorAccent);
                }
            }
        });
        initPaint();
        initRect();
        setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void initPaint(){
        mDotPanit = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPanit.setStyle(Paint.Style.FILL);
        mDotPanit.setColor(Color.BLACK);
        mImgPaint = new Paint();
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(Color.WHITE);
        mDashPaint = new Paint();
        mDashPaint.setColor(mColor);
        PathEffect dash = new DashPathEffect(new float[]{8,8,8,8},1);//设置虚线的间隔和点的长度
        mDashPaint.setPathEffect(dash);
    }

    private void initRect(){
        mImgRect = new RectF(PADDING,0,PADDING+mImage.getWidth(),0);
        mDotRadius = dip2px(5);
        mDotCenter = new PointF(mImgRect.right+PADDING+mDotRadius,0);
        mDotRect = new RectF();
        mProgressRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = widthSpec,height = heightSpec;
        ViewGroup parent = (ViewGroup) getParent();
        if(widthMode != MeasureSpec.EXACTLY){
            if(parent != null)
                width = parent.getMeasuredWidth();
            else
                width = SCREEN_WIDTH;
        }
        if(height < mImage.getHeight()+PADDING*2)
            height = mImage.getHeight() + PADDING*2;
        if(heightMode != MeasureSpec.EXACTLY){
            height = mImage.getHeight() + PADDING*2;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mImgRect.set(PADDING, (getHeight()-mImage.getHeight())/2, mImage.getWidth()+PADDING, (getHeight()+mImage.getHeight())/2);
        mDotCenter.set(mDotCenter.x, getHeight()/2);
        mDotRect.set(mDotCenter.x-mDotRadius, mDotCenter.y-mDotRadius, mDotCenter.x+mDotRadius, mDotCenter.y+mDotRadius);
        mProgressRect.set(mDotCenter.x, (getHeight()-dip2px(1))/2, getWidth()-PADDING, (getHeight()+dip2px(1))/2);
        mProgress = mProgressRect.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        drawImg(canvas);
        canvas.restore();
        drawDash(canvas);
        drawProgress(canvas);
        drawDot(canvas);
    }

    private void drawImg(Canvas c){
        if(mImgTouched) {
            c.rotate(-35,mImgRect.left,getHeight()/2);
        }
        c.drawBitmap(mImage,mImgRect.left,mImgRect.top,mImgPaint);
    }

    private void drawDash(Canvas c){

    }

    private void drawProgress(Canvas c){
        c.drawRect(mProgressRect,mProgressPaint);
    }

    private void drawDot(Canvas c){
        c.drawCircle(mDotCenter.x,mDotCenter.y,mDotRadius,mDotPanit);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(event.getX(),event.getY());
                log("touch down");
                checkTouchObject(mTouchPoint);
                doObjectTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                log("touch move");
                if(mTouchObject == OBJECT_DOT){
                    float x = event.getX();
                    if(x > mImgRect.right + PADDING + dip2px(4)
                            && x < mProgressRect.right) {
                        mDotCenter = new PointF(x,mDotCenter.y);
                        mDotRect.set(mDotCenter.x-mDotRadius, mDotCenter.y-mDotRadius, mDotCenter.x+mDotRadius, mDotCenter.y+mDotRadius);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                log("touch up");
                doObjectTouchUp(event);
                break;
        }
        return true;
    }

    private void checkTouchObject(PointF point){
        if(mImgRect.contains(point.x,point.y)){
            log("image touched");
            mTouchObject = OBJECT_IMG;
        }else if(mDotRect.contains(point.x,point.y)){
            log("dot touched");
            mTouchObject = OBJECT_DOT;
        }
    }

    private void doObjectTouchDown(MotionEvent e){
        if(mTouchObject == OBJECT_IMG){
            mImgTouched = true;
        }else if(mTouchObject == OBJECT_DOT){
            mDotRadius = dip2px(8);
        }
        invalidate();
    }

    private void doObjectTouchUp(MotionEvent e){
        if(mTouchObject == OBJECT_IMG){
            mImgTouched = false;
        }else if(mTouchObject == OBJECT_DOT){
            mDotRadius = dip2px(5);
        }
        invalidate();
        mTouchObject = OBJECT_NULL;
    }

    private int dip2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int px2dip( float px) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    private void log(String log){
        Log.e(TAG,"-- "+log+" --");
    }
}
