package com.example.stranger.me.modal;

/**
 * Created by Stranger on 11/20/2015.
 */
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String country;
    private String about;
    private long age;
    private String profileImageURL;
    private boolean online;

    public User(String firstName, String lastName, String country, String profileImageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.profileImageURL = profileImageURL;
    }

    public User() {
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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




}
