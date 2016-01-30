package com.example.stranger.me.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Conversation;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Farooq on 1/23/2016.
 */
public class ChatService extends Service {
    public static final String RECEIVER = "com.example.stranger.service.ChatService.NotifcationDismissReceiver";
    private static final String TAG = "ChatService";
    private static ArrayList<Conversation> mConversations;
    private static int NOTIFICATION_ID = 5876;
    public static int REQUEST_CODE = 4563;
    public static int BROADCAST_REQUEST_CODE = 2341;
    private NotificationManager mNotificationManager;
    private static ChatService instance = null;
    private NotifcationDismissReceiver receiver;
    private String previousKey;//conversation key for which the messages were previously showing (ChatFragment)

    private ChildEventListener mPrivateChatListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String userId = dataSnapshot.getKey();
            final String conId = String.valueOf(dataSnapshot.getValue());
            FirebaseHelper.getRoot().child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Conversation conversation = new Conversation(getApplicationContext());
                    conversation.setFriendId(String.valueOf(userId));
                    conversation.setConversationKey(conId);
                    String firstname = (String) dataSnapshot.child("firstName").getValue();
                    String imageUrl = String.valueOf(dataSnapshot.child("profileImageURL").getValue());
                    conversation.setFriendName(firstname);
                    conversation.setFriendImageUrl(imageUrl);
                    conversation.setNotificationId(NOTIFICATION_ID);
                    NOTIFICATION_ID += 1;
                    conversation.setListener();
                    new ConversationAddTask().execute(conversation);
                    FirebaseHelper.getRoot().child("users").child(userId).removeEventListener(this);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String conKey = (String) dataSnapshot.getValue();
            new ConversationRemoveTask().execute(conKey);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ChatService Created");
        mConversations = new ArrayList<>();
        instance = this;
        Firebase.setAndroidContext(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        receiver = new NotifcationDismissReceiver();
        registerReceiver(receiver, new IntentFilter(RECEIVER));
    }

    public void updateListener() {

        /**
         * remove any existing notifications and clear conversations list
         * unlike NotificationRemoveTask it will remove every conversation and its notifications
         * executed whenever the service is created or the authId changes
         */
        new NotificationsRemoveTask().execute();

    }

    public static ChatService getInstance() {
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ChatService started");
        FirebaseHelper.getRoot().addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (FirebaseHelper.getAuthId() != null) {
                    updateListener();
                } else {
                    FirebaseHelper.getRoot().removeAuthStateListener(this);
                    stopSelf();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "ChatService stopped");
        return super.stopService(name);
    }


