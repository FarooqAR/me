package com.example.stranger.me.modal;

import com.example.stranger.me.helper.FirebaseHelper;

/**
 * Created by Farooq on 11/10/2015.
 */
public class Comment {
    private String comment;
    private String commentator;
    private int timestamp;
    private int likes;


    public Comment(){

    }

    public String getComment() {
        return comment;
    }

    public String getCommentator() {
        return commentator;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getLikes() {
        return likes;
    }
    public void create(String comments_key){
        FirebaseHelper.getRoot().child("post_comments").child(comments_key).push().setValue(this);
    }
}
