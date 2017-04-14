package com.lee.justdoit;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BaseActivity extends AppCompatActivity {
    protected  final int DEFAULT_TOOLBAR_HEIGHT = 56;
    protected boolean bShowActionButton;
    protected boolean bShowToolbar;

    protected CoordinatorLayout rootLayout;
    protected AppBarLayout appBarLayout;
    protected CollapsingToolbarLayout toolbarLayout;
    protected Toolbar toolbar;
    protected FloatingActionButton faButton;
    protected ImageView appbarImage;

//
//    /** 根据返回的值来设置AppbarLayout 的高度*/
//    protected int getAppBarheight(int dp){
//        if(dp < DEFAULT_TOOLBAR_HEIGHT) dp = DEFAULT_TOOLBAR_HEIGHT;
//        return dip2px(dp);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initParams();
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initParams(){
        bShowActionButton = true;
        bShowToolbar = true;
    }

    private void initViews(){
        rootLayout = (CoordinatorLayout) findViewById(R.id.base_coordinatorlayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.base_appbarlayout);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.base_ctblayout);
        toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        toolbar.setBackgroundResource(R.color.colorPrimary);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!bShowToolbar){
            toolbarLayout.setVisibility(View.GONE);
        }
        appbarImage = (ImageView) findViewById(R.id.base_appbar_img);
        faButton = (FloatingActionButton) findViewById(R.id.base_fab);
    }

    public void setAppBarHeight(int dp){
        if(dp < DEFAULT_TOOLBAR_HEIGHT) {
            dp = DEFAULT_TOOLBAR_HEIGHT;
            toolbar.setBackgroundResource(R.color.colorPrimary);
        }else {
            toolbar.setBackgroundResource(android.R.color.transparent);
        }
        ViewGroup.LayoutParams p = appBarLayout.getLayoutParams();
        p.height = dip2px(dp);
        appBarLayout.setLayoutParams(p);
    }

    public void setAppbarImage(Drawable drawable){
        appbarImage.setImageDrawable(drawable);
    }

    public void setAppbarImage(int resId){
        appbarImage.setImageResource(resId);
    }

    public void setAppbarImage(Bitmap bitmap){
        appbarImage.setImageBitmap(bitmap);
    }

    public void setFAButtonAnchor(int targetId,int gravity){
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) faButton.getLayoutParams();
        lp.gravity = Gravity.NO_GRAVITY;
        lp.setMargins(0,0,(int)getResources().getDimension(R.dimen.fab_margin_right),0);
        lp.setAnchorId(targetId);
        lp.anchorGravity = gravity;
        faButton.setLayoutParams(lp);
    }

    public void setContent(int resId){
        rootLayout.addView(getLayoutInflater().inflate(resId,rootLayout,false),1);
    }

    public  void setAtyTitle(String title){
        appBarLayout.setExpanded(true,false);
        toolbarLayout.setTitle(title);
        toolbar.setTitle(title);

//        getSupportActionBar().setTitle(title);
    }

    public void setAtyTitle(int id){
        setAtyTitle(getResources().getString(id));
    }

    protected int getViewHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    public int dip2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

   public int px2dip( float px) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
