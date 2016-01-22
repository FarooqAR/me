package com.example.stranger.me.modal;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Stranger on 11/13/2015.
 */
public class Group {
    @JsonIgnore
    String key;
    String name;
    String description;
    String conversation;

    public Group() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }
}
