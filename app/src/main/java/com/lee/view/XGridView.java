package com.lee.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017-08-08.
 */

public class XGridView extends GridView {
    private final int PRESS_TIME = 1000;//长按时间

    private int mDownX;//触碰时的X坐标
    private int mDownY;//触碰时的Y坐标
    private int mMoveX;//移动时的X坐标
    private int mMoveY;//移动时的Y坐标

    private int mOffset2Top;//DragGridView距离屏幕顶部的偏移量
    private int mOffset2Left;//DragGridView距离屏幕左边的偏移量
    private int mPointToItemTop;//触碰点距离ItemView的上边距
    private int mPointToItemLeft;//触碰点距离ItemView的左边距
    private int mStatusHeight;//状态栏高度

    private boolean isDraging;//是否正在拖曳

    private Bitmap mBitmap;//ItemView的图片
    private int mTouchPostiion;//触碰的位置
    private View mTouchItemView;//触碰的ItemView

//    private Vibrator mVibrator;//震动器
    private ImageView mDragImageView;//拖曳的View
    private WindowManager mWindowManager;//窗口管理器
    private WindowManager.LayoutParams mWindowLayoutParams;//窗口管理器布局

    private OnChanageListener onChanageListener;//交换事件监听器

    private Handler mHandler = new Handler();


    public XGridView(Context context) {
        this(context,null);
    }

    public XGridView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mStatusHeight = getStatusHeight(context);
//        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //使用Handler延迟dragResponseMS执行mLongClickRunnable
                mHandler.postDelayed(mLongClickRunnable, PRESS_TIME);

                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                //根据按下的X,Y坐标获取所点击item的position
                mTouchPostiion = pointToPosition(mDownX, mDownY);

                if (mTouchPostiion == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                //根据position获取该item所对应的View
                mTouchItemView = getChildAt(mTouchPostiion - getFirstVisiblePosition());

                //下面这几个距离大家可以参考我的博客上面的图来理解下
                mPointToItemTop = mDownY - mTouchItemView.getTop();
                mPointToItemLeft = mDownX - mTouchItemView.getLeft();

                mOffset2Top = (int) (ev.getRawY() - mDownY);
                mOffset2Left = (int) (ev.getRawX() - mDownX);

                //开启mDragItemView绘图缓存
                mTouchItemView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                mBitmap = Bitmap.createBitmap(mTouchItemView.getDrawingCache());
                //这一步很关键，释放绘图缓存，避免出现重复的镜像
                mTouchItemView.destroyDrawingCache();

                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                //拖曳点超出GridView区域则取消拖曳事件
                if (ev.getY() > getHeight() || ev.getY() < 0) {
                    onStopDrag();
                }

                //如果我们在按下的item上面移动，只要超过item的边界就移除mRunnable
                if (!isTouchInItem(mTouchItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isDraging && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    mMoveX = (int) ev.getX();
                    mMoveY = (int) ev.getY();
                    //拖动item
                    onDragItem(mMoveX, mMoveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }


    //处理长按事件的线程
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            isDraging = true; //设置可以拖拽
//            mVibrator.vibrate(50); //震动一下
            mTouchItemView.setVisibility(View.INVISIBLE);//隐藏该ItemView

            //根据我们按下的点显示ItemView镜像
            createDragView(mBitmap, mDownX, mDownY);
        }
    };

    //添加拖动View
    private void createDragView(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPointToItemTop + mOffset2Left;
        mWindowLayoutParams.y = downY - mPointToItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.6f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    private void removeDragView() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    //是否点击在GridView的item上面
    private boolean isTouchInItem(View dragView, int x, int y) {
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }
        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }
        return true;
    }

    //拖动事件处理
    private void onDragItem(int moveX, int moveY) {
        mWindowLayoutParams.x = moveX - mPointToItemLeft + mOffset2Left;
        mWindowLayoutParams.y = moveY - mPointToItemTop + mOffset2Top - mStatusHeight;
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
        onSwapItem(moveX, moveY);//Item的相互交换
    }

    //交换item,并且控制item之间的显示与隐藏效果
    private void onSwapItem(int moveX, int moveY) {
        //获取我们手指移动到的那个item的position
        int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mTouchPostiion && tempPosition != AdapterView.INVALID_POSITION) {
            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);//拖动到了新的item,新的item隐藏掉
            getChildAt(mTouchPostiion - getFirstVisiblePosition()).setVisibility(View.VISIBLE);//之前的item显示出来

            if (onChanageListener != null) {
                onChanageListener.onChange(mTouchPostiion, tempPosition);
            }

            mTouchPostiion = tempPosition;
        }
    }

    //停止拖拽我们将之前隐藏的item显示出来，并将DragView移除
    private void onStopDrag() {
        isDraging = false;
        getChildAt(mTouchPostiion - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
        removeDragView();
    }

    //Item交换事件监听
    public void setOnChangeListener(OnChanageListener onChanageListener) {
        this.onChanageListener = onChanageListener;
    }

    //获取状态栏高度
    private int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    //当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
    public interface OnChanageListener {
        public void onChange(int from, int to);
    }
}
