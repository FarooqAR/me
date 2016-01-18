package com.example.stranger.me.modal;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Stranger on 11/9/2015.
 */
public class Message {
    @JsonIgnore
    private String push_key;
    private String message;
    private String sender;
    private String timestamp;
    private String imageUrl;
    private boolean seen;

    public Message() {
    }

    public Message(String message, String sender) {
        this.message = message;
        this.sender = sender;
        this.seen = false;
    }

    public String getPush_key() {
        return push_key;
    }

    public void setPush_key(String push_key) {
        this.push_key = push_key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
