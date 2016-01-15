package com.example.stranger.me.modal;

/**
 * Created by Farooq on 1/14/2016.
 */
public class Friend {
    private String id;
    private String key;

    public Friend(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public Friend() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
