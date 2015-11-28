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
        final ArrayList<String> friends = new ArrayList<String>();
        getRoot().child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleDataSnapshot :
                        dataSnapshot.getChildren()) {
                    String friendId = (String) singleDataSnapshot.getValue();
                    friends.add(friendId);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return friends;
    }

}
