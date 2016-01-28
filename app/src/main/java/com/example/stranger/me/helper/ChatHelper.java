package com.example.stranger.me.helper;

import android.os.AsyncTask;
import android.os.Handler;

import com.example.stranger.me.modal.Message;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Farooq on 1/17/2016.
 */
public class ChatHelper {
    private static DataSnapshot PRIVATE_CHAT_NODE = null;
    public static DataSnapshot getPrivateChatNode() {
        return PRIVATE_CHAT_NODE;
    }

    public static void setPrivateChatNode(DataSnapshot privateChatNode) {
        PRIVATE_CHAT_NODE = privateChatNode;
    }

    public static String getConversationKey(String id) {
        String key = null;
        try {
            if(id!=null)
            key = new ConversationKeyTask().execute(id).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static void sendMessage(final String user_id, final Message message, final Firebase.CompletionListener listener) {
        String con_key = getConversationKey(user_id);
        if (con_key == null) {
            String conversation_key = null;
            try {
                conversation_key = new GenerateKey().execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            final String finalConversation_key = conversation_key;
            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CHAT).child(FirebaseHelper.getAuthId()).child(user_id).setValue(conversation_key, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CHAT).child(user_id).child(FirebaseHelper.getAuthId()).setValue(finalConversation_key, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                Firebase ref = FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(finalConversation_key).push();
                                new MessageHandler(ref,message,listener);
                            }
                        });
                    }
                }
            });

        } else {
            Firebase ref = FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(con_key).push();
            new MessageHandler(ref,message,listener);
        }
    }

    private static class ConversationKeyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String key = null;
            Map<String, Object> private_chat_auth = null;
            Map<String, Object> private_chat_user = null;
            if (getPrivateChatNode() != null && FirebaseHelper.getAuthId() != null) {
                private_chat_auth = (Map<String, Object>) getPrivateChatNode().child(FirebaseHelper.getAuthId()).getValue();
                private_chat_user = (Map<String, Object>) getPrivateChatNode().child(params[0]).getValue();
                if (private_chat_auth != null && private_chat_user != null) {
                    String con_auth = (String) private_chat_auth.get(params[0]);
                    String con_user = (String) private_chat_user.get(FirebaseHelper.getAuthId());
                    if (con_auth != null &&
                            con_user != null &&
                            con_auth.equals(con_user)) {
                        key = con_auth;
                    }
                }
            }

            return key;
        }
    }

    static class MessageHandler extends Handler{
        final Map<String,Object> map = new HashMap<>();
        Firebase ref;
        Firebase.CompletionListener listener;
        public MessageHandler(Firebase ref,final Message msg,Firebase.CompletionListener listener) {
            this.ref = ref;
            this.listener = listener;
            Thread t = new Thread() {
                @Override
                public void run(){
                    map.put("sender", FirebaseHelper.getAuthId());
                    map.put("message", msg.getMessage());
                    map.put("seen",false);
                    map.put("locationLat",msg.getLocationLat());
                    map.put("locationLong",msg.getLocationLong());
                    map.put("location",msg.getLocation());
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






    public static class GenerateKey extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return UUID.randomUUID().toString();
        }
    }
}
