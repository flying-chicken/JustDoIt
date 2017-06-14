package com.lee.glide;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;

import java.io.IOException;

public class GlideActivity extends BaseActivity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setAtyTitle();
        setContent(R.layout.content_glide);

        image = (ImageView) findViewById(R.id.glide_image);
//        image.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//            }
//        });
        Glide.with(this).load("file:///android_asset/compnent.jpg").diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
    }

}
