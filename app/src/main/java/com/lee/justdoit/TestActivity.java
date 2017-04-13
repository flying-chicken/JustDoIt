package com.lee.justdoit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017-04-13.
 */

public class TestActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAtyTitle("TestHHHHH");
        setAppBarHeight(256);
        setContent(R.layout.content_base);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.test_fab);
//        ((ViewGroup)fab.getParent()).removeView(fab);
//        rootLayout.addView(fab);
    }

//    @Override
//    protected int getAppBarheight() {
//        return dip2px(256);
//    }
}
