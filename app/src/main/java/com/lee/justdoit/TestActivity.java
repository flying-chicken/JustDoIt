package com.lee.justdoit;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017-04-13.
 */

public class TestActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setAtyTitle("TestHH");
//        setAppBarHeight(256);

        setContent(R.layout.content_base);

        setAppbarImage(R.mipmap.header);
        setFAButtonAnchor(R.id.base_appbarlayout, Gravity.BOTTOM|Gravity.END|Gravity.RIGHT);
    }
}