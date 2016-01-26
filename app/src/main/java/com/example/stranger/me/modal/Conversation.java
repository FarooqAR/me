package com.example.stranger.me.modal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.activity.MainActivity;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.service.ChatService;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Farooq on 1/23/2016.
 */
public class Conversation implements Parcelable {
    private static final String TAG = "Conversation";
    private String friendId;
    private String friendName;
    private String friendImageUrl;
    private String conversationKey;
    private int notificationId;
    private ArrayList<String> pushKeys;
    private ChildEventListener listener;
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendImageUrl() {
        return friendImageUrl;
    }

    public void setFriendImageUrl(String friendImageUrl) {
        this.friendImageUrl = friendImageUrl;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public ArrayList<String> getPushKeys() {
        return pushKeys;
    }

    public void setPushKeys(ArrayList<String> pushKeys) {
        this.pushKeys = pushKeys;
    }

    public ChildEventListener getListener() {
        return listener;
    }


    public Conversation(final Context context) {
        this.context = context;
        this.pushKeys = new ArrayList<>();
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String messagePushKey = dataSnapshot.getKey();
                String sender = (String) dataSnapshot.child("sender").getValue();
                if (!sender.equals(FirebaseHelper.getAuthId())) {
                    new MessageAddTask().execute(messagePushKey);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                new MessageRemoveTask().execute(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

    public void setListener() {
        FirebaseHelper.getRoot().child("private_conversation").child(getConversationKey()).orderByChild("seen").equalTo(false).addChildEventListener(listener);
    }

    public void removeListener() {
        FirebaseHelper.getRoot().child("private_conversation").child(getConversationKey()).removeEventListener(listener);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(getPushKeys());
        parcel.writeString(getConversationKey());
    }

    private Conversation(Parcel in) {
        setPushKeys((ArrayList<String>) in.readSerializable());
        setConversationKey(in.readString());
    }

    public static final Parcelable.Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel parcel) {
            return new Conversation(parcel);
        }

        @Override
        public Conversation[] newArray(int i) {
            return new Conversation[i];
        }
    };

    class NotificationTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected synchronized Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            String msgWord = (getPushKeys().size() == 1) ? "message" : "messages";
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setLargeIcon(bitmap)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getPushKeys().size() + " unread " + msgWord)
                            .setContentText(getFriendName() + " sent you " + getPushKeys().size() + " " + msgWord);

            Intent intent = new Intent(ChatService.RECEIVER);
            intent.putExtra("notificationConversation", Conversation.this);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), ChatService.BROADCAST_REQUEST_CODE, intent, 0);
            mBuilder.setDeleteIntent(pendingIntent);

            Intent resultIntent = new Intent(getContext(), MainActivity.class);
            resultIntent.putExtra(HomeActivity.FRIEND_ID, friendId);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());

            stackBuilder.addParentStack(HomeActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            ChatService.REQUEST_CODE,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(getNotificationId(), mBuilder.build());
            return null;
        }
    }

    class MessageAddTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected synchronized Boolean doInBackground(String... strings) {
            for (int i = 0; i < pushKeys.size(); i++) {
                if (pushKeys.get(i).equals(strings[0]))
                    return false;
            }
            pushKeys.add(strings[0]);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        new NotificationTask().execute(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable drawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable drawable) {

                    }
                };
                Picasso.with(getContext()).load(getFriendImageUrl()).into(target);
            }
        }
    }

    private class MessageRemoveTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String pushKey = strings[0];
            for (int i = 0; i < getPushKeys().size(); i++) {
                if(getPushKeys().get(i).equals(pushKey)){
                    getPushKeys().remove(i);
                    return null;
                }
            }
            return null;
        }
    }
}
