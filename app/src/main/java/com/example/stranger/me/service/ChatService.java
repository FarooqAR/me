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
        mConversations = new ArrayList<>();
        instance = this;
        Firebase.setAndroidContext(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotifcationDismissReceiver receiver = new NotifcationDismissReceiver();
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
    public static ChatService getInstance(){
        return instance;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FirebaseHelper.getRoot().addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (FirebaseHelper.getAuthId() != null) {
                    updateListener();
                    Log.d(TAG, "auth id=" + FirebaseHelper.getAuthId());
                }
                Log.d(TAG, "onAuthChange");
            }
        });
        return START_STICKY;
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
            Conversation c = cons[0];
            for (String pushKey : c.getPushKeys()) {
                FirebaseHelper.getRoot()
                        .child("private_conversation")
                        .child(c.getConversationKey())
                        .child(pushKey)
                        .child("seen").setValue(true);
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
            mConversations.add(conversations[0]);
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
        new NotificationRemoveTask().execute(conversationKey);
    }

    //it will be executed when a service is created or auth state is changed
    private class NotificationsRemoveTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < mConversations.size(); i++) {
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

    private class NotificationRemoveTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String conversationKey = strings[0];
            Log.d(TAG,"removing notification");
            for (int i = 0; i < mConversations.size(); i++) {
                Conversation conversation = mConversations.get(i);
                if (conversation.getConversationKey().equals(conversationKey)) {
                    mNotificationManager.cancel(conversation.getNotificationId());//cancel any existing notifications related to given conversationkey
                    conversation.getPushKeys().clear();
                    Log.d(TAG,"Notification Removed");
                    return null;
                }
            }
            mConversations.clear();
            return null;
        }

    }
}
