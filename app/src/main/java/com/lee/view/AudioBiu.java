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
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lee.justdoit.R;

/**
 * Created by Administrator on 2017-06-29.
 */

public class AudioBiu extends View {
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

    private DashBuilder dashBuilder;

    private LongPressRunnable mLongPressRunnable = new LongPressRunnable();

    private class LongPressRunnable implements Runnable {
        @Override
        public void run() {
            log(" is long pressed");
            mImgTouched = true;
            dashBuilder.start(mDotCenter.x);
        }
    }

    public AudioBiu(Context context) {
        super(context);
        init(context);
    }

    public AudioBiu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioBiu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
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
                if (swatch != null) {
                    mColor = swatch.getRgb();
                } else {
                    mColor = getResources().getColor(R.color.colorAccent);
                }
            }
        });
        initPaint();
        initRect();
        setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    private void initPaint() {
        mDotPanit = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPanit.setStyle(Paint.Style.FILL);
        mDotPanit.setColor(Color.BLACK);
        mImgPaint = new Paint();
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(Color.WHITE);
        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setColor(Color.BLACK);
        PathEffect dash = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);//设置虚线的间隔和点的长度
        mDashPaint.setPathEffect(dash);
    }

    private void initRect() {
        mImgRect = new RectF(PADDING, 0, PADDING + mImage.getWidth(), 0);
        mDotRadius = dip2px(5);
        mDotCenter = new PointF(mImgRect.right + PADDING + mDotRadius, 0);
        mDotRect = new RectF();
        mProgressRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = widthSpec, height = heightSpec;
        ViewGroup parent = (ViewGroup) getParent();
        if (widthMode != MeasureSpec.EXACTLY) {
            if (parent != null)
                width = parent.getMeasuredWidth();
            else
                width = SCREEN_WIDTH;
        }
        if (height < mImage.getHeight() + PADDING * 2)
            height = mImage.getHeight() + PADDING * 2;
        if (heightMode != MeasureSpec.EXACTLY) {
            height = mImage.getHeight() + PADDING * 2;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mImgRect.set(PADDING, (getHeight() - mImage.getHeight()) / 2, mImage.getWidth() + PADDING, (getHeight() + mImage.getHeight()) / 2);
        mDotCenter.set(mDotCenter.x, getHeight() / 2);
        mDotRect.set(mDotCenter.x - mDotRadius, mDotCenter.y - mDotRadius, mDotCenter.x + mDotRadius, mDotCenter.y + mDotRadius);
        mProgressRect.set(mDotCenter.x, (getHeight() - dip2px(1)) / 2, getWidth() - PADDING, (getHeight() + dip2px(1)) / 2);
        mProgress = mProgressRect.width();
        dashBuilder = new DashBuilder(new PointF(mImgRect.left, mDotCenter.y), new PointF(mProgressRect.left, mDotCenter.y), new PointF(mProgressRect.right, mDotCenter.y));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawImg(canvas);
        drawDash(canvas);
        drawProgress(canvas);
        drawDot(canvas);
    }

    private void drawImg(Canvas c) {
        c.save();
        if (mImgTouched) {
            c.rotate(-30, mImgRect.left, getHeight() / 2);
        }
        c.drawBitmap(mImage, mImgRect.left, mImgRect.top, mImgPaint);
        c.restore();
    }

    private void drawDash(Canvas c) {
        if (mImgTouched) {
//            c.drawPath(mDashPath, mDashPaint);
            c.drawPath(dashBuilder.getPath(), mDashPaint);
        }
    }

    private void drawProgress(Canvas c) {
        c.drawRect(mProgressRect, mProgressPaint);
    }

    private void drawDot(Canvas c) {
        c.drawCircle(mDotCenter.x, mDotCenter.y, mDotRadius, mDotPanit);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchPoint.set(event.getX(), event.getY());
                log("touch down");
                checkTouchObject(mTouchPoint);
                doObjectTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                log("touch move");
                if (mTouchObject == OBJECT_DOT) {
                    float x = event.getX();
                    if (x > mProgressRect.left && x < mProgressRect.right) {
                        mDotCenter = new PointF(x, mDotCenter.y);
                        mDotRect.set(mDotCenter.x - mDotRadius, mDotCenter.y - mDotRadius, mDotCenter.x + mDotRadius, mDotCenter.y + mDotRadius);
                    }
                } else if (mTouchObject == OBJECT_IMG) {
                    if (!mImgRect.contains(event.getX(), event.getY())) {
                        removeCallbacks(mLongPressRunnable);
                        mImgTouched = false;
                        dashBuilder.stop();
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                log("touch up");
                doObjectTouchUp(event);
                break;
        }
        return true;
    }

    private void checkTouchObject(PointF point) {
        if (mImgRect.contains(point.x, point.y)) {
            log("image touched");
            mTouchObject = OBJECT_IMG;
        } else if (mDotRect.contains(point.x, point.y)) {
            log("dot touched");
            mTouchObject = OBJECT_DOT;
        }
    }

    private void doObjectTouchDown(MotionEvent e) {
        if (e.getPointerCount() != 1) return;
        if (mTouchObject == OBJECT_IMG) {
            checkLongPress();
        } else if (mTouchObject == OBJECT_DOT) {
            mDotRadius = dip2px(8);
            invalidate();
        }
    }

    private void checkLongPress() {
        postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
    }

    private void doObjectTouchUp(MotionEvent e) {
        if (mTouchObject == OBJECT_IMG) {
            mImgTouched = false;
            dashBuilder.stop();
            removeCallbacks(mLongPressRunnable);
        } else if (mTouchObject == OBJECT_DOT) {
            mDotRadius = dip2px(5);
        }
        invalidate();
        mTouchObject = OBJECT_NULL;
    }

    private int dip2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int px2dip(float px) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    private void log(String log) {
        String objectName;
        switch (mTouchObject) {
            case OBJECT_IMG:
                objectName = "OBJECT_IMG";
                break;
            case OBJECT_DOT:
                objectName = "OBJECT_DOI";
                break;
            default:
                objectName = "OBJECT_NULL";
                break;
        }
        Log.e(TAG, "-- " + objectName + " " + log + " --");
    }

    public class DashBuilder {
        public static final int DIR_LEFT = 33;
        public static final int DIR_RIGHT = 44;
        public static final int DIR_STOP = 55;
        private static final float VELOCITY = 4;

        private PointF dStartPoint, dLeftPoint, dEndPoint;
        private Path dPath;
        private int dDir;
        private float dX;
        private DRunnable dRunnable = new DRunnable();

        private static final int MSG_STOP = 1;
        private static final int MSG_START = 2;
        private Handler dHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_START:
                        move(msg.arg1);
                        break;
                    case MSG_STOP:
                        break;
                }
            }
        };

        private class DRunnable implements Runnable{
            @Override
            public void run() {
                while (true) {
                    Log.e(" --- ", "post run ...");
                }
            }
        }

        /* start  left                  end
         *  ·     ·--------------------·
         */
        public DashBuilder(PointF start, PointF left, PointF end) {
            dStartPoint = start;
            dLeftPoint = left;
            dEndPoint = end;
            dDir = DIR_RIGHT;
            dPath = new Path();
        }

        private void start(float startX) {
            log("left :" + dLeftPoint.toString() + ", end :" + dEndPoint.toString());
            post(dRunnable);
            Message msg = dHandler.obtainMessage();
            msg.what = MSG_START;
            msg.arg1 = (int) startX;
            dHandler.sendMessage(msg);
        }

        //按住时，计算圆点X坐标，并计算path路径
        public void move(float startX) {
            dX = startX;
            switch (dDir) {
                case DIR_LEFT:
                    dX -= VELOCITY;
                    if (dX <= dLeftPoint.x) {
                        dX = dLeftPoint.x;
                        dDir = DIR_RIGHT;
                    }
                    calculateDashPath(dX);
                    start(dX);
                    break;
                case DIR_RIGHT:
                    dX += VELOCITY;
                    if (dX >= dEndPoint.x) {
                        dX = dEndPoint.x;
                        dDir = DIR_LEFT;
                    }
                    calculateDashPath(dX);
                    start(dX);
                    break;
                default:
                    dHandler.sendEmptyMessage(MSG_STOP);
                    break;
            }
        }

        //假装是个抛物线. x: 圆点的x坐标
        private void calculateDashPath(float x) {
            dPath.reset();
            float cx = (x - dStartPoint.x) / 2;
            float cy = dStartPoint.y - (float) (1 / 1.732 * cx);
            dPath.moveTo(dStartPoint.x, dStartPoint.y);
            dPath.quadTo(cx, cy, x, dStartPoint.y);
            postInvalidate();
        }

        private void stop() {
            removeCallbacks(dRunnable);
            dDir = DIR_STOP;
            dHandler.sendEmptyMessage(MSG_STOP);
        }

        public Path getPath() {
            return dPath;
        }
    }
}
