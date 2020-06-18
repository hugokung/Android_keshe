package com.example.picturesharedemo1.Activity;

/*import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import User;
import MyFragmentPagerAdapter;
import com.example.picturesharedemo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;


public class MineActivity extends AppCompatActivity  {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;

    private TextView nickname,username,selfintroduction;
    private Button editMyinfo;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);//需要在setContentView之前初始化
        setContentView(R.layout.fragment_my);

        Bmob.initialize(this, "35514037b1883d3a2964dbbc925fdb47");
        username=findViewById(R.id.tv_nickname);
        selfintroduction=findViewById(R.id.tv_selfIntroduction);
        //信息初始化
        initViews();

        //更新编辑过的个人资料
        editMyinfo=this.findViewById(R.id.bt_editMyinfo);
        editMyinfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MineActivity.this, My_EditInfoActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initViews() {
        SimpleDraweeView draweeView = findViewById(R.id.drawee_img);

        //显示头像
        draweeView.setImageURI("https://ss2.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3890587736,1624617238&fm=26&gp=0.jpg");

        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);

        //设置Tab的图标
        one.setIcon(R.drawable.ic_view_module_black_24dp);
        two.setIcon(R.drawable.ic_favorite_border_black_24dp);
        three.setIcon(R.drawable.ic_turned_in_not_black_24dp);

        //加载信息
        getMyinfo();
    }

    private void getMyinfo() {
        //添加个人信息数据-注册
      /*  User user = new User();
        user.setNickname("nancy");
        user.setSelfintroduction("个人简介：hello world！0515");

        user.setUsername("1700300607" );
        user.setPassword("123456" );*/
        //设置头像
      /*
            final String imagePath = getExternalCacheDir()+ "my_actr.ipg";
            final BmobFile avatar = new BmobFile(new File(imagePath));
            avatar.upload(new UploadFileListener(){
                @Override
                public void done(BmobException e) {
                    if (null == e) {
                        user.setAvatar(avatar);
                        //上传成功
                        Toast.makeText(MainActivity.this,"上传成功！！！" ,Toast.LENGTH_SHORT).show();
                    } else {
                        //上传失败
                        Toast.makeText(MainActivity.this,"上传失败" ,Toast.LENGTH_SHORT).show();
                    }
                }
            });*/

    /*    user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    Toast.makeText(MineActivity.this,"添加数据成功",Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(MainActivity.this,"添加数据失败" ,Toast.LENGTH_SHORT).show();
                    // Log.e("添加失败","原因",e);
                }
            }
        });*/

    /*    //加载个人信息
        BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
        String Id = bu.getObjectId();
        BmobQuery<User> bmobQuery = new BmobQuery<>();
       // bmobQuery.getObject("2f92598086", new QueryListener<User>() {
        bmobQuery.getObject(Id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    nickname.setText(user.getNickname());
                    selfintroduction.setText(user.getSelfintroduction());
                    Toast.makeText(MineActivity.this,"加载成功",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MineActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* public void loadInternetImage(View view, Context mContext){
               Resources r=mContext.getResources();
               Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                       + r.getResourcePackageName(R.drawable.ic_logo) + "/"
                       + r.getResourceTypeName(R.drawable.ic_logo) + "/"
                       + r.getResourceEntryName(R.drawable.ic_logo));
                 SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.drawee_img);
                 draweeView.setImageURI(uri);

     }
    // fresco:overlayImage="@drawable/my_actr"
   // @SuppressLint("WrongViewCast")

}*/