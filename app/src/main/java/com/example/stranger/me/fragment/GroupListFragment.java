package com.example.stranger.me.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.adapter.GroupListAdapter;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.Group;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

public class GroupListFragment extends Fragment implements GroupListAdapter.OnGroupChangeListener{
    private static ViewPager mParentViewPager;
    private OnFragmentInteractionListener mListener;
    private RobotoEditText mFindGroupEditText;
    private ImageButton mFindGroupBtn;
    private RecyclerView mRecyclerView;
    private GroupListAdapter mAdapter;
    private ArrayList<Group> mGroups;
    private FloatingActionButton mAddGroupBtn;
    private RelativeLayout mRootView;
    private View.OnClickListener mFindGroupBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = String.valueOf(mFindGroupEditText.getText());
            if (!text.equals("")) {
                mGroups.clear();
                String[] splitted = text.split(" ");
                if (splitted.length > 1) {
                    new GroupFindTask().execute(splitted[0], splitted[1]);
                } else {
                    new GroupFindTask().execute(splitted[0]);
                }
            }
        }
    };
    private View.OnClickListener mAddGroupBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openAddGroupDialog();
        }
    };

    public GroupListFragment() {
        // Required empty public constructor
    }

    private void openAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_group_dialog, null, false);
        builder.setTitle("Create Group")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RobotoEditText group_name = (RobotoEditText) view.findViewById(R.id.dialog_group_name);
                        RobotoEditText group_desc = (RobotoEditText) view.findViewById(R.id.dialog_group_description);
                        String groupName = String.valueOf(group_name.getText());
                        String groupDesc = String.valueOf(group_desc.getText());
                        if (isValidGroup(groupName, groupDesc)) {
                            GroupHelper.addGroup(groupName, groupDesc, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError == null) {
                                        //group is added with current user as admin
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isValidGroup(String groupName, String groupDesc) {
        if (!groupName.equals("") && !groupDesc.equals("")) {
            if (groupName.length() > 4 && groupDesc.length() > 4) {
                return true;
            } else {
                SnackbarHelper.create(mRootView, "Minimum 5 letters allowed").show();
            }
        }
        return false;
    }

    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();

        return fragment;
    }
    public void setParentViewPager(ViewPager viewPager){
        mParentViewPager = viewPager;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroups=new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        init(view);
        mFindGroupBtn.setOnClickListener(mFindGroupBtnListener);
        mAddGroupBtn.setOnClickListener(mAddGroupBtnListener);
        mAdapter = new GroupListAdapter(getActivity(), mGroups);
        mAdapter.setParentViewPager(mParentViewPager);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        if(GroupHelper.getGROUPS()!=null){
            new GroupAddTask().execute();
        }
        return view;
    }

    private void init(View view) {
        mFindGroupBtn = (ImageButton) view.findViewById(R.id.action_find_group);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.groups_recyclerview);
        mFindGroupEditText = (RobotoEditText) view.findViewById(R.id.find_group_edittext);
        mAddGroupBtn = (FloatingActionButton) view.findViewById(R.id.action_group_add);
        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
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

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //find group and add it to recyclerview

    public class GroupFindTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... text) {
            String text1 = text[0].toLowerCase();
            String text2 = (text.length>1)?text[1].toLowerCase():"?/>";
            for (DataSnapshot groupSnapshot : GroupHelper.getGROUPS().getChildren()) {
                Group group = groupSnapshot.getValue(Group.class);
                group.setKey(groupSnapshot.getKey());
                if (text.length > 1) {
                    if (group.getName().toLowerCase().contains(text1) || group.getName().toLowerCase().contains(text1)
                            || group.getName().toLowerCase().contains((text2)) || group.getName().toLowerCase().contains((text2))) {
                        mGroups.add(group);
                    }
                } else if (text.length > 0) {
                    if (group.getName().toLowerCase().contains(text1)) {
                        mGroups.add(group);
                    }
                }
            }
            return mGroups.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemRangeInserted(0, integer);
        }
    }

    public class GroupAddTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... text) {
            mGroups.clear();
            for (DataSnapshot groupSnapshot : GroupHelper.getGROUPS().getChildren()) {
                Group group = groupSnapshot.getValue(Group.class);
                group.setKey(groupSnapshot.getKey());
                mGroups.add(group);
            }
            return mGroups.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyItemRangeInserted(0,integer);
        }
    }
}
