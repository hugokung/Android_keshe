package com.example.picturesharedemo1.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;
import com.example.picturesharedemo1.Fragment.FragmentMy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.Objects;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.example.picturesharedemo1.Activity.PostDetail.saveImageToGallery;

public class OthersInfoActivity extends AppCompatActivity {
    private TextView nickname,selfintroduction,postnum,followeenum,followernum,title,postname;
    private Button follow;
    SimpleDraweeView draweeView;
    SwipeRefreshLayout layout;
    GridLayout gridLayout;

    private int flag;
    private String postId;
    Post cur_post = new Post();
    User author =new User();
    private ImageView[] img;
    private String authorID;

    AlertDialog dialog;
    private ImageView image;
    private View imgEntryView;
    private Bitmap[] bitmap ;

    private FragmentManager fmanager;
    private FragmentTransaction ftransaction;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);//需要在setContentView之前初始化
        this.setContentView(R.layout.activity_otherinfo);

        Intent in = getIntent();
        postId = in.getStringExtra("postId");

        Bmob.initialize(this,"8369765908b5b24d951f7a13eb151240");
        Log.i("已收到", "传入ID："+postId);

        //信息初始化-未编辑过的为空白
        initViews();
        //加载个人信息
        initMyinfo();
        //点击事件
        clickevent();

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gridLayout.removeAllViews();//初始化网格布局为空
                refresh();
            }
        });

    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //信息初始化-未编辑过的为空白
                        initViews();
                        //加载个人信息
                        initMyinfo();
                        //点击事件
                        clickevent();
                        layout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void clickevent() {
        follow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (flag==0){
                    follow.setText("已关注");
                    flag=1;
                    User newuser = new User();
                    BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                    String Id = bu.getObjectId();
                    BmobRelation relation = new BmobRelation();
                    relation.add(author);
                    newuser.setObjectId(Id);
                    newuser.setFocusId(relation);
                    newuser.update(Id,new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(OthersInfoActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                                Log.i("已收到", "关注author："+author);

                            }else{
                                Toast.makeText(OthersInfoActivity.this,"关注失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if (flag==1){
                    follow.setText("关注");
                    flag=0;
                    User newuser = new User();
                    BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                    String Id = bu.getObjectId();
                    BmobRelation relation = new BmobRelation();
                    relation.remove(author);
                    newuser.setObjectId(Id);
                    newuser.setFocusId(relation);
                    newuser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Toast.makeText(OthersInfoActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
                                Log.i("已收到", "取消关注author："+author);
                            }else{
                                Toast.makeText(OthersInfoActivity.this,"取消关注失败",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(OthersInfoActivity.this,"关注状态异常",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TA的关注
        followeenum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(OthersInfoActivity.this,FolloweeActivity.class);
                intent.putExtra("id", authorID);
                startActivity(intent);
            }
        });
        //TA的粉丝
        followernum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(OthersInfoActivity.this, FollowerActivity.class);
                intent.putExtra("id", authorID);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        nickname=findViewById(R.id.tv_nickname);
        selfintroduction=findViewById(R.id.tv_selfIntroduction);
        draweeView = findViewById(R.id.drawee_img);
        postnum=findViewById(R.id.tv_postnum);
        followeenum=findViewById(R.id.tv_followeenum);
        followernum=findViewById(R.id.tv_followernum);
        follow=findViewById(R.id.bt_follow);
        title=findViewById(R.id.tv_title);
        postname=findViewById(R.id.postname);
        layout =findViewById(R.id.refresh);
        gridLayout=findViewById(R.id.layout);
        //显示加号空白头像
        draweeView.setImageURI("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1385058234,3576270783&fm=26&gp=0.jpg");

        LayoutInflater inflater1 = LayoutInflater.from(OthersInfoActivity.this);
        imgEntryView = inflater1.inflate(R.layout.dialog_pic, null);
        dialog = new AlertDialog.Builder(OthersInfoActivity.this).create();
        image = imgEntryView.findViewById(R.id.large_Pic);

        postnum.setText("0");
        followernum.setText("0");
        followeenum.setText("0");
    }

    private void initMyinfo() {
        HandlerThread handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        MyHandler handler = new MyHandler(handlerThread.getLooper());

        //获取发布者ID
        BmobQuery<Post> poQuery = new BmobQuery<Post>();
        poQuery.include("author");
        poQuery.getObject(postId,new QueryListener<Post>(){
            @Override
            public void done(Post post, BmobException e) {
                if (e==null)
                {   cur_post = post;
                    Message msg = handler.obtainMessage();
                    //利用bundle对象来传值
                    Bundle bundle = new Bundle();
                    bundle.putString("authorID",post.getAuthor().getObjectId());
                    msg.setData(bundle);
                    msg.sendToTarget();
                    /*   author = post.getAuthor();
                         authorId = author.getObjectId();
                         Log.i("已收到", "传入author："+author);
                         Log.i("已收到", "传入postID："+postId);*/
                   // Log.i("已收到1", "传入bundle authorID："+post.getAuthor().getObjectId());
                }
                else{
                    Toast.makeText(OthersInfoActivity.this,"传入失败",Toast.LENGTH_SHORT).show();
                    Log.e("传入发布者ID失败", "原因："+e);
                }

            }
        });

    }

    class MyHandler extends Handler {

        public MyHandler() {
            super();
        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取bundle对象的值
            Bundle b = msg.getData();
            authorID = b.getString("authorID");
            Log.i("已收到", "bundle传出authorID："+authorID);

           /* User currentUser = BmobUser.getCurrentUser(User.class);
            if (authorID.equals(currentUser.getObjectId())){
                Toast.makeText(OthersInfoActivity.this,"ID相同",Toast.LENGTH_SHORT).show();
                onTabSelected(1);

            }else{
                initMyinfo1(authorID);
            }*/

            initMyinfo1(authorID);
        }
    }
    private void initMyinfo1(String authorId){

        //加载当前用户的关注列表
        BmobQuery<User> userBmobQuery1 = new BmobQuery<>();
        User currentUser = BmobUser.getCurrentUser(User.class);
        userBmobQuery1.addWhereRelatedTo("focusId",new BmobPointer(currentUser));
        userBmobQuery1.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    //判断当前用户是否关注点击用户
                    for (int i=0;i<list.size();i++){
                        if (list.get(i).getObjectId().equals(author.getObjectId())){
                            //关注列表之一=点击用户，显示已关注
                            follow.setText("已关注");
                            flag=1;
                            break;
                        }else{
                            follow.setText("关注");
                            flag=0;
                        }
                    }
                   // Toast.makeText(OthersInfoActivity.this,"当前用户关注："+list.size()+"人", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("bmob","获取关注人数失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        //根据authorId查找author
        author.setObjectId(authorId);
        Log.i("bmob","传入ID为："+authorId);
        //加载点击用户的个人信息
        BmobQuery<User> userBmobQuery2 = new BmobQuery<>();
        userBmobQuery2.getObject(authorId, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    draweeView.setImageURI(user.getAvatar().getUrl());
                    nickname.setText(user.getNickname());
                    selfintroduction.setText(user.getSelfintroduction());

                    if(TextUtils.isEmpty(user.getNickname())){
                        title.setText(user.getUsername()+"的主页");
                        postname.setText(user.getUsername()+"的相册");
                    }else {
                        title.setText(user.getNickname()+"的主页");
                        postname.setText(user.getNickname()+"的相册");
                    }
                    //Toast.makeText(getActivity(),"加载成功"+user.getAvatar().getUrl(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(OthersInfoActivity.this,"加载个人信息失败",Toast.LENGTH_SHORT).show();
                    Log.e("添加失败1","原因",e);
                }
            }
        });
        //加载点击用户的关注总数
        BmobQuery<User> userBmobQuery3 = new BmobQuery<>();
        userBmobQuery3.addWhereRelatedTo("focusId",new BmobPointer(author));
        userBmobQuery3.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    followeenum.setText(list.size()+"");
                    Log.i("bmob","关注人数："+list.size());
                }else{
                    Log.i("bmob","获取关注人数失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        //加载点击用户粉丝总数
        BmobQuery<User> userBmobQuery4 = new BmobQuery<User>();
        userBmobQuery4.addWhereEqualTo("focusId",authorId);
        userBmobQuery4.count(User.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if(e==null){
                    followernum.setText(integer.toString());
                  //  Toast.makeText(OthersInfoActivity.this,"当前粉丝："+integer+"人", Toast.LENGTH_SHORT).show();
                    Log.i("bmob","粉丝人数："+integer);
                }else{
                    Log.i("bmob","获取粉丝人数失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

        //显示发布用户所有发布照片

        BmobQuery<Post> postBmobQuery1 = new BmobQuery<>();
        postBmobQuery1.addWhereEqualTo("author",author);
        postBmobQuery1.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e==null){
                    postnum.setText(list.size()+"");//显示发布总数
                    img=new ImageView[list.size()];
                    bitmap = new Bitmap[list.size()];

                    for(int i=0;i<list.size();i++) {
                        img[i] = new ImageView(OthersInfoActivity.this);
                        //通过glide框架显示图片到imageview
                        Glide.with(OthersInfoActivity.this).load(list.get(i).getImage().getUrl())
                                .placeholder(R.drawable.load_fail)
                                .error(R.drawable.load_fail
                                ).into(img[i]);

                        //转为bitmap，便于后面保存
                        int k=i;
                        Glide.with(OthersInfoActivity.this).asBitmap()
                                .load(list.get(i).getImage().getUrl())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        bitmap[k] = resource;
                                    }
                                });

                        img[i].setPadding(2, 2, 2, 2);
                        //图片宽度自适应为屏幕1/3
                        WindowManager windowManager = (WindowManager) OthersInfoActivity.this.getSystemService(Context.WINDOW_SERVICE);
                        Point point = new Point();
                        windowManager.getDefaultDisplay().getSize(point); //将获取到的屏幕的宽度，放到point中
                        int itemWidth = point.x / 3;
                        //显示图片
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(itemWidth, itemWidth);
                        img[i].setScaleType(ImageView.ScaleType.FIT_XY);
                        img[i].setLayoutParams(params);
                        gridLayout.addView(img[i]);
                        //点击查看大图并保存
                        int j=i;//i是外部变量，不能传递，转给j才可以传递
                        img[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View paramView) {
                                // 加载自定义的布局文件
                                Glide.with(OthersInfoActivity.this).load(list.get(j).getImage().getUrl()).into(image);
                                dialog.setView(imgEntryView); // 自定义dialog
                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();
                                // 点击大图关闭dialog
                                imgEntryView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View paramView) {
                                        dialog.cancel();
                                    }
                                });

                                //长按dialog保存图片到本地
                                imgEntryView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
                                        //弹出的“保存图片”的Dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(OthersInfoActivity.this);
                                        builder.setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        if (bitmap[j] != null) {
                                                            saveImageToGallery(OthersInfoActivity.this, bitmap[j]);
                                                        } else {
                                                            Toast.makeText(OthersInfoActivity.this, "bitmap为空", Toast.LENGTH_SHORT).show();
                                                        }
                                                }
                                            }
                                        });
                                        builder.show();
                                        return true;
                                    }
                                });
                            }
                        });

                    }
                }
                else{
                    Toast.makeText(OthersInfoActivity.this,"查询发布失败",Toast.LENGTH_SHORT).show();
                    Log.e("添加失败2","原因",e);
                }
            }
        });
    }
}
