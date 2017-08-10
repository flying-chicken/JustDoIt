package com.lee.photo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;

import java.util.ArrayList;
import java.util.List;

public class SelectMultipleImageActivity extends BaseActivity {
    private List<String> imagePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAtyTitle("SelectMultipleImage");
        setContent(R.layout.activity_select_multiple_image);
    }

    @Override
    protected void initParams() {
        super.initParams();
        bShowActionButton = false;
    }

    private void getImagesPath() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = getContentResolver();
        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePaths.add(path);
        }
    }
}
