package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.ChatAdapter;
import com.example.stranger.me.adapter.ChatFriendListAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";
    private RecyclerView mRecyclerView;
    private ListView mFriendsListView;
    private RelativeLayout mMainContent;
    private LinearLayout mRootView;
    private ArrayList<User> mUsers;

    private ChatAdapter mChatAdapter;
    private ChatFriendListAdapter mChatFriendListAdapter;
    private OnFragmentInteractionListener mListener;
    private ChildEventListener mUsersDataChangeListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if(FirebaseHelper.isFriend(dataSnapshot.getKey()))
                new ChangeUserInChatFriendListTask().execute(dataSnapshot);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            if(FirebaseHelper.isFriend(dataSnapshot.getKey()))
                new RemoveUserFromChatFriendListTask().execute(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private ChildEventListener mFriendsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new AddFriendToChatListTask().execute((String)dataSnapshot.getValue());

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new RemoveFriendFromList().execute((String)dataSnapshot.getValue());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers = new ArrayList<>();
        mChatFriendListAdapter = new ChatFriendListAdapter(getActivity(),R.layout.chat_friends_list_item,mUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);
        mFriendsListView.setAdapter(mChatFriendListAdapter);
        FirebaseHelper.getRoot().child("users").addChildEventListener(mUsersDataChangeListener);
        FirebaseHelper.getRoot().child("friends").child(FirebaseHelper.getAuthId()).addChildEventListener(mFriendsListener);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.friends_chat_recyclerview);
        mFriendsListView = (ListView) view.findViewById(R.id.friends_chat_list);
        mMainContent = (RelativeLayout) view.findViewById(R.id.friends_chat_content);
        mRootView = (LinearLayout) view.findViewById(R.id.root_view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class RemoveUserFromChatFriendListTask extends AsyncTask<DataSnapshot,Void,Integer>{

        @Override
        protected Integer doInBackground(DataSnapshot... params) {
            for (int i = 0; i < mUsers.size(); i++){
                User user = mUsers.get(i);
                if(user.getId().equals(params[0].getKey())){
                    mUsers.remove(i);
                    Log.d(TAG, "User removed from list: id=" + user.getId() + ", name=" + user.getFirstName());
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer != null){
                mChatFriendListAdapter.notifyDataSetChanged();
                Log.d(TAG, "adapter notified [item removed]");
            }
        }
    }
    public class ChangeUserInChatFriendListTask extends AsyncTask<DataSnapshot,Void,Integer>{

        @Override
        protected Integer doInBackground(DataSnapshot... params) {
            String id = params[0].getKey();
            for(int i = 0; i< mUsers.size();i++){
                User previous_data = mUsers.get(i);
                User current_data = params[0].getValue(User.class);
                current_data.setId(id);
                if(previous_data.getId().equals(id)){
                    mUsers.set(i,current_data);
                    Log.d(TAG, "User changed in list: id=" + current_data.getId() + ", name=" + current_data.getFirstName()+", online="+current_data.isOnline());
                    return i;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer != null){
                mChatFriendListAdapter.notifyDataSetChanged();
                Log.d(TAG, "adapter notified [item changed]");
            }
        }
    }

    private class RemoveFriendFromList extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            String idOfFriendToRemove = params[0];
            for (int i =0;i<mUsers.size();i++){
                User user = mUsers.get(i);
                if(user.getId().equals(idOfFriendToRemove)){
                    mUsers.remove(i);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer != null){
                mChatFriendListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class AddFriendToChatListTask extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            String idOfFriendToAdd = params[0];
            for (int i=0;i<mUsers.size();i++){
                //check whether friend is already added
                if(mUsers.get(i).getId().equals(idOfFriendToAdd)){//if friend is already added
                    return null;
                }
            }
            //friend will be added only if the above for loop completed without returning null
            User user = FirebaseHelper.getUsers().child(idOfFriendToAdd).getValue(User.class);
            user.setId(idOfFriendToAdd);
            mUsers.add(user);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer!=null){
                mChatFriendListAdapter.notifyDataSetChanged();
            }
        }
    }
}
