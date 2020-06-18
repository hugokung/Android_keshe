package com.example.picturesharedemo1.Adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.picturesharedemo1.Activity.LoginActivity;
import com.example.picturesharedemo1.Activity.OthersInfoActivity;
import com.example.picturesharedemo1.Activity.PostDetail;
import com.example.picturesharedemo1.Bean.Post;
import com.example.picturesharedemo1.Bean.User;
import com.example.picturesharedemo.R;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class FindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Post> data;
    private List<Post> like_data;
    private List<Post> collect_data;
    BmobUser now_user = BmobUser.getCurrentUser(BmobUser.class);
    public FindAdapter(Context context, List<Post>data, List<Post> like_data, List<Post> collect_data){
        this.context = context;
        this.data = data;
        this.like_data=like_data;
        this.collect_data = collect_data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new RecyclerViewHolder(view);
    }
    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView nickname,content,time,likes,collects;
        public ImageView head_pic,post_pic,like_pic,collect_pic;

        public RecyclerViewHolder(View view) {
            super(view);
            nickname = view.findViewById(R.id.user_id_name);
            content = view.findViewById(R.id.share_content);
            time = view.findViewById(R.id.post_time);
            head_pic = view.findViewById(R.id.user_head);
            post_pic = view.findViewById(R.id.share_image);
            likes = view.findViewById(R.id.like_sum);
            collects = view.findViewById(R.id.collection_sum);
            like_pic = view.findViewById(R.id.user_like);
            collect_pic = view.findViewById(R.id.user_collection);
            like_pic.setTag(0);
            collect_pic.setTag(0);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        Post post = data.get(position);
        //有昵称则优先显示昵称，无昵称显示username
        if(TextUtils.isEmpty(post.getAuthor().getNickname())){
            recyclerViewHolder.nickname.setText(post.getAuthor().getUsername());
        }else {
            recyclerViewHolder.nickname.setText(post.getAuthor().getNickname());
        }
        recyclerViewHolder.time.setText(post.getCreatedAt());
        recyclerViewHolder.content.setText(post.getContent());
        recyclerViewHolder.likes.setText(post.getLike_num().toString());
        recyclerViewHolder.collects.setText(post.getCollection_num().toString());

        Integer pos = recyclerViewHolder.getAdapterPosition();
        Integer type = getItemViewType(pos);
        if(type == 0) Log.i("find_type",""+type.toString()+" "+post.getObjectId()+" "+pos.toString());
        if(type==3){
            Log.d("find_obj_id",""+post.getObjectId());
            recyclerViewHolder.like_pic.setImageResource(R.drawable.like_fill);
        }
        if(type==2){
            Log.d("find_obj_id",""+post.getObjectId());
            recyclerViewHolder.collect_pic.setImageResource(R.drawable.collection_fill);
        }
        if(type==1){
            recyclerViewHolder.like_pic.setImageResource(R.drawable.like_fill);
            recyclerViewHolder.collect_pic.setImageResource(R.drawable.collection_fill);
        }
        BmobFile post_img = post.getImage();
        String post_img_url = post_img.getUrl();
        BmobFile head_img = post.getAuthor().getAvatar();

        Glide.with(context)
                .load(post_img_url)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.load_fail)
                .into(recyclerViewHolder.post_pic);

        if(head_img!=null){
            String head_img_url = head_img.getUrl();
            Glide.with(context)
                    .load(head_img_url)
                    .placeholder(R.drawable.image_loading)
                    .error(R.drawable.load_fail)
                    .into(recyclerViewHolder.head_pic);
        }
        recyclerViewHolder.head_pic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击成功", Toast.LENGTH_SHORT).show();
                int position = recyclerViewHolder.getAdapterPosition();
                Intent in= new Intent(context, OthersInfoActivity.class);
                in.putExtra("postId", data.get(position).getObjectId());
                context.startActivity(in);
            }
        });
        recyclerViewHolder.like_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerViewHolder.getAdapterPosition();
                Post cur = data.get(position);
                String cur_objId=data.get(position).getObjectId();

                //一开始就没点过赞或者取消过点赞
                if((!judge_like(cur_objId)&&recyclerViewHolder.like_pic.getTag().equals(0))||recyclerViewHolder.like_pic.getTag().equals(2)){//当前动态用户没点过赞
                    Integer sum = cur.getLike_num();
                    sum = sum + 1;
                    cur.setLike_num(sum);
                    recyclerViewHolder.likes.setText(sum.toString());
                    Post po = new Post();
                    po.increment("like_num");
                    po.update(cur_objId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
//                                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "点赞数更新错误1", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    recyclerViewHolder.like_pic.setTag(1);      //标记为点赞过
                    recyclerViewHolder.like_pic.setImageResource(R.drawable.like_fill);
                    Log.i("obj_id_click",""+cur_objId);
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
                            if(e==null){
                                Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{   //之前就点过赞，再点就是取消
                    Integer sum = cur.getLike_num();
                    if(sum!=0) {
                        sum = sum - 1;
                        cur.setLike_num(sum);
                        recyclerViewHolder.likes.setText(sum.toString());
                        Post po = new Post();
                        po.increment("like_num",-1);
                        po.update(cur_objId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
//                                    Toast.makeText(context, "ok2", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "点赞数更新错误2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    recyclerViewHolder.like_pic.setImageResource(R.drawable.like);
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
                            if(e==null){
                                Toast.makeText(context, "取消点赞成功", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "取消失败"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    recyclerViewHolder.like_pic.setTag(2);      //标记为没点过赞
                }
            }
        });

        recyclerViewHolder.collect_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerViewHolder.getAdapterPosition();
                Post cur = data.get(position);
                String cur_objId=data.get(position).getObjectId();
                if((!judge_collect(cur_objId)&&recyclerViewHolder.collect_pic.getTag().equals(0))||recyclerViewHolder.collect_pic.getTag().equals(2)){//当前动态用户没点过赞
                    Integer sum = cur.getCollection_num();
                    sum = sum + 1;
                    cur.setCollection_num(sum);
                    recyclerViewHolder.collects.setText(sum.toString());
                    Post po = new Post();
                    po.increment("collection_num");
                    po.update(cur_objId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
//                                Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "收藏数更新失败1", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    recyclerViewHolder.collect_pic.setTag(1);
                    recyclerViewHolder.collect_pic.setImageResource(R.drawable.collection_fill);
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
                            if(e==null){
                                Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Integer sum = cur.getCollection_num();
                    if(sum!=0) {
                        sum = sum - 1;
                        cur.setCollection_num(sum);
                        recyclerViewHolder.collects.setText(sum.toString());
                        Post po = new Post();
                        po.increment("collection_num",-1);
                        po.update(cur_objId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
//                                    Toast.makeText(context, "ok2", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context, "收藏数更新失败2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    recyclerViewHolder.collect_pic.setImageResource(R.drawable.collection);
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
                            if(e==null){
                                Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "取消失败"+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    recyclerViewHolder.collect_pic.setTag(2);
                }
            }
        });
        recyclerViewHolder.post_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerViewHolder.getAdapterPosition();
                if(BmobUser.getCurrentUser(BmobUser.class)!=null){
                    Intent in = new Intent(context, PostDetail.class);
                    in.putExtra("id",data.get(position).getObjectId());
                    if((!judge_like(data.get(position).getObjectId())&&recyclerViewHolder.like_pic.getTag().equals(0))||recyclerViewHolder.like_pic.getTag().equals(2)){
                        in.putExtra("like_sta","0");
                    }
                    else in.putExtra("like_sta","1");
                    if((!judge_collect(data.get(position).getObjectId())&&recyclerViewHolder.collect_pic.getTag().equals(0))||recyclerViewHolder.collect_pic.getTag().equals(2)){
                        in.putExtra("collect_sta","0");
                    }
                    else in.putExtra("collect_sta","1");
                    context.startActivity(in);
                }
                else{
                    Toast.makeText(context, "请登录", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });
        recyclerViewHolder.head_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击成功", Toast.LENGTH_SHORT).show();
                int position = recyclerViewHolder.getAdapterPosition();
                Intent in= new Intent(context, OthersInfoActivity.class);
                in.putExtra("postId", data.get(position).getObjectId());
                context.startActivity(in);

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    @Override
    public int getItemViewType(int position){
        String cur_id = data.get(position).getObjectId();
        if(judge_like(cur_id)&&judge_collect(cur_id)){
            return 1;
        }
        else if(judge_like(cur_id)==false&&judge_collect(cur_id)){
            return 2;
        }
        else if(judge_like(cur_id)&&judge_collect(cur_id)==false){
            return 3;
        }
        else return 0;
    }
    private Boolean judge_like(String str){
        for (Post t: like_data){
            String tmp = t.getObjectId();
            if(tmp.equals(str)){
                return true;
            }
        }
        return false;
    }
    private Boolean judge_collect(String str){
        for(Post t: collect_data){
            String tmp = t.getObjectId();
            if(tmp.equals(str)){
                return true;
            }
        }
        return false;
    }

}
