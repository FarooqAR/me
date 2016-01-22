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
import android.widget.TextView;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.GroupListAdapter.OnGroupChangeListener;
import com.example.stranger.me.adapter.GroupMembersAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.DataSnapshot;

import java.util.ArrayList;


public class GroupMemberFragment extends Fragment implements OnGroupChangeListener {
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RobotoEditText mEditText;
    private ImageButton mFindBtn;
    private GroupMembersAdapter mAdapter;
    private ArrayList<User> members;
    private TextView mSelectGroup;
    private View.OnClickListener mFindBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (GroupHelper.getCurrentGroup()!= null) {
                String text = String.valueOf(mEditText.getText());
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

    public static GroupMemberFragment newInstance() {
        GroupMemberFragment fragment = new GroupMemberFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        members = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_member, container, false);
        init(view);
        if(GroupHelper.getCurrentGroup()!=null){
            members.clear();
            mSelectGroup.setVisibility(View.GONE);
            mAdapter = new GroupMembersAdapter(getActivity(),GroupHelper.getCurrentGroup(),members);
            new MemberAddTask().execute(GroupHelper.getCurrentGroup());
        }

        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mFindBtn.setOnClickListener(mFindBtnListener);
        
        return view;
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_members_recyclerview);
        mEditText = (RobotoEditText) view.findViewById(R.id.add_member_edittext);
        mFindBtn = (ImageButton) view.findViewById(R.id.action_find_member);
        mSelectGroup = (TextView) view.findViewById(R.id.select_group);
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
        mSelectGroup.setVisibility(View.GONE);
        members.clear();
        mAdapter = new GroupMembersAdapter(getActivity(), GroupHelper.getCurrentGroup(), members);
        new MemberAddTask().execute(GroupHelper.getCurrentGroup());
    }

    public class MemberAddTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            members.clear();
            String groupKey = strings[0];
            for (DataSnapshot dataSnapshot : GroupHelper.getMEMBERS().child(groupKey).getChildren()) {
                String id = dataSnapshot.getKey();
                User user = FirebaseHelper.getUsers().child(id).getValue(User.class);
                user.setId(id);
                members.add(user);
            }
            return members.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mRecyclerView.setAdapter(mAdapter);
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
            members.clear();
            for (DataSnapshot dataSnapshot : GroupHelper.getMEMBERS().child(GroupHelper.getCurrentGroup()).getChildren()) {
                String id = dataSnapshot.getKey();
                User user = FirebaseHelper.getUsers().child(id).getValue(User.class);
                user.setId(id);
                String firstname = user.getFirstName().toLowerCase();
                String lastname = user.getLastName().toLowerCase();
                if (text1 != null && text2 != null) {
                    if (firstname.contains(text1) || firstname.contains(text2) || lastname.contains(text1) || lastname.contains(text2)) {
                        members.add(user);
                    }
                } else if (text1 != null) {
                    if (firstname.contains(text1) || lastname.contains(text1)) {
                        members.add(user);
                    }
                }
            }
            return members.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mSelectGroup.setVisibility(View.GONE);
            mAdapter.notifyItemRangeInserted(0, integer);
            if (integer == 0) SnackbarHelper.create(mRecyclerView,"No such member found").show();

        }
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
