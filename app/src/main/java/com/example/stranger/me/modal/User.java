package com.example.stranger.me.modal;

import com.example.stranger.me.helper.FirebaseHelper;

import java.util.ArrayList;

/**
 * Created by Stranger on 11/20/2015.
 */
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String location;
    private String profileImg;
    private boolean online;

    public String getProfileImg() {
        return profileImg;
    }

    public boolean isOnline() {
        return online;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getId() {

        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList getFriends() {

        return FirebaseHelper.getFriends(id);
    }
}
