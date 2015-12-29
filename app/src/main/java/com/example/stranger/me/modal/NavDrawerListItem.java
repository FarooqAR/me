package com.example.stranger.me.modal;

/**
 * Created by Farooq on 12/29/2015.
 */
public class NavDrawerListItem {
    private int mId;
    private String mText;

    public NavDrawerListItem(int mId, String mText) {
        this.mId = mId;
        this.mText = mText;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }
}
