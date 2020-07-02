package com.example.picturesharedemo1.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.picturesharedemo1.Adapter.FindAdapter;
import com.example.picturesharedemo1.Adapter.HomeAdapter;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class FragmentSearch extends Fragment {
    private RecyclerView rv;
    private SwipeRefreshLayout srlayout;
    private HomeAdapter findAdapter;
    private FindAdapter findAdapter2;
    private List<Post> data = new ArrayList<>();
    private List<Post> like_data = new ArrayList<>();
    private List<Post> collect_data = new ArrayList<>();

    private Handler handler;
    class My_handler extends Handler{
        @Override
        public void handleMessage(Message msg){

            if(msg.what == 0){  //全部动态的信息查询成功
                data.clear();
                data = (List<Post>) msg.obj;
            }
            else if(msg.what == 2){     //用户的点赞信息查询成功
                like_data.clear();
                like_data = (List<Post>) msg.obj;
            }
            else if(msg.what == 1){     //用户的收藏信息查询成功
                collect_data.clear();
                collect_data = (List<Post>) msg.obj;
                findAdapter2 = new FindAdapter(getActivity(),data,like_data,collect_data);  //所有需要的信息都查询成功可以加载页面了
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.setAdapter(findAdapter2);
            }
            /*if(msg.obj=="success"){
                findAdapter2 = new FindAdapter(getActivity(),data,like_data,collect_data);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                rv.setAdapter(findAdapter2);
            }*/
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        Bmob.initialize(getActivity(),"8369765908b5b24d951f7a13eb151240");
        Refresh();
        srlayout.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);
        srlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
    }

    private void Refresh() {
        BmobQuery<Post> po = new BmobQuery<Post>();
        po.order("-createdAt");
        po.include("author");
        po.setLimit(100);
        po.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                //srlayout.setRefreshing(false);
                if(e==null){
                    //data.clear();
                    //data=list;

                    handler = new My_handler();
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = list;
                    handler.sendMessage(msg);

                    init_like_list();
//                    findAdapter = new HomeAdapter(getActivity(),data,like_data,collect_data);
//                    //findAdapter2 = new FindAdapter(getActivity(),data);
//                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rv.setAdapter(findAdapter);
//                    BmobFile test_img = list.get(0).getImage();
//                    String test_img_url = test_img.getUrl();
//                    Toast.makeText(getActivity(), "图片地址:"+test_img_url, Toast.LENGTH_SHORT).show();
                }
                else{
                    srlayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        rv = getActivity().findViewById(R.id.find_recyclerview);
        srlayout = getActivity().findViewById(R.id.find_swipe);
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
                    //like_data.clear();
                    //like_data = list;
                    Integer list_num = list.size();

                    handler = new My_handler();
                    Message  msg = new Message();
                    msg.what = 2;
                    msg.obj = list;
                    handler.sendMessage(msg);
                    init_collect_list();
                    Toast.makeText(getActivity(), "获取点赞信息成功"+list_num.toString(), Toast.LENGTH_SHORT).show();
                }
                else{
                    srlayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "获取点赞信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        BmobQuery<Post> query_collect = new BmobQuery<>();
        query_collect.addWhereRelatedTo("user_collect",new BmobPointer(me));
        query_collect.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    //collect_data.clear();
                    //collect_data = list;
                    handler = new My_handler();
                    Message meg = new Message();
                    meg.obj="success";
                    meg.obj = list;
                    meg.what = 1;
                    handler.sendMessage(meg);
                    srlayout.setRefreshing(false);
//                    Toast.makeText(getActivity(), "获取收藏信息成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "获取收藏信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
    private void init_collect_list(){
        BmobUser this_user = BmobUser.getCurrentUser(BmobUser.class);
        User me = new User();
        me.setObjectId(this_user.getObjectId());
        BmobQuery<Post> query_collect = new BmobQuery<>();
        query_collect.addWhereRelatedTo("user_collect",new BmobPointer(me));
        query_collect.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if(e==null){
                    //collect_data.clear();
                    //collect_data = list;
                    handler = new My_handler();
                    Message meg = new Message();
                    meg.obj="success";
                    meg.obj = list;
                    meg.what = 1;
                    handler.sendMessage(meg);
                    srlayout.setRefreshing(false);
//                    Toast.makeText(getActivity(), "获取收藏信息成功", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), "获取收藏信息失败"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
