package com.lee.photo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SelectImageActivity extends BaseActivity {
    private static final int TAKE_PHOTO = 11;
    private static final int CHOOSE_PHOTO = 22;
    private static final int CROP_PHOTO = 33;
    private ImageView imageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAtyTitle("SelectImage");
        setContent(R.layout.activity_select_single_img);

        imageView = (ImageView) findViewById(R.id.img_img);
    }


    public void takePhoto(View v){
        // new一个File用来存放拍摄到的照片
        // 通过getExternalStorageDirectory方法获得手机系统的外部存储地址
        File imageFile = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");
        // 如果存在就删了重新创建
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将存储地址转化成uri对象
        if(Build.VERSION.SDK_INT >= 24){ //7.0及其以上系统用的是Content Uri
            // 中间的参数 authority 需要跟Manifest文件provider标签里的authorities值一样.
            imageUri = FileProvider.getUriForFile(this,"com.lee.justdoit.fileprovider", imageFile);
        }else {//File Uri
            imageUri = Uri.fromFile(imageFile);
        }
        // 设置打开照相的Intent
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 设置相片的输出uri为刚才转化的imageUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //这里加入flag
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 开启相机程序，设置requestCode为TOKE_PHOTO
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void selectImage(View v){
        requestReadExternalPermission();
    }

    private void selectImage(){
        //同样new一个file用于存放照片
        File imageFile = new File(Environment.getExternalStorageDirectory(), "outputImage.jpg");
        if (imageFile.exists()) {
            imageFile.delete();
        }
        try {
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转换成File Uri
        imageUri = Uri.fromFile(imageFile);
        //开启选择内容界面
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        if(Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getImageContentUri(imageFile),"image/*");
        }else{
            intent.setType("image/*");
        }
        //添加读取URI权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //设置可以缩放
        intent.putExtra("scale", true);
        //设置可以裁剪
        intent.putExtra("crop", true);
//        intent.putExtra("aspectX", 320);
//        intent.putExtra("aspectY", 320);
//        intent.putExtra("outputX", 320);
//        intent.putExtra("outputY", 320);
        //设置输出位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //开始选择
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    /* 转换 content:// uri */
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //运行时权限判断
    @SuppressLint("NewApi")
    private void requestReadExternalPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //permission IS NOT granted.
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                // 0 是自己定义的请求code
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }else{
            selectImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    selectImage();
                } else {
                    Toast.makeText(SelectImageActivity.this,"No related permission",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == TAKE_PHOTO){
                // 设置intent为启动裁剪程序
                Intent intent = new Intent("com.android.camera.action.CROP");
                // 设置Data为刚才的imageUri和Type为图片类型
                intent.setDataAndType(imageUri, "image/*");
                // 设置可缩放
                intent.putExtra("scale", true);
                // 设置输出地址为imageUri
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 开启裁剪,设置requestCode为CROP_PHOTO
                startActivityForResult(intent, CROP_PHOTO);
            }else if(requestCode == CHOOSE_PHOTO){
                handleImageOnKitkat(data);
            }else if(requestCode == CROP_PHOTO){
                Bitmap bitmap;
                try {
                    //通过BitmapFactory将imageUri转化成bitmap
                    bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(imageUri));
                    //设置显示
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri
                    .getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri
                    .getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果不是document类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath); // 根据图片路径显示图片
        System.err.println(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
