package com.example.stranger.me.helper;

import android.os.Handler;
import android.os.Message;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Farooq on 1/19/2016.
 */
public class GroupHelper {
    private static DataSnapshot GROUPS = null;
    private static DataSnapshot MEMBERS = null;
    private static DataSnapshot GROUP_REQUESTS = null;


    public static DataSnapshot getGroupRequests() {
        return GROUP_REQUESTS;
    }

    public static void setGroupRequests(DataSnapshot groupRequests) {
        GROUP_REQUESTS = groupRequests;
    }

    public static DataSnapshot getMEMBERS() {
        return MEMBERS;
    }

    public static void setMEMBERS(DataSnapshot MEMBERS) {
        GroupHelper.MEMBERS = MEMBERS;
    }

    public static DataSnapshot getGROUPS() {
        return GROUPS;
    }

    public static void setGROUPS(DataSnapshot GROUPS) {
        GroupHelper.GROUPS = GROUPS;
    }

    //equivalent to join group
    public static void addMember(final String userId, final String groupKey, final int accessLevel, final Firebase.CompletionListener listener) {
        FirebaseHelper.getRoot().child("group_requests").child(groupKey).child(userId).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                FirebaseHelper.getRoot().child("group_members")
                        .child(groupKey)
                        .child(userId)
                        .child("accessLevel")
                        .setValue(accessLevel, listener);
            }
        });

    }

    //equivalent to leave group
    public static void removeMember(String userId, String groupKey, Firebase.CompletionListener listener) {
        FirebaseHelper.getRoot().child("group_members")
                .child(groupKey)
                .child(userId).removeValue(listener);
    }

    public static void addGroup(String groupName, String groupDesc, Firebase.CompletionListener listener) {
        new AddGroupTask(groupName, groupDesc, listener);
    }

    public static String getConversationKey(String groupKey) {
        return (String) getGROUPS().child(groupKey).child("conversation").getValue();
    }

    public static boolean isRequested(String userId, String groupKey) {
        return getGroupRequests().child(groupKey).child(userId).exists();
    }

    public static void sendRequest(String userId, String groupKey, Firebase.CompletionListener listener) {
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_REQUESTS).child(groupKey).child(userId).child("seen").setValue(false, listener);
    }

    public static void deleteGroup(final String groupKey, final Firebase.CompletionListener listener) {
        //delete posts as well
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_REQUESTS).child(groupKey).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(groupKey).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).child(getConversationKey(groupKey)).removeValue(new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                FirebaseHelper.getRoot().child(FirebaseHelper.GROUPS).child(groupKey).removeValue(listener);
                            }
                        });
                    }
                });
            }
        });


    }
    public static void sendMessage(String con_key, com.example.stranger.me.modal.Message message,Firebase.CompletionListener listener){
        Firebase ref= FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).child(con_key).push();
        new MessageHandler(ref,message,listener);
    }
    public static long getAccessLevel(String userId, String groupKey) {
        if (getMEMBERS().child(groupKey).child(userId).exists()) {
            long accessLevel = (long) getMEMBERS().child(groupKey).child(userId).child("accessLevel").getValue();
            return accessLevel;
        }
        return -1;
    }

    public static class AddGroupTask extends Handler {
        private Map<String, Object> group;
        private String group_key;
        private Firebase.CompletionListener listener;

        public AddGroupTask(final String groupName, final String groupDesc, Firebase.CompletionListener listener) {
            this.listener = listener;
            Thread t = new Thread() {
                @Override
                public void run() {
                    String conversation_key = UUID.randomUUID().toString();
                    group_key = UUID.randomUUID().toString();
                    Map<String, Object> group_data = new HashMap<>();
                    group = new HashMap<>();
                    group_data.put("name", groupName);
                    group_data.put("description", groupDesc);
                    group_data.put("conversation", conversation_key);
                    group.put(group_key, group_data);
                    sendEmptyMessage(0);
                }
            };
            t.start();

        }

        @Override
        public void handleMessage(Message msg) {
            addMember(FirebaseHelper.getAuthId(), group_key, 3, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    FirebaseHelper.getRoot().child("groups").updateChildren(group, listener);
                }
            });

        }
    }
    static class MessageHandler extends Handler{
        final Map<String,Object> map = new HashMap<>();
        Firebase ref;
        Firebase.CompletionListener listener;
        public MessageHandler(Firebase ref,final com.example.stranger.me.modal.Message msg,Firebase.CompletionListener listener) {
            this.ref = ref;
            this.listener = listener;
            Thread t = new Thread() {
                @Override
                public void run(){
                    map.put("sender", msg.getSender());
                    map.put("message", msg.getMessage());
                    map.put("seen",false);
                    map.put("imageUrl", msg.getImageUrl());
                    map.put("timestamp", ServerValue.TIMESTAMP);
                    sendEmptyMessage(0);
                }
            };
            t.start();

        }

        @Override
        public void handleMessage(android.os.Message msg) {
            ref.setValue(map, listener);
        }
    }


}