    public static class NotifcationDismissReceiver extends BroadcastReceiver {
        public NotifcationDismissReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Conversation c = intent.getParcelableExtra("notificationConversation");
            new ResetSeen().execute(c);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(c.getNotificationId());
        }
    }

    //it will be executed on notification dismiss
    public static class ResetSeen extends AsyncTask<Conversation, Void, Void> {

        @Override
        protected synchronized Void doInBackground(Conversation... cons) {
            final Conversation c = cons[0];
            for (final String pushKey : c.getPushKeys()) {
                FirebaseHelper.getRoot()
                        .child("private_conversation")
                        .child(c.getConversationKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(pushKey).exists()) {
                            FirebaseHelper.getRoot()
                                    .child("private_conversation")
                                    .child(c.getConversationKey())
                                    .child(pushKey)
                                    .child("seen").setValue(true);
                        }
                        FirebaseHelper.getRoot()
                                .child("private_conversation")
                                .child(c.getConversationKey()).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        FirebaseHelper.getRoot()
                                .child("private_conversation")
                                .child(c.getConversationKey()).removeEventListener(this);
                    }
                });

            }
            for (int i = 0; i < mConversations.size(); i++) {
                if (c.getConversationKey().equals(mConversations.get(i).getConversationKey())) {
                    mConversations.get(i).getPushKeys().clear();
                }
            }
            return null;
        }
    }


    private class ConversationAddTask extends AsyncTask<Conversation, Void, Void> {
        @Override
        protected synchronized Void doInBackground(Conversation... conversations) {
            for (int i = 0; i < mConversations.size(); i++) {
                if (mConversations.get(i).getConversationKey().equals(conversations[0].getConversationKey())) {
                    return null;
                }
            }
            mConversations.add(conversations[0]);
            Log.d(TAG, "conversation=" + conversations[0].getConversationKey());
            return null;
        }
    }

    private class ConversationRemoveTask extends AsyncTask<String, Void, Void> {
        @Override
        protected synchronized Void doInBackground(String... conversations) {
            for (Conversation conversation : mConversations) {
                if (conversation.getConversationKey().equals(conversations[0])) {
                    conversation.removeListener();
                    mNotificationManager.cancel(conversation.getNotificationId());
                    mConversations.remove(conversation);
                    return null;
                }
            }
            return null;
        }
    }

    public void removeNotificationsFor(String conversationKey) {
        new RemoveNotificationFromChatFragment().execute(conversationKey);
    }
    public void setListenerFor(String conversationKey){
        new SetNotificationFor().execute(conversationKey);
    }
    @Override
    public void onDestroy() {

        try {
            new NotificationRemoveTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        unregisterReceiver(receiver);
        Log.d(TAG, "ChatService destroyed");
        super.onDestroy();
    }

    //it will be executed when a service is created or auth state is changed
    private class NotificationsRemoveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < mConversations.size(); i++) {
                mConversations.get(i).removeListener();
                mConversations.get(i).getPushKeys().clear();
                mNotificationManager.cancel(mConversations.get(i).getNotificationId());//cancel any existing notifications
            }
            mConversations.clear();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            FirebaseHelper.getRoot()
                    .child("private_chat")
                    .child(FirebaseHelper.getAuthId())
                    .removeEventListener(mPrivateChatListener);
            FirebaseHelper.getRoot()
                    .child("private_chat")
                    .child(FirebaseHelper.getAuthId())
                    .addChildEventListener(mPrivateChatListener);
        }
    }
    //it will be executed when the chat service is completely destroyed
    private class NotificationRemoveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void...voids) {

                for (int i = 0; i < mConversations.size(); i++) {
                    Conversation conversation = mConversations.get(i);
                    conversation.removeListener();
                    mNotificationManager.cancel(conversation.getNotificationId());//cancel any existing notifications
                    conversation.getPushKeys().clear();
                }
            mConversations.clear();
            return null;

        }
    }
    private class SetNotificationFor extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String conversationKey = strings[0];
            for (int i = 0; i < mConversations.size(); i++) {
                Conversation conversation = mConversations.get(i);
                if (conversation.getConversationKey().equals(conversationKey)) {
                    conversation.setListener();
                    break;
                }
            }
            return conversationKey;
        }

        @Override
        protected void onPostExecute(String key) {
            super.onPostExecute(key);
            previousKey = key;
        }
    }
    private class RemoveNotificationFromChatFragment extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String conversationKey = strings[0];

            //if the chat for this conversation is visible then no need to notify user for new messages
            for (int i = 0; i < mConversations.size(); i++) {
                Conversation conversation = mConversations.get(i);
                if (conversation.getConversationKey().equals(conversationKey)) {
                    mNotificationManager.cancel(conversation.getNotificationId());//cancel any existing notifications related to given conversationkey
                    conversation.getPushKeys().clear();
                    conversation.removeListener();
                    break;
                }
            }
            //set listener for previous conversation so that we can again get notification of that chat
            if(previousKey!=null) {
                for (int i = 0; i < mConversations.size(); i++) {
                    Conversation conversation = mConversations.get(i);
                    if (conversation.getConversationKey().equals(previousKey)) {
                        conversation.getPushKeys().clear();
                        conversation.removeListener();
                        conversation.setListener();
                        break;
                    }
                }
            }
            return conversationKey;

        }

        @Override
        protected void onPostExecute(String key) {
            super.onPostExecute(key);
            previousKey = key;
        }
    }
}