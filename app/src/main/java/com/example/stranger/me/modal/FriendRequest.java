package com.example.stranger.me.modal;

/**
 * Created by Farooq on 1/14/2016.
 */
public class FriendRequest {
    private String id;
    private boolean seen;

    public FriendRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
