package com.example.picturesharedemo1.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.picturesharedemo1.Activity.FolloweeActivity;
//import com.example.picturesharedemo1.Activity.FollowerActivity;
import com.example.picturesharedemo1.Activity.FollowerActivity;
import com.example.picturesharedemo1.Activity.LoginActivity;
import com.example.picturesharedemo1.Activity.My_EditInfoActivity;
import com.example.picturesharedemo1.Adapter.MyFragmentPagerAdapter;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

import static android.content.DialogInterface.BUTTON_NEGATIVE;


public class FragmentMy extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private AppBarLayout appBarLayout;


    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;


    private TextView nickname,selfintroduction,postnum,followeenum,followernum,logout;
    private Button editMyinfo;

    SimpleDraweeView draweeView;
    SwipeRefreshLayout layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(getActivity());//需要在setContentView之前初始化
        View view=inflater.inflate(R.layout.fragment_my,container,false);


        Bmob.initialize(getActivity(), "8369765908b5b24d951f7a13eb151240");
        nickname=view.findViewById(R.id.tv_nickname);
        selfintroduction=view.findViewById(R.id.tv_selfIntroduction);
        draweeView = view.findViewById(R.id.drawee_img);
        mViewPager= view.findViewById(R.id.viewPager);
        mTabLayout = view.findViewById(R.id.tabLayout);
        postnum=view.findViewById(R.id.tv_postnum);
        followeenum=view.findViewById(R.id.tv_followeenum);
        followernum=view.findViewById(R.id.tv_followernum);
        layout =view.findViewById(R.id.refresh);
        editMyinfo=view.findViewById(R.id.bt_editMyinfo);
        logout=view.findViewById(R.id.logout);
        appBarLayout=view.findViewById(R.id.appbar);

        //信息初始化-未编辑过的为空白
        initViews();
        //加载个人信息
        initMyinfo();
       //刷新
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //信息初始化-未编辑过的为空白
                initViews();
                //加载个人信息
                initMyinfo();
                layout.setRefreshing(false);
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset >=0) {
                    layout.setEnabled(true);
                }else {
                    layout.setEnabled(false);
                }
            }
        });
        //点击事件
        clickEvents();
        return view;
    }
    private void clickEvents() {
        //编辑个人资料
        editMyinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), My_EditInfoActivity.class);
                startActivity(intent);
            }
        });
        //我的发布
        postnum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), AlbumFragment.class);
                startActivity(intent);
            }
        });
        //我的关注
        followeenum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), FolloweeActivity.class);
                startActivity(intent);
            }
        });
        //我的粉丝
        followernum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), FollowerActivity.class);
                startActivity(intent);
            }
        });
        //点击退出登录
        //首先，监听对话框里面的button点击事件
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    case BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                        break;
                    default:
                        break;
                }
            }
        };
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 创建退出对话框
                AlertDialog isExit = new AlertDialog.Builder(getActivity()).create();
                // 设置对话框标题
                isExit.setTitle("提示");
                // 设置对话框消息
                isExit.setMessage("确定要退出吗，未保存的数据将会遗失");
                // 添加选择按钮并注册监听
                isExit.setButton(Dialog.BUTTON_POSITIVE,"确定", listener);
                isExit.setButton2("取消",listener);
                // 显示对话框
                isExit.show();
            }
       });


    }
    private void initViews() {
        //显示加号空白头像
        draweeView.setImageURI("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1385058234,3576270783&fm=26&gp=0.jpg");
        //使用适配器将ViewPager与Fragment绑定在一起
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout.setupWithViewPager(mViewPager);
        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);

        //设置Tab的图标
        one.setIcon(R.drawable.ic_view_module_black_24dp);
        two.setIcon(R.drawable.ic_favorite_border_black_24dp);
        three.setIcon(R.drawable.ic_turned_in_not_black_24dp);

        postnum.setText("0");
        followernum.setText("0");
        followeenum.setText("0");
    }

    private void initMyinfo() {
        //加载后端存储的个人信息
        User bu = BmobUser.getCurrentUser(User.class);
        String Id = bu.getObjectId();
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(Id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    draweeView.setImageURI(user.getAvatar().getUrl());
                    nickname.setText(user.getNickname());
                    selfintroduction.setText(user.getSelfintroduction());
                 //   Toast.makeText(getActivity(),"加载成功"+user.getAvatar().getUrl(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"加载个人信息失败",Toast.LENGTH_SHORT).show();
                    Log.e("添加失败","原因",e);
                }
            }
        });
       //加载关注总数
       BmobQuery<User> userBmobQuery2 = new BmobQuery<>();
       userBmobQuery2.addWhereRelatedTo("focusId",new BmobPointer(bu));
       userBmobQuery2.findObjects(new FindListener<User>() {
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

       //加载粉丝总数
       BmobQuery<User> userBmobQuery3 = new BmobQuery<User>();
       userBmobQuery3.addWhereEqualTo("focusId",Id);
       userBmobQuery3.count(User.class, new CountListener() {
           @Override
           public void done(Integer integer, BmobException e) {
               if(e==null){
                   followernum.setText(integer.toString());
                   Log.i("bmob","粉丝人数："+integer);
               }else{
                   Log.i("bmob","获取粉丝人数失败："+e.getMessage()+","+e.getErrorCode());
               }
           }
       });
       //获取当前用户发布总数
       BmobQuery<Post> bmobQuery1 = new BmobQuery<Post>();
       bmobQuery1.addWhereEqualTo("author",bu);
       bmobQuery1.count(Post.class, new CountListener() {
           @Override
           public void done(Integer integer, BmobException e) {
               if(e==null){
                   postnum.setText(integer.toString());
                   //Toast.makeText(getActivity(),"当前用户共发布："+integer+"个", Toast.LENGTH_SHORT).show();
               }else{
                   Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
               }
           }
       });
   }
}
