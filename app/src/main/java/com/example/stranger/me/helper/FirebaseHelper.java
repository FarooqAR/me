package com.example.stranger.me.helper;

import android.os.AsyncTask;

import com.example.stranger.me.modal.Friend;
import com.example.stranger.me.modal.Request;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Farooq on 11/14/2015.
 */
public class FirebaseHelper {
    public static final String FRIEND_REQUESTS_KEY = "friend_requests";
    public static final String FRIENDS_KEY= "friends";
    public static final String PRIVATE_CONVERSATION= "private_conversation";
    public static final String PRIVATE_CHAT= "private_chat";
    public static final String GROUP_CONVERSATION= "group_conversation";
    public static final String GROUP_MEMBERS= "group_members";
    public static final String GROUP_REQUESTS= "group_requests";
    public static final String GROUPS= "groups";
    public static final String USERS_KEY= "users";

    private static Firebase ROOT = null;
    private static String AUTH_ID = null;
    private static DataSnapshot USERS = null;//all users
    public static ArrayList<Request> friendRequests = null;//requests to authenticated user
    public static ArrayList<Friend> friends = null;//friends of authenticated user
    private static DataSnapshot FRIEND_REQUESTS = null;//friend requests node


    public static void setFriendRequests(DataSnapshot friendRequests) {
        FRIEND_REQUESTS = friendRequests;
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }

    public static void setFriends(ArrayList<Friend> friends) {
        FirebaseHelper.friends = friends;
    }


    public static ArrayList<Request> getFriendRequests() {
        return friendRequests;
    }

    public static DataSnapshot getFriendRequestsSnapshot() {
        return FRIEND_REQUESTS;
    }

    public static void setFriendRequests(ArrayList<Request> friendRequests) {
        FirebaseHelper.friendRequests = friendRequests;
    }

    public static String getProfileImage(String user_id) {
        String url = (String) getUsers().child(user_id).child("profileImageURL").getValue();
        return url;
    }

    //update friend request list
    public static void addFriendRequest(Request request) {
        new AddFriendRequestTask().execute(request);
    }

    public static void removeFriendRequest(String id) {
        new RemoveFriendRequestTask().execute(id);
    }

    //update friend list
    public static void addFriend(Friend friend) {
        new AddFriendTask().execute(friend);
    }

    public static void removeFriend(String id) {
        new RemoveFriendTask().execute(id);
    }

    public static Firebase getRoot() {
        if (ROOT == null) {
            ROOT = new Firebase("https://app-me.firebaseio.com");
        }
        return ROOT;
    }

    public static void setUsers(DataSnapshot snapshot) {
        USERS = snapshot;
    }

    public static String getAuthId() {
        if (AUTH_ID == null) {
            if (getRoot().getAuth() != null &&
                    (FirebaseHelper.getRoot().getAuth().getProvider().equals("facebook") ||
                            FirebaseHelper.getRoot().getAuth().getProvider().equals("google"))) {
                AUTH_ID = (String) getRoot().getAuth().getProviderData().get("id");
            } else if (getRoot().getAuth() != null) {
                AUTH_ID = getRoot().getAuth().getUid();
            }
        }
        return AUTH_ID;
    }

    public static void setAuthId(String id) {
        AUTH_ID = id;
    }

    public static boolean isUser(String id) {
        return getUsers().child(id).exists();
    }


