package com.example.picturesharedemo1.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import java.io.File;
import java.io.FileOutputStream;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostDetail extends AppCompatActivity {

    private TextView share_content,nickname,time,likes,collects;
    private ImageView head_pic,share_pic,like_pic,collect_pic,back,download;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            nickname.setText(msg.getData().getString("nickname"));
            time.setText(msg.getData().getString("time"));
            share_content.setText(msg.getData().getString("content"));
            likes.setText(msg.getData().getString("likes"));
            collects.setText(msg.getData().getString("collects"));
            Glide.with(PostDetail.this)
                    .load(msg.getData().getString("img_url"))
                    .placeholder(R.drawable.image_loading)
                    .error(R.drawable.load_fail)
                    .into(PostDetail.this.share_pic);
             Glide.with(PostDetail.this)
                   .asBitmap()
                   .load(msg.getData().getString("img_url"))
                   .into(new SimpleTarget<Bitmap>() {
                       @Override
                       public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                           bitmap[0] = resource;
                       }
              });

            if(msg.getData().getString("head_url")!="null"){
                Glide.with(PostDetail.this)
                        .load(msg.getData().getString("head_url"))
                        .placeholder(R.drawable.image_loading)
                        .error(R.drawable.load_fail)
                        .into(PostDetail.this.head_pic);
            }
        }
    };

    String objectId;
    String like_sta,collect_sta;
    Post cur_post = new Post();
    BmobUser now_user = BmobUser.getCurrentUser(BmobUser.class);
    //保存图片
    //长按后显示的 Item
    final String[] items = new String[] { "保存图片"};
    //图片转成Bitmap数组
    final Bitmap[] bitmap = new Bitmap[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent in = getIntent();
        objectId = in.getStringExtra("id");
        like_sta = in.getStringExtra("like_sta");
        collect_sta = in.getStringExtra("collect_sta");
        Bmob.initialize(this,"8369765908b5b24d951f7a13eb151240");
        initView();
        initData();
        clickEvents();
    }

    private void clickEvents() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        like_pic.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String cur_objId = cur_post.getObjectId();
             if ((like_sta.equals("0") && like_pic.getTag().equals(0)) || (like_pic.getTag().equals(2))) {
                 Integer sum = cur_post.getLike_num();
                 sum = sum + 1;
                 likes.setText(sum.toString());
                 like_pic.setImageResource(R.drawable.like_fill);
                 cur_post.setLike_num(sum);
                 Post po = new Post();
                 po.increment("like_num");
                 po.update(cur_objId, new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
//                                Toast.makeText(PostDetail.this, "ok", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "点赞数更新失败1", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 like_pic.setTag(1);
                 Post like_po = new Post();
                 like_po.setObjectId(cur_objId);
                 User me = new User();
                 me.setObjectId(now_user.getObjectId());
                 BmobRelation relation = new BmobRelation();
                 relation.add(like_po);
                 me.setUser_like(relation);
                 me.update(new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
                             Toast.makeText(PostDetail.this, "点赞成功", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "点赞失败", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             } else {
                 Integer sum = cur_post.getLike_num();
                 sum = sum - 1;
                 cur_post.setLike_num(sum);
                 likes.setText(sum.toString());
                 like_pic.setImageResource(R.drawable.like);
                 Post po = new Post();
                 po.increment("like_num", -1);
                 po.update(cur_objId, new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
//                                Toast.makeText(PostDetail.this, "ok2", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "点赞数更新失败2", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 Post unlike_po = new Post();
                 unlike_po.setObjectId(cur_objId);
                 BmobRelation relation = new BmobRelation();
                 relation.remove(unlike_po);
                 User me = new User();
                 me.setObjectId(now_user.getObjectId());
                 me.setUser_like(relation);
                 me.update(new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
                             Toast.makeText(PostDetail.this, "取消点赞成功", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "取消失败" + e, Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 like_pic.setTag(2);
             }
         }
     });
        collect_pic.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String cur_objId = cur_post.getObjectId();
             if ((collect_sta.equals("0") && collect_pic.getTag().equals(0)) || (collect_pic.getTag().equals(2))) {
                 Integer sum = cur_post.getCollection_num();
                 sum = sum + 1;
                 collects.setText(sum.toString());
                 collect_pic.setImageResource(R.drawable.collection_fill);
                 cur_post.setCollection_num(sum);
                 Post po = new Post();
                 po.increment("collection_num");
                 po.update(cur_objId, new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
//                                Toast.makeText(PostDetail.this, "ok", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "收藏数更新失败1", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 collect_pic.setTag(1);
                 Post collect_po = new Post();
                 collect_po.setObjectId(cur_objId);
                 User me = new User();
                 me.setObjectId(now_user.getObjectId());
                 BmobRelation relation = new BmobRelation();
                 relation.add(collect_po);
                 me.setUser_collect(relation);
                 me.update(new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
                             Toast.makeText(PostDetail.this, "收藏成功", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "收藏失败", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             } else {
                 Integer sum = cur_post.getCollection_num();
                 sum = sum - 1;
                 cur_post.setCollection_num(sum);
                 collects.setText(sum.toString());
                 like_pic.setImageResource(R.drawable.collection);
                 Post po = new Post();
                 po.increment("collection_num", -1);
                 po.update(cur_objId, new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
//                                Toast.makeText(PostDetail.this, "ok2", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "收藏数更新失败2", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 Post uncollect_po = new Post();
                 uncollect_po.setObjectId(cur_objId);
                 BmobRelation relation = new BmobRelation();
                 relation.remove(uncollect_po);
                 User me = new User();
                 me.setObjectId(now_user.getObjectId());
                 me.setUser_collect(relation);
                 me.update(new UpdateListener() {
                     @Override
                     public void done(BmobException e) {
                         if (e == null) {
                             Toast.makeText(PostDetail.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                         } else {
                             Toast.makeText(PostDetail.this, "取消失败" + e, Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 collect_pic.setTag(2);
             }
         }
     });
        head_pic.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

         }
     });

        share_pic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //弹出的“保存图片”的Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetail.this);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (bitmap[0] != null) {
                                    saveImageToGallery(PostDetail.this, bitmap[0]);
                                } else {
                                    Toast.makeText(PostDetail.this, "bitmap为空", Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        }
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片 创建文件夹
       File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //图片文件名称
        String fileName = "shy_"+System.currentTimeMillis() + ".jpg";
        File file = new File(appDir,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("111",e.getMessage());
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
       /* String path = file.getAbsolutePath();
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
        } catch (FileNotFoundException e) {
            Log.e("333",e.getMessage());
            e.printStackTrace();
        }*/
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
    }


    private void initData() {
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.include("author");
        new Thread(new Runnable() {
            @Override
            public void run() {
                query.getObject(objectId, new QueryListener<Post>() {
                    @Override
                    public void done(Post post, BmobException e) {
                        if(e==null){
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            //有昵称则显示昵称，无昵称显示username
                            if(("").equals(post.getAuthor().getNickname())){
                                bundle.putString("nickname",post.getAuthor().getUsername());
                            }else {
                                bundle.putString("nickname",post.getAuthor().getNickname());
                            }

                            bundle.putString("time",post.getCreatedAt());
                            bundle.putString("content",post.getContent());
                            bundle.putString("likes",post.getLike_num().toString());
                            bundle.putString("collects",post.getCollection_num().toString());
                            BmobFile post_img = post.getImage();
                            String post_img_url = post_img.getUrl();
                            bundle.putString("img_url",post_img_url);
                            BmobFile user_head = post.getAuthor().getAvatar();
                            String user_head_url="null";
                            if(user_head!=null){
                                user_head_url = user_head.getUrl();
                                bundle.putString("head_url",user_head_url);
                            }
                            else bundle.putString("head_url",user_head_url);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                        else {
                            Toast.makeText(PostDetail.this, "获取失败"+e, Toast.LENGTH_SHORT).show();
                            Log.e("获取detail失败","原因",e);
                        }
                    }
                });
            }
        }).start();
    }

    private void initView() {
        share_content = findViewById(R.id.detail_share_content);
        nickname = findViewById(R.id.detail_user_id_name);
        time = findViewById(R.id.detail_post_time);
        likes = findViewById(R.id.detail_like_sum);
        collects = findViewById(R.id.detail_collection_sum);
        head_pic = findViewById(R.id.detail_user_head);
        share_pic = findViewById(R.id.detail_share_image);
        like_pic = findViewById(R.id.detail_user_like);
        collect_pic = findViewById(R.id.detail_user_collection);
        back = findViewById(R.id.detail_back);
        like_pic.setTag(0);
        collect_pic.setTag(0);
        if(like_sta.equals("1")){
            like_pic.setImageResource(R.drawable.like_fill);
        }
        if(collect_sta.equals("1")){
            collect_pic.setImageResource(R.drawable.collection_fill);
        }
    }
}

