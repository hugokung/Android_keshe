package com.example.picturesharedemo1.Bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobUser {
    private String  repassword;
    //头像
    private BmobFile avatar;
    //昵称
    private String nickname;
    //性别
    private String gender;
    //生日
    private String birth;
    //个人简介
    private String selfintroduction;
    //关注的人
    private BmobRelation focusId;
    //点赞的人
    private BmobRelation user_like;
    //收藏的人
    private BmobRelation user_collect;

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    public BmobFile getAvatar() {
        return avatar;
    }
    public User setAvatar(BmobFile avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getNickname() {
        return nickname;
    }
    public User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getGender() {
        return gender;
    }
    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getSelfintroduction() {
        return selfintroduction;
    }
    public User setSelfintroduction(String selfintroduction) {
        this.selfintroduction = selfintroduction;
        return this;
    }


    public String getBirth() {
        return birth;
    }

    public User setBirth(String birth) {
        this.birth = birth;
        return this;
    }

    public BmobRelation getFocusId() {
        return focusId;
    }

    public void setFocusId(BmobRelation focusId) {
        this.focusId = focusId;
    }
    public BmobRelation getUser_collect() {
        return user_collect;
    }

    public void setUser_collect(BmobRelation user_collect) {
        this.user_collect = user_collect;
    }

    public BmobRelation getUser_like() {
        return user_like;
    }

    public void setUser_like(BmobRelation user_like) {
        this.user_like = user_like;
    }

}
