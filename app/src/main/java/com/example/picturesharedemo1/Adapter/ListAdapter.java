/*package com.example.picturesharedemo1.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.picturesharedemo.R;
import com.example.picturesharedemo1.Activity.FolloweeActivity;
import com.example.picturesharedemo1.Bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<User> list;

    public static class ViewHolder {
         public ImageView head_pic;
         public TextView name;

    }

    public ListAdapter(Context context, ArrayList<User> list) {
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
            convertView = inflater.inflate(R.layout.activity_followee_list, null);//实例化一个布局文件

            ViewHolder holder = new ViewHolder();
            holder.head_pic=convertView.findViewById(R.id.iv_followee_head);
            holder.name=convertView.findViewById(R.id.tv_followee_name);
            convertView.setTag(holder);
        }

        User poUser =list.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        Glide.with(context).load(poUser.getAvatar()
                .getFileUrl()).into(viewHolder.head_pic);
        if(TextUtils.isEmpty(poUser.getNickname())){
            viewHolder.name.setText(poUser.getUsername());
        }else{
            viewHolder.name.setText(poUser.getNickname());
        }


        return convertView;
    }

}*/
