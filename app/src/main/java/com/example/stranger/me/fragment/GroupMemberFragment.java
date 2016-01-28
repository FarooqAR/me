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

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.GroupMembersAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class GroupMemberFragment extends Fragment {
    private static final String GROUP_KEY = "group_key";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RobotoEditText mEditText;
    private ImageButton mFindBtn;
    private ImageButton mRefreshBtn;
    private GroupMembersAdapter mAdapter;
    private String mGroupKey;
    private ArrayList<User> mMembers;
    private ChildEventListener mMemberListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String userId = dataSnapshot.getKey();
            User user = FirebaseHelper.getUsers().child(userId).getValue(User.class);
            user.setId(userId);
            new MemberAddTask().execute(user);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new MemberRemoveTask().execute(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private View.OnClickListener mRefreshBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMembers.clear();
            mAdapter.notifyDataSetChanged();
            FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(mGroupKey).removeEventListener(mMemberListener);
            FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(mGroupKey).addChildEventListener(mMemberListener);
        }
    };
    private View.OnClickListener mFindBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = String.valueOf(mEditText.getText());
            if(!text.equals("")) {
                mMembers.clear();
                mAdapter.notifyDataSetChanged();
                FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(mGroupKey).removeEventListener(mMemberListener);
                String[] splitted = text.split(" ");
                if (splitted.length == 1) {
                    new MemberFindTask().execute(splitted[0]);
                } else if (splitted.length == 2) {
                    new MemberFindTask().execute(splitted[0], splitted[1]);
                }
            }

        }
    };

    public GroupMemberFragment() {
        // Required empty public constructor
    }

    public static GroupMemberFragment newInstance(String mGroupKey) {
        GroupMemberFragment fragment = new GroupMemberFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_KEY, mGroupKey);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupMemberFragment newInstance() {
        GroupMemberFragment fragment = new GroupMemberFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMembers = new ArrayList<>();
        if (getArguments() != null) {
            mGroupKey = getArguments().getString(GROUP_KEY, mGroupKey);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(mGroupKey).removeEventListener(mMemberListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_member, container, false);
        init(view);
        mAdapter = new GroupMembersAdapter(getActivity(), mGroupKey, mMembers);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mFindBtn.setOnClickListener(mFindBtnListener);
        mRefreshBtn.setOnClickListener(mRefreshBtnListener);

        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_MEMBERS).child(mGroupKey).addChildEventListener(mMemberListener);
        return view;
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_members_recyclerview);
        mEditText = (RobotoEditText) view.findViewById(R.id.add_member_edittext);
        mFindBtn = (ImageButton) view.findViewById(R.id.action_find_member);
        mRefreshBtn = (ImageButton) view.findViewById(R.id.action_refresh_member);
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

    public class MemberAddTask extends AsyncTask<User, Void, Integer> {

        @Override
        protected Integer doInBackground(User... users) {
            User user = users[0];
            mMembers.add(user);
            return mMembers.indexOf(user);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemInserted(integer);
        }
    }

    public class MemberFindTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            String text1 = null;
            String text2 = null;
            if (strings.length > 1) {
                text1 = strings[0].toLowerCase();
                text2 = strings[1].toLowerCase();
            } else if (strings.length == 1) {
                text1 = strings[0].toLowerCase();
            }

            for (DataSnapshot dataSnapshot : GroupHelper.getMEMBERS().child(mGroupKey).getChildren()) {
                String id = dataSnapshot.getKey();
                User user = FirebaseHelper.getUsers().child(id).getValue(User.class);
                user.setId(id);
                String firstname = user.getFirstName().toLowerCase();
                String lastname = user.getLastName().toLowerCase();
                if (text1 != null && text2 != null) {
                    if (firstname.contains(text1) || firstname.contains(text2) || lastname.contains(text1) || lastname.contains(text2)) {
                        mMembers.add(user);
                    }
                } else if (text1 != null) {
                    if (firstname.contains(text1) || lastname.contains(text1)) {
                        mMembers.add(user);
                    }
                }
            }
            return mMembers.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemRangeInserted(0, integer);
            if (integer == 0) SnackbarHelper.create(mRecyclerView, "No such member found").show();

        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private class MemberRemoveTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            String userId = strings[0];
            for (int i = 0; i < mMembers.size(); i++) {
                User user = mMembers.get(i);
                if (user.getId().equals(userId)) {
                    mMembers.remove(i);
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
            }
        }
    }
}
