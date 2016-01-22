package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.GroupListAdapter;
import com.example.stranger.me.adapter.GroupRequestAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.modal.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class GroupRequestsFragment extends Fragment implements GroupListAdapter.OnGroupChangeListener {
    private static final String TAG = "GroupRequests";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private TextView mNoNewRequests;
    private GroupRequestAdapter mAdapter;
    private ArrayList<User> mRequests;
    private ChildEventListener mRequestListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new AddRequestTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(TAG, "onCancelled request");
        }
    };

    public GroupRequestsFragment() {
        // Required empty public constructor
    }

    public static GroupRequestsFragment newInstance() {
        GroupRequestsFragment fragment = new GroupRequestsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_requests, container, false);
        init(view);
        if (GroupHelper.getCurrentGroup() != null && GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), GroupHelper.getCurrentGroup()) == 3) {
            updateListener(GroupHelper.getCurrentGroup());
        } else {
            mNoNewRequests.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_requests_recyclerview);
        mNoNewRequests = (TextView) view.findViewById(R.id.no_requests);
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

    @Override
    public void onGroupChange() {
        String groupKey = GroupHelper.getCurrentGroup();
        if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), groupKey) == 3) {
            mNoNewRequests.setVisibility(View.GONE);
            updateListener(groupKey);
        } else {
            mNoNewRequests.setVisibility(View.VISIBLE);
        }
    }

    private void updateListener(String groupKey) {
        mRequests = new ArrayList<>();
        mAdapter = new GroupRequestAdapter(getActivity(), groupKey, mRequests);
        mRecyclerView.setAdapter(mAdapter);
        if (GroupHelper.getPreviousGroup() != null)//it will be null for first time
            FirebaseHelper.getRoot().child("group_requests").child(GroupHelper.getPreviousGroup()).removeEventListener(mRequestListener);

        FirebaseHelper.getRoot().child("group_requests").child(groupKey).startAt().orderByChild("seen").addChildEventListener(mRequestListener);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class AddRequestTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String userId = params[0];
            User user = FirebaseHelper.getUsers().child(userId).getValue(User.class);
            user.setId(userId);
            mRequests.add(user);
            return mRequests.indexOf(user);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mNoNewRequests.setVisibility(View.GONE);
            mAdapter.notifyItemInserted(integer);
        }
    }
}
