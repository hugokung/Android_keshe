package com.example.picturesharedemo1.Bean;

import cn.bmob.v3.BmobObject;

import cn.bmob.v3.datatype.BmobFile;

public class Post extends BmobObject {
    private User author;    //发布人
    private String title,content,nickname;  //标题、内容、用户昵称
    private BmobFile image;     //发布的图片

    private Integer like_num,collection_num;

    public Integer getLike_num() {
        return like_num;
    }

    public void setLike_num(Integer like_num) {
        this.like_num = like_num;
    }

    public Integer getCollection_num() {
        return collection_num;
    }

    public void setCollection_num(Integer collection_num) {
        this.collection_num = collection_num;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }
}
