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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.GroupRequestAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.modal.User;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class GroupRequestsFragment extends Fragment{
    private static final String TAG = "GroupRequests";
    private static final String GROUP_KEY = "group_key";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private TextView mNoNewRequests;
    private TextView mAccessRestrict;
    private GroupRequestAdapter mAdapter;
    private LinearLayout mRequestsProgress;
    private String mGroupKey;
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
            new RemoveRequestTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    public GroupRequestsFragment() {
        // Required empty public constructor
    }

    public static GroupRequestsFragment newInstance(String mGroupKey) {
        GroupRequestsFragment fragment = new GroupRequestsFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_KEY, mGroupKey);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupRequestsFragment newInstance() {
        GroupRequestsFragment fragment = new GroupRequestsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            mGroupKey = getArguments().getString(GROUP_KEY);
        }
        mRequests = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_requests, container, false);
        init(view);
        if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), mGroupKey) != -1) {
            mAccessRestrict.setVisibility(View.GONE);
            FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_REQUESTS).child(mGroupKey).orderByChild("seen")
                    .addChildEventListener(mRequestListener);
        } else {
            mAccessRestrict.setVisibility(View.VISIBLE);
            mRequestsProgress.setVisibility(View.GONE);
        }
        if(!GroupHelper.getGroupRequests().child(mGroupKey).exists()){
            mNoNewRequests.setVisibility(View.VISIBLE);
            mRequestsProgress.setVisibility(View.GONE);
        }
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new GroupRequestAdapter(getActivity(), mGroupKey, mRequests);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_REQUESTS).child(mGroupKey).orderByChild("seen")
                .removeEventListener(mRequestListener);
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_requests_recyclerview);
        mNoNewRequests = (TextView) view.findViewById(R.id.no_requests);
        mAccessRestrict = (TextView) view.findViewById(R.id.access_restrict);
        mRequestsProgress = (LinearLayout) view.findViewById(R.id.group_requests_progress);
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
            mRequestsProgress.setVisibility(View.GONE);
            mAdapter.notifyItemInserted(integer);
        }
    }

    private class RemoveRequestTask extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            String userId = strings[0];
            for (int i=0;i<mRequests.size();i++){
                User user = mRequests.get(i);
                if(user.getId().equals(userId)) {
                    mRequests.remove(i);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer!=null){
                mAdapter.notifyItemRemoved(integer);
            }
        }
    }
}
