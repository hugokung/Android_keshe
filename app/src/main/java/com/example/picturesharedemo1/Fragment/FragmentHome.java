package com.example.picturesharedemo1.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.picturesharedemo1.Activity.PostActivity;
import com.example.picturesharedemo1.Adapter.HomeAdapter;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class FragmentHome extends Fragment {

    private RecyclerView rv;
    private SwipeRefreshLayout srlayout;
    private TextView HelloHome;
    private List<Post> data= new ArrayList<>();
    private HomeAdapter homeAdapter;
    private FloatingActionButton add;

    private List<Post> like_data = new ArrayList<>();
    private List<Post> collect_data = new ArrayList<>();
    private List<String> focus_data = new ArrayList<>();

    private Handler handler;
    class My_handler extends Handler{
        @Override
        public void handleMessage(Message msg){
            if(msg.obj=="success"){
                homeAdapter = new HomeAdapter(getActivity(),data,like_data,collect_data);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.setAdapter(homeAdapter);
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        iniView();
        Bmob.initialize(getActivity(),"8369765908b5b24d951f7a13eb151240");
        Refresh();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });
        srlayout.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        srlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });

    }
    private void Refresh() {
        //先查询自己关注的人
        BmobQuery<User> query_follow = new BmobQuery<User>();
        BmobUser this_user = BmobUser.getCurrentUser(BmobUser.class);
        User user = new User();
        user.setObjectId(this_user.getObjectId());
        query_follow.addWhereRelatedTo("focusId",new BmobPointer(user));
        query_follow.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    Integer num = list.size();
                    focus_data.clear();
//                    Toast.makeText(getActivity(), "success!"+num.toString(), Toast.LENGTH_SHORT).show();
                    for(User t:list){

                        focus_data.add(t.getObjectId());
                    }
                    focus_data.add(this_user.getObjectId());
                    if(num==0){
                        Toast.makeText(getActivity(), "你还没有关注的人喔！", Toast.LENGTH_SHORT).show();
                        srlayout.setRefreshing(false);
                    }
                    else {
                        init_like_list();     //查询关注的人的动态
                    }
                }
                else {
                    Toast.makeText(getActivity(), "获取失败1"+e, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    private void iniView() {
        rv = getActivity().findViewById(R.id.home_recyclerview);
        srlayout = getActivity().findViewById(R.id.home_swipe);
        HelloHome = getActivity().findViewById(R.id.Hello_home);
        add = getActivity().findViewById(R.id.add);
    }
    private void search_post(){
        BmobQuery<User> innerQuery = new BmobQuery<User>();
        innerQuery.addWhereContainedIn("objectId",focus_data);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereMatchesQuery("author","_User",innerQuery);
        query.order("-createdAt");
        query.include("author");
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                srlayout.setRefreshing(false);
                if(e==null){
                    data.clear();
                 //   Integer num =list.size();
                    data=list;
                    //Toast.makeText(getActivity(), "success!!!"+num.toString(), Toast.LENGTH_SHORT).show();
                    handler = new My_handler();
                    Message msg = new Message();
                    msg.obj="success";
                    handler.sendMessage(msg);
                }
                else {
                    srlayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "获取失败2"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void init_like_list(){

        BmobUser this_user = BmobUser.getCurrentUser(BmobUser.class);
        BmobQuery<Post> query_like = new BmobQuery<Post>();
        User me = new User();
        me.setObjectId(this_user.getObjectId());
        query_like.addWhereRelatedTo("user_like",new BmobPointer(me));
        query_like.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    like_data.clear();
                    like_data = list;
//                    Toast.makeText(getActivity(), "获取点赞信息成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "获取点赞信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        BmobQuery<Post> query_collect = new BmobQuery<>();
        query_collect.addWhereRelatedTo("user_collect",new BmobPointer(me));
        query_collect.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    collect_data.clear();
                    collect_data = list;
//                    Toast.makeText(getActivity(), "获取收藏信息成功", Toast.LENGTH_SHORT).show();
                    search_post();
                }
                else{
                    Toast.makeText(getActivity(), "获取收藏信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

