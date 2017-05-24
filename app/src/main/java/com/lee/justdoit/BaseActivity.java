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
import android.support.design.widget.TabLayout;
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
    protected boolean bCollapseTollbar;
    protected boolean bShowTabLayout;

    protected CoordinatorLayout rootLayout;
    protected AppBarLayout appBarLayout;
    protected CollapsingToolbarLayout toolbarLayout;
    protected Toolbar toolbar;
    protected FloatingActionButton faButton;
    protected ImageView appbarImage;
    protected TabLayout tabLayout;


    /** 根据返回的值来设置AppbarLayout 的高度*/
    protected int getAppBarheight(){
//        if(dp < DEFAULT_TOOLBAR_HEIGHT) dp = DEFAULT_TOOLBAR_HEIGHT;
        return DEFAULT_TOOLBAR_HEIGHT;
    }

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
        bShowTabLayout = true;
    }

    private void initViews(){
        rootLayout = (CoordinatorLayout) findViewById(R.id.base_coordinatorlayout);
        initToolbar();
        initTabLayout();
        initFAB();
    }

    private void initToolbar(){
        int appbarH = getAppBarheight();
        if(appbarH > DEFAULT_TOOLBAR_HEIGHT){
            appBarLayout = (AppBarLayout) getLayoutInflater().inflate(R.layout.collapse_toolbar_layout,rootLayout,false);
            toolbarLayout = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.base_ctblayout);
            toolbar = (Toolbar) appBarLayout.findViewById(R.id.base_toolbar);
            appbarImage = (ImageView) appBarLayout.findViewById(R.id.base_appbar_img);
            setAppBarHeight(appbarH);
            bCollapseTollbar = true;
        }else{
            appBarLayout = (AppBarLayout) getLayoutInflater().inflate(R.layout.toolbar_layout,rootLayout,false);
            toolbar = (Toolbar) appBarLayout.findViewById(R.id.base_toolbar);
            bCollapseTollbar = false;
        }
        rootLayout.addView(appBarLayout,0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!bShowToolbar){
            if(!bShowTabLayout){
                appBarLayout.setVisibility(View.GONE);
            }else if(bCollapseTollbar){
                toolbarLayout.setVisibility(View.GONE);
            }else{
                toolbar.setVisibility(View.GONE);
            }
        }
    }

    private void initFAB(){
        faButton = (FloatingActionButton) findViewById(R.id.base_fab);
        if(!bShowActionButton)
            faButton.setVisibility(View.GONE);
    }

    private void initTabLayout(){
        tabLayout = (TabLayout) appBarLayout.findViewById(R.id.base_tablayout);
        if(!bShowTabLayout){
            tabLayout.setVisibility(View.GONE);
            return;
        }else{
            tabLayout.setVisibility(View.VISIBLE);
        }
        addTabs(3);
    }

    private void addTabs(int count){
        tabLayout.removeAllTabs();
        for(int i=1; i<= count; i++){
            tabLayout.addTab(tabLayout.newTab().setText("TAB"+i));
        }
    }

    private void setAppBarHeight(int dp){
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
        if(appbarImage == null || !bCollapseTollbar) return;
        appbarImage.setImageDrawable(drawable);
    }

    public void setAppbarImage(int resId){
        if(appbarImage == null || !bCollapseTollbar) return;
        appbarImage.setImageResource(resId);
    }

    public void setAppbarImage(Bitmap bitmap){
        if(appbarImage == null || !bCollapseTollbar) return;
        appbarImage.setImageBitmap(bitmap);
    }

    public void setFAButtonImage(Drawable drawable){
        faButton.setImageDrawable(drawable);
    }

    public void setFAButtonImage(Bitmap bitmap){
        faButton.setImageBitmap(bitmap);
    }

    public void setFAButtonImage(int resId){
        faButton.setImageResource(resId);
    }

    public void setFAButtonAnchor(int targetId,int gravity){
        if(targetId == R.id.base_appbarlayout && !bCollapseTollbar) return;
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) faButton.getLayoutParams();
        lp.gravity = Gravity.NO_GRAVITY;
        lp.setMargins(0,0,(int)getResources().getDimension(R.dimen.fab_margin_right),0);
        lp.setAnchorId(targetId);
        lp.anchorGravity = gravity;
        faButton.setLayoutParams(lp);
    }

    public void setFAButtonAnchor(int targetId){
        setFAButtonAnchor(targetId, Gravity.BOTTOM|Gravity.END|Gravity.RIGHT);
    }

    public void setContent(int resId){
        rootLayout.addView(getLayoutInflater().inflate(resId,rootLayout,false),1);
    }

    public  void setAtyTitle(String title){
        if(bCollapseTollbar)
            toolbarLayout.setTitle(title);
        else{
//            toolbar.setTitle(title);
            getSupportActionBar().setTitle(title);
        }
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