    public static boolean isFriend(String id) {//push key in friends list
        boolean isFriend = false;
        try {
            isFriend = new GetFriendTask().execute(id).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return isFriend;
    }

    public static DataSnapshot getUsers() {
        return USERS;
    }

    //whether the given user has sent friend request to authenticated user
    public static boolean isRequested(String userId) {
        boolean isRequested = false;
        try {
            isRequested = new GetFriendRequestTask().execute(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return isRequested;
    }

    //whether the user has sent friend request to given user
    public static boolean isRequestSent(String userId) {
        boolean isRequestSent = false;
        try {
            isRequestSent = new GetUserRequestTask().execute(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return isRequestSent;
    }


    public static void unFriend(final String id, final Firebase.CompletionListener listener) {
        //remove given user from friends of current user

        getRoot().child(FRIENDS_KEY).child(getAuthId()).child(id).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //remove auth user from friends of given user

                getRoot().child(FRIENDS_KEY).child(id).child(getAuthId()).removeValue(new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        //delete their conversation if there is a key (confirmAsFriend will ensure that there is a key)
                            getRoot().child(PRIVATE_CONVERSATION).child(ChatHelper.getConversationKey(id)).removeValue(new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    //delete registered conversation keys
                                    getRoot().child(PRIVATE_CHAT).child(FirebaseHelper.getAuthId()).child(id).removeValue(new Firebase.CompletionListener() {
                                        @Override
                                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                            //delete registered conversation keys
                                            getRoot().child(PRIVATE_CHAT).child(id).child(FirebaseHelper.getAuthId()).removeValue(listener);
                                        }
                                    });
                                }
                            });

                    }
                });
            }
        });
    }

    public static void sendFriendRequest(final String id, Firebase.CompletionListener listener) {
        if (!id.equals(getAuthId()))//send request only if given id is not equal to auth id
            getRoot().child(FRIEND_REQUESTS_KEY).child(id).child(getAuthId()).child("seen").setValue(false, listener);
    }

    public static void confirmAsFriend(final String id, final Firebase.CompletionListener listener) {
        //first remove the given user request from current user's friend requests list
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY)
                .child(getAuthId()).child(id).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null)

                    FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).child(id).child(getAuthId()).removeValue(new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            //then add the authenticated user as a friend of given user
                            FirebaseHelper.getRoot().child(FRIENDS_KEY).child(FirebaseHelper.getAuthId()).child(id).child("seen").setValue(false, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError == null) {
                                        //add then add the given user as a friend of authenticated user
                                        FirebaseHelper.getRoot().child(FRIENDS_KEY).child(id).child(getAuthId()).child("seen").setValue(false, FirebaseHelper.getAuthId(), new Firebase.CompletionListener() {
                                            @Override
                                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                new RegisterKey(id,listener).execute();

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

            }
        });
    }
    static class RegisterKey extends AsyncTask<Void,Void,String>{
        Firebase.CompletionListener listener;
        String id;
        public RegisterKey(String id,Firebase.CompletionListener listener) {
            this.listener = listener;
            this.id = id;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String key = String.valueOf(UUID.randomUUID());
            return key;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            FirebaseHelper.getRoot().child(PRIVATE_CHAT).child(FirebaseHelper.getAuthId()).child(id).setValue(s, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    FirebaseHelper.getRoot().child(PRIVATE_CHAT).child(id).child(FirebaseHelper.getAuthId()).setValue(s, listener);
                }
            });
        }
    }

    //it will be executed when the friend request of a user to authenticated user is added
    private static class AddFriendRequestTask extends AsyncTask<Request, Void, Void> {

        @Override
        protected Void doInBackground(Request... params) {
            friendRequests.add(params[0]);
            return null;
        }
    }

    //it will be executed when the friend request from a user to authenticated user is removed
    private static class RemoveFriendRequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            for (int i = 0; i < friendRequests.size(); i++) {
                if (friendRequests.get(i).getId().equals(params[0])) {
                    friendRequests.remove(friendRequests.get(i));
                }
            }
            return null;
        }
    }

    //it will be executed to check whether the given user has sent friend request to authenticated user
    private static class GetFriendRequestTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            for (Request request : friendRequests) {
                if (request.getId().equals(id))
                    return true;
            }
            return false;
        }
    }

    //it will be executed when the friend of authenticated user is added
    private static class AddFriendTask extends AsyncTask<Friend, Void, Void> {
        @Override
        protected Void doInBackground(Friend... params) {
            friends.add(params[0]);
            return null;
        }
    }

    //it will be executed when the friend of authenticated user is removed
    private static class RemoveFriendTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            for (int i = 0; i < friends.size(); i++) {
                Friend friend = friends.get(i);
                if (friend.getId().equals(params[0])) {
                    friends.remove(friend);
                }
            }
            return null;
        }
    }

    //it will be executed to check if the given user is a friend of authenticated user
    private static class GetFriendTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            for (int i = 0; i < friends.size(); i++) {
                if (friends.get(i).getId().equals(id))
                    return true;
            }
            return false;
        }
    }

    private static class GetUserRequestTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String id = params[0];
            //friend requests of given user
            Map<String, Object> contactFriendRequests = null;
            if (getFriendRequestsSnapshot().child(id) != null) {
                contactFriendRequests = (Map<String, Object>) getFriendRequestsSnapshot().child(id).getValue();
            } else
                return false;
            if (contactFriendRequests != null)
                return contactFriendRequests.containsKey(getAuthId());
            return false;
        }
    }


}
