package com.example.picturesharedemo1.Bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class LikeCollect extends BmobObject {
    private String user_id;
    private List<String> like_list;
    private List<String> collect_list;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<String> getLike_list() {
        return like_list;
    }

    public void setLike_list(List<String> like_list) {
        this.like_list = like_list;
    }

    public List<String> getCollect_list() {
        return collect_list;
    }

    public void setCollect_list(List<String> collect_list) {
        this.collect_list = collect_list;
    }



}
