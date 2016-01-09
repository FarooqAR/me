package com.example.stranger.me.helper;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Farooq on 11/14/2015.
 */
public class FirebaseHelper {
    private static Firebase root;
    private static String AUTH_ID;

    public static Firebase getRoot() {
        if (root == null) {
            root = new Firebase("https://app-me.firebaseio.com");
        }
        return root;
    }

    public static String getAuthId() {
        if (AUTH_ID == null) {
            AUTH_ID = getRoot().getAuth().getUid();
        }
        return AUTH_ID;
    }

    public static ArrayList<String> getFriends(String userId) {
        return null;
    }

}
