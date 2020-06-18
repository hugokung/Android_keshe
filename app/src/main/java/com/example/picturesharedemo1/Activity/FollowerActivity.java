package com.example.picturesharedemo1.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.picturesharedemo.R;
import com.example.picturesharedemo1.Bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class FollowerActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<User> userArrayList = new ArrayList();
    private ListAdapter listAdapter;
    private int flag;
    User author =new User();
    private String oAutherID;
    SwipeRefreshLayout layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);

        Bmob.initialize(FollowerActivity.this, "8369765908b5b24d951f7a13eb151240");
        listView=findViewById(R.id.lvFollower);
        layout =findViewById(R.id.refresh);

        Intent intent=getIntent();
        oAutherID=intent.getStringExtra("id");
       //Toast.makeText(FollowerActivity.this, "该用户为"+oAutherID, Toast.LENGTH_SHORT).show();

        //加载粉丝信息
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        User currentUser = BmobUser.getCurrentUser(User.class);
        //如果未传入ID，即查看的是本人的内容，则将ID赋值为currentUserID
        if (TextUtils.isEmpty(oAutherID)){
            oAutherID= currentUser.getObjectId();
        }
        User user = new User();
        user.setObjectId(currentUser.getObjectId());
        userBmobQuery.addWhereEqualTo("focusId", oAutherID);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    for(int i=0;i< list.size();i++){
                        User foUser = list.get(i);
                        userArrayList.add(foUser);
                        listAdapter= new ListAdapter(FollowerActivity.this, list);
                        listView.setAdapter(listAdapter);
                    }
                }
                else {
                    Toast.makeText(FollowerActivity.this, "获取失败1"+e, Toast.LENGTH_SHORT).show();
                }
            }

        });
        refresh();//刷新界面
    }

    private void refresh() {

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //加载粉丝信息
                BmobQuery<User> userBmobQuery = new BmobQuery<>();
                User currentUser = BmobUser.getCurrentUser(User.class);
                //如果未传入ID，即查看的是本人的内容，则将ID赋值为currentUserID
                if (TextUtils.isEmpty(oAutherID)){
                    oAutherID= currentUser.getObjectId();
                }
                User user = new User();
                user.setObjectId(currentUser.getObjectId());
                userBmobQuery.addWhereEqualTo("focusId", oAutherID);
                userBmobQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if(e==null){
                            for(int i=0;i< list.size();i++){
                                User foUser = list.get(i);
                                userArrayList.add(foUser);
                                listAdapter= new ListAdapter(FollowerActivity.this, list);
                                listView.setAdapter(listAdapter);
                            }
                        }
                        else {
                            Toast.makeText(FollowerActivity.this, "获取失败1"+e, Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                layout.setRefreshing(false);
            }
        });
    }


    public class ViewHolder {
        ImageView head_pic;
        TextView name;
        Button follower;

    }
    public class ListAdapter extends BaseAdapter {
        private Context context;
        private List<User> list;

        public ListAdapter(Context context, List<User> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Bmob.initialize(context, "8369765908b5b24d951f7a13eb151240");

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item, null);//实例化一个布局文件

                ViewHolder holder = new ViewHolder();
                holder.head_pic=convertView.findViewById(R.id.head);
                holder.name=convertView.findViewById(R.id.tv_name);
                holder.follower=convertView.findViewById(R.id.bt_follow);
                convertView.setTag(holder);
            }

            User poUser =list.get(position);
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            Glide.with(context).load(poUser.getAvatar()
                    .getUrl()).into(viewHolder.head_pic);
            if(TextUtils.isEmpty(poUser.getNickname())){
                viewHolder.name.setText(poUser.getUsername());
            }else{
                viewHolder.name.setText(poUser.getNickname());
            }

            //加载当前用户的关注列表
            BmobQuery<User> userBmobQuery1 = new BmobQuery<>();
            User currentUser = BmobUser.getCurrentUser(User.class);
            userBmobQuery1.addWhereRelatedTo("focusId",new BmobPointer(currentUser));
            userBmobQuery1.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if(e==null){
                        //判断当前用户的关注
                        for (int i=0;i<list.size();i++){
                            if (list.get(i).getObjectId().equals(poUser.getObjectId())){
                                //如果点击用户=关注列表之一，显示已关注
                                viewHolder.follower.setText("已关注");
                                flag=1;
                                break;
                            }else{
                                viewHolder.follower.setText("关注");
                                flag=0;
                            }
                        }
                        // Toast.makeText(OthersInfoActivity.this,"当前用户关注："+list.size()+"人", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("bmob","获取关注人数失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });

            viewHolder.follower.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (flag==0){
                        viewHolder.follower.setText("已关注");
                        flag=1;
                        User newuser = new User();
                        BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                        String Id = bu.getObjectId();
                        BmobRelation relation = new BmobRelation();

                        author.setObjectId(poUser.getObjectId());
                        relation.add(author);
                        newuser.setObjectId(Id);
                        newuser.setFocusId(relation);
                        newuser.update(Id,new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(FollowerActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                                    Log.i("已收到", "关注author："+author);

                                }else{
                                    Toast.makeText(FollowerActivity.this,"关注失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else if (flag==1){
                        viewHolder.follower.setText("关注");
                        flag=0;
                        User newuser = new User();
                        BmobUser bu = BmobUser.getCurrentUser(BmobUser.class);
                        String Id = bu.getObjectId();
                        BmobRelation relation = new BmobRelation();

                        author.setObjectId(poUser.getObjectId());
                        relation.remove(author);
                        newuser.setObjectId(Id);
                        newuser.setFocusId(relation);
                        newuser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(FollowerActivity.this,"取消关注成功",Toast.LENGTH_SHORT).show();
                                    Log.i("已收到", "取消关注author："+author);
                                }else{
                                    Toast.makeText(FollowerActivity.this,"取消关注失败",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(FollowerActivity.this,"关注状态异常",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }

    }

}
