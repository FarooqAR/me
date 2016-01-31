package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.adapter.ContactListAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Friend;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


public class FriendsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private ContactListAdapter mAdapter;
    private RobotoEditText mFindFriend;
    private ImageButton mFindFriendBtn;
    private ImageButton mRefreshBtn;
    private ProgressBar mProgress;
    private Button mFindContactBtn;
    private TextView mNoFriend;
    private TextView mFriendsCount;
    private ChildEventListener mFriendsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new FriendAddTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new FriendRemoveTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private View.OnClickListener mFindFriendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = String.valueOf(mFindFriend.getText());
            if (text != null && !text.equals("")) {
                mProgress.setVisibility(View.VISIBLE);
                String[] textSplitted = text.split(" ");
                removeListener();
                if (textSplitted.length > 1) {
                    new FindFriendTask().execute(textSplitted[0], textSplitted[1]);
                } else {
                    new FindFriendTask().execute(textSplitted[0], "?/");
                }
            }
        }
    };

    public FriendsListFragment() {
        // Required empty public constructor
    }


    public static FriendsListFragment newInstance() {
        FriendsListFragment fragment = new FriendsListFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers = new ArrayList<>();
        mAdapter = new ContactListAdapter(getActivity(), mUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        init(view);
        mUsers.clear();
        mAdapter.notifyDataSetChanged();
        mFriendsCount.setText("");
        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mFindFriendBtn.setOnClickListener(mFindFriendBtnListener);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsers.clear();
                mAdapter.notifyDataSetChanged();
                removeListener();
                addListener();
            }
        });
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIENDS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(FirebaseHelper.getAuthId()).exists()) {
                    mFindContactBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mFindContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity activity = (HomeActivity) getActivity();
                activity.setFragment(activity.getFragmentsSize() - 2);
            }
        });
        return view;
    }
    private void removeListener(){
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIENDS_KEY).child(FirebaseHelper.getAuthId()).removeEventListener(mFriendsListener);
    }
    private void addListener(){
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIENDS_KEY).child(FirebaseHelper.getAuthId()).addChildEventListener(mFriendsListener);
    }
    @Override
    public void onResume() {
        super.onResume();
        mUsers.clear();
        addListener();
    }

    @Override
    public void onPause() {
        //if the user logouts while he is on this fragment, don't call the listener
        if(FirebaseHelper.getAuthId() != null)
        removeListener();
        super.onPause();
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.find_friend_recyclerview);
        mFindFriend = (RobotoEditText) view.findViewById(R.id.find_friend_edittext);
        mFindFriendBtn = (ImageButton) view.findViewById(R.id.action_find_friend);
        mRefreshBtn = (ImageButton) view.findViewById(R.id.action_refresh_friends);
        mProgress = (ProgressBar) view.findViewById(R.id.find_friend_progress);
        mNoFriend = (TextView) view.findViewById(R.id.no_friend);
        mFriendsCount = (TextView) view.findViewById(R.id.friends_count);
        mFindContactBtn = (Button) view.findViewById(R.id.find_contact_btn);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    //find user and add it to recyclerview
    public class FindFriendTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNoFriend.setVisibility(View.GONE);
            mFindContactBtn.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... text) {
            String text1 = text[0].toLowerCase();
            String text2 = text[1].toLowerCase();
            mUsers.clear();
            for (int i = 0; i < FirebaseHelper.getFriends().size(); i++) {
                Friend friend = FirebaseHelper.getFriends().get(i);
                User user = FirebaseHelper.getUsers().child(friend.getId()).getValue(User.class);
                user.setId(friend.getId());
                String first_name = (user.getFirstName() != null) ? user.getFirstName() : "";
                String last_name = (user.getLastName() != null) ? user.getLastName() : "";

                if (first_name.toLowerCase().contains(text1) || last_name.toLowerCase().contains(text1) ||
                        first_name.toLowerCase().contains(text2) || last_name.toLowerCase().contains(text2)) {
                    mUsers.add(user);
                }
            }
            return mUsers.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemRangeInserted(0, integer);
            mFriendsCount.setText(integer + " friends found");
            mProgress.setVisibility(View.GONE);
            if (integer == 0) {
                mNoFriend.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FriendRemoveTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String idOfRemovedFriend = strings[0];
            for (int i = 0; i < mUsers.size(); i++) {
                User user = mUsers.get(i);
                if (user.getId().equals(idOfRemovedFriend)) {
                    mUsers.remove(user);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                mAdapter.notifyItemRemoved(integer);
                mFriendsCount.setText(mUsers.size() + " friends");
            }
        }
    }

    private class FriendAddTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            String friendId = strings[0];
            User user = FirebaseHelper.getUsers().child(friendId).getValue(User.class);
            user.setId(friendId);
            mUsers.add(user);
            return mUsers.indexOf(user);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mFindContactBtn.setVisibility(View.GONE);
            mAdapter.notifyItemInserted(integer);
            mFriendsCount.setText(mUsers.size()+" friends");
        }
    }
}
