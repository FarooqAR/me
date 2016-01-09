package com.example.stranger.me.modal;

import java.util.Map;


public class Post {
    String title;
    String posterId;
    String postId;
    String sharedFrom;//if shared is true then it is used
    String imageLink;
    String commentsKey;
    boolean shared;
    long timestamp;
    long likes;
    int latitude;
    int longitude;
    Map<String,Object> comments;
    /*TODO:add getters and setters for comments*/

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

    public String getSharedFrom() {
        return sharedFrom;
    }

    public void setSharedFrom(String sharedFrom) {
        this.sharedFrom = sharedFrom;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCommentsKey() {
        return commentsKey;
    }

    public void setCommentsKey(String commentsKey) {
        this.commentsKey = commentsKey;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
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

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
