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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.ContactListAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Request;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


public class FriendRequestsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private ContactListAdapter mAdapter;
    private RobotoEditText mFindFriendRequest;
    private ImageButton mFindFriendRequestBtn;
    private ImageButton mRefreshBtn;
    private ProgressBar mProgress;
    private TextView mNoFriendRequest;
    private TextView mFriendRequestsCount;
    private ChildEventListener mFriendRequestsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new FriendRequestAddTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new FriendRequestRemoveTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private View.OnClickListener mFindFriendRequestsBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = String.valueOf(mFindFriendRequest.getText());
            if (text != null && !text.equals("")) {
                mProgress.setVisibility(View.VISIBLE);
                String[] textSplitted = text.split(" ");
                removeListener();
                if (textSplitted.length > 1) {
                    new FindFriendRequestTask().execute(textSplitted[0], textSplitted[1]);
                } else {
                    new FindFriendRequestTask().execute(textSplitted[0], "?/");
                }
            }
        }
    };

    public FriendRequestsFragment() {
        // Required empty public constructor
    }


    public static FriendRequestsFragment newInstance() {
        FriendRequestsFragment fragment = new FriendRequestsFragment();

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
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        init(view);
        mUsers.clear();
        mAdapter.notifyDataSetChanged();
        mFriendRequestsCount.setText("");
        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mFindFriendRequestBtn.setOnClickListener(mFindFriendRequestsBtnListener);
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsers.clear();
                mAdapter.notifyDataSetChanged();
                removeListener();
                addListener();
            }
        });
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(FirebaseHelper.getAuthId()).exists()) {
                    mNoFriendRequest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }
    private void removeListener(){
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).child(FirebaseHelper.getAuthId()).removeEventListener(mFriendRequestsListener);
    }
    private void addListener(){
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).child(FirebaseHelper.getAuthId()).addChildEventListener(mFriendRequestsListener);
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.find_friend_request_recyclerview);
        mFindFriendRequest = (RobotoEditText) view.findViewById(R.id.find_friend_request_edittext);
        mFindFriendRequestBtn = (ImageButton) view.findViewById(R.id.action_find_friend_request);
        mRefreshBtn = (ImageButton) view.findViewById(R.id.action_refresh_friend_request);
        mProgress = (ProgressBar) view.findViewById(R.id.find_friend_request_progress);
        mNoFriendRequest = (TextView) view.findViewById(R.id.no_friend_request);
        mFriendRequestsCount = (TextView) view.findViewById(R.id.friend_requests_count);
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
    public class FindFriendRequestTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //hide no new requests
        }

        @Override
        protected Integer doInBackground(String... text) {
            String text1 = text[0].toLowerCase();
            String text2 = text[1].toLowerCase();
            mUsers.clear();
            for (int i = 0; i < FirebaseHelper.getFriendRequests().size(); i++) {
                Request request = FirebaseHelper.getFriendRequests().get(i);
                User user = FirebaseHelper.getUsers().child(request.getId()).getValue(User.class);
                user.setId(request.getId());
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
            mFriendRequestsCount.setText(integer + " requests found");
            mProgress.setVisibility(View.GONE);
            if (integer == 0) {
                mNoFriendRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FriendRequestRemoveTask extends AsyncTask<String, Void, Integer> {

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
                mFriendRequestsCount.setText(mUsers.size() + " requests");
                mNoFriendRequest.setVisibility(View.GONE);
            }
        }
    }

    private class FriendRequestAddTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            String requestId = strings[0];
            User user = FirebaseHelper.getUsers().child(requestId ).getValue(User.class);
            user.setId(requestId );
            mUsers.add(user);
            return mUsers.indexOf(user);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemInserted(integer);
            mFriendRequestsCount.setText(mUsers.size() + " requests");
            mNoFriendRequest.setVisibility(View.GONE);
        }
    }
}
