package com.example.stranger.me.modal;

import java.util.Map;


public class Post {
    String title;
    String posterId;
    String postId;
    long timestamp;
    long likes;
    Map<String,Object> comments;
    /*TODO:add getters and setters*/

    public Post(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Map<String, Object> getComments() {
        return comments;
    }

    public void setComments(Map<String, Object> comments) {
        this.comments = comments;
    }
}
