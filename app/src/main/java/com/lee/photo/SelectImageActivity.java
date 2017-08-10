package com.lee.photo;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;

import java.io.File;

public class SelectImageActivity extends BaseActivity {
    private ImageView imageView;
    // 拍照成功，读取相册成功，裁减成功
    private final int  ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK = 3;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAtyTitle("SelectImage");
        setContent(R.layout.activity_select_single_img);

        imageView = (ImageView) findViewById(R.id.img_img);
        // new一个File用来存放拍摄到的照片
        // 通过getExternalStorageDirectory方法获得手机系统的外部存储地址
        // 定义拍照后存放图片的文件位置和名称，使用完毕后可以方便删除
        file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        if(file.exists()) {
            file.delete();// 清空之前的文件
        }

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(getUri(file),"image/*");
                startActivity(i);
            }
        });
    }


    public void takePhoto(View v){
        //这里被注掉的，是在6.0中进行权限判断的，大家可以根据情况，自行加上
                /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123);
                    Log.e("Album","我没有权限啊");
                }else {

                    Log.e("Album","我有权限啊");
                }*/

        // 来自相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
        //这里加入flag
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, CAMERA_OK);// CAMERA_OK是用作判断返回结果的标识
    }

    private Uri getUri(File file){
        if(Build.VERSION.SDK_INT >= 24){ //7.0及其以上系统用的是Content Uri
            // 中间的参数 authority 需要跟Manifest文件provider标签里的authorities值一样.
            return FileProvider.getUriForFile(this,"com.lee.justdoit.fileprovider", file);
        }else {//File Uri
            return Uri.fromFile(file);
        }
    }

    public void selectImage(View v){
        // 来自相册
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        /**
         * 下面这句话，与其它方式写是一样的效果，如果：
         * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         * intent.setType(""image/*");设置数据类型
         * 要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
         */
        //添加读取URI权限
        albumIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_OK);
//        requestReadExternalPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode = " + requestCode);
        switch (requestCode) {
            // 如果是直接从相册获取
            case ALBUM_OK:
                //从相册中获取到图片了，才执行裁剪动作
                if (data != null) {
                    clipPhoto(data.getData(),ALBUM_OK);
                    //setPicToView(data);
                }
                break;
            // 如果是调用相机拍照时
            case CAMERA_OK:
                // 当拍照到照片时进行裁减，否则不执行操作
                if (file.exists()) {
//                    clipPhoto(Uri.fromFile(file),CAMERA_OK);//开始裁减图片
                    clipPhoto(getUri(file),CAMERA_OK);//开始裁减图片
                }
                break;
            // 取得裁剪后的图片，这里将其设置到imageview中
            case CUT_OK:
                /**
                 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，
                 * 要重新裁剪，丢弃 当前功能时，会报NullException
                 */
                if (data != null) {
                    setPicToView(data);
                }else
                {
                    Log.e("Main","data为空");
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     * @param uri          图片uri
     * @param type         类别：相机，相册
     */
    public void clipPhoto(Uri uri,int type) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent, CUT_OK);
    }

    /**
     * 保存裁剪之后的图片数据 将图片设置到imageview中
     *
     * @param picdata　　　　　　　　　　资源
     */
    private void setPicToView(Intent picdata) {
        try
        {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picdata.getData()));
            imageView.setImageBitmap(bitmap);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

//    /* 转换 content:// uri */
//    public Uri getImageContentUri(File imageFile) {
//        String filePath = imageFile.getAbsolutePath();
//        Cursor cursor = getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { MediaStore.Images.Media._ID },
//                MediaStore.Images.Media.DATA + "=? ",
//                new String[] { filePath }, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int id = cursor.getInt(cursor
//                    .getColumnIndex(MediaStore.MediaColumns._ID));
//            Uri baseUri = Uri.parse("content://media/external/images/media");
//            return Uri.withAppendedPath(baseUri, "" + id);
//        } else {
//            if (imageFile.exists()) {
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.DATA, filePath);
//                return getContentResolver().insert(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            } else {
//                return null;
//            }
//        }
//    }

//    //运行时权限判断
//    @SuppressLint("NewApi")
//    private void requestReadExternalPermission() {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            //permission IS NOT granted.
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            } else {
//                // 0 是自己定义的请求code
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            }
//        }else{
//            selectImage();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 0: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted
//                    selectImage();
//                } else {
//                    Toast.makeText(SelectImageActivity.this,"No related permission",Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//            default:
//                break;
//        }
//    }


//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private void handleImageOnKitkat(Intent data) {
//        String imagePath = null;
//        Uri uri = data.getData();
//        if (DocumentsContract.isDocumentUri(this, uri)) {
//            // 如果是document类型的Uri，则通过document id处理
//            String docId = DocumentsContract.getDocumentId(uri);
//            if ("com.android.providers.media.documents".equals(uri
//                    .getAuthority())) {
//                String id = docId.split(":")[1]; // 解析出数字格式的id
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = getImagePath(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
//            } else if ("com.android.providers.downloads.documents".equals(uri
//                    .getAuthority())) {
//                Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"),
//                        Long.valueOf(docId));
//                imagePath = getImagePath(contentUri, null);
//            }
//        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // 如果不是document类型的Uri，则使用普通方式处理
//            imagePath = getImagePath(uri, null);
//        }
//        displayImage(imagePath); // 根据图片路径显示图片
//        System.err.println(imagePath);
//    }
//
//    private String getImagePath(Uri uri, String selection) {
//        String path = null;
//        // 通过uri和selection来获取真实的图片路径
//        Cursor cursor = getContentResolver().query(uri, null, selection, null,
//                null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            }
//            cursor.close();
//        }
//        return path;
//    }
//
//    private void displayImage(String imagePath) {
//        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            imageView.setImageBitmap(bitmap);
//        } else {
//            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
//        }
//    }
}
