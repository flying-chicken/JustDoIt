package com.lee.justdoit;

import android.content.res.TypedArray;
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

public class BaseActivity extends AppCompatActivity {
    protected  final int DEFAULT_TOOLBAR_HEIGHT = 56;
    protected boolean bShowActionButton;
    protected boolean bShowToolbar;

    protected CoordinatorLayout rootLayout;
    protected AppBarLayout appBarLayout;
    protected CollapsingToolbarLayout toolbarLayout;
    protected Toolbar toolbar;

    protected void setAppBarHeight(int dp){
        if(dp < DEFAULT_TOOLBAR_HEIGHT) dp = DEFAULT_TOOLBAR_HEIGHT;
        ViewGroup.LayoutParams p = appBarLayout.getLayoutParams();
        p.height = dip2px(dp);
        appBarLayout.setLayoutParams(p);
    }

    /** 根据返回的值来设置AppbarLayout 的高度*/
    protected int getAppBarheight(int dp){
        if(dp < DEFAULT_TOOLBAR_HEIGHT) dp = DEFAULT_TOOLBAR_HEIGHT;
        return dip2px(dp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        initParams();
        initViews();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

    protected void initViews(){
        rootLayout = (CoordinatorLayout) findViewById(R.id.base_coordinatorlayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.base_appbarlayout);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.base_ctblayout);
//        setAppBarHeight(getAppBarheight());
        toolbar = (Toolbar) findViewById(R.id.base_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!bShowToolbar){
            toolbarLayout.setVisibility(View.GONE);
        }
    }

    protected void setContent(int resId){
        rootLayout.addView(getLayoutInflater().inflate(resId,rootLayout,false),1);
    }

    public  void setAtyTitle(String title){
        toolbarLayout.setTitle(title);
        toolbar.setTitle(title);
        getSupportActionBar().setTitle(title);
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
