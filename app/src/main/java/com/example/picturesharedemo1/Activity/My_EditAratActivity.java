package com.example.picturesharedemo1.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class My_EditAratActivity extends AppCompatActivity {
    int REQUEST_SYSTEM_PIC=0;
    private Button mbtAlbum;
    SimpleDraweeView mivArat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);//需要在setContentView之前初始化
        this.setContentView(R.layout.activity_editavat);

        Bmob.initialize(My_EditAratActivity.this, "8369765908b5b24d951f7a13eb151240");
        mivArat=findViewById(R.id.my_img);
        initImg();//显示头像

        mbtAlbum=findViewById(R.id.btn_select);
        mbtAlbum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(My_EditAratActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(My_EditAratActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    openAlbum();
                }
            }
        });

    }

    private void initImg() {
        //加载后端存储的个人信息
        BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
        String Id = bu.getObjectId();
        // Toast.makeText(My_EditAratActivity.this,"ID="+Id,Toast.LENGTH_SHORT).show();
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    mivArat.setImageURI(user.getAvatar().getUrl());
                    //  Toast.makeText(My_EditAratActivity.this,"加载头像成功"+user.getAvatar().getUrl(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(My_EditAratActivity.this,"加载头像失败",Toast.LENGTH_SHORT).show();
                    Log.e("添加失败","原因",e);
                }
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SYSTEM_PIC);//打开系统相册

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SYSTEM_PIC && resultCode == RESULT_OK && null != data) {
            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitkat(data);
            } else {
                handleImageBeforeKitkat(data);
            }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片

    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
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
           // Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //mivArat.setImageBitmap(bitmap);
             mivArat.setImageURI(imagePath);
            upload(imagePath);
         //  initImg();
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void upload(String imagePath) {
        BmobFile avatar = new BmobFile(new File(imagePath));
        avatar.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                    String Id = bu.getObjectId();
                    user.setObjectId(Id);
                    user.setAvatar(avatar);
                    user.update(Id,new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(My_EditAratActivity.this, "添加图片到表成功"+avatar.getFileUrl(), Toast.LENGTH_SHORT).show();
                            } else {
                                 Toast.makeText(My_EditAratActivity.this, "加载失败1", Toast.LENGTH_SHORT).show();
                                Log.e("添加图片到表失败","原因1",e);
                            }
                        }
                    });
                    Toast.makeText(My_EditAratActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                    initImg();
                } else {
                    Toast.makeText(My_EditAratActivity.this, "加载失败2", Toast.LENGTH_SHORT).show();
                    Log.e("上传图像失败","原因2",e);
                }
            }
        });
    }
}
