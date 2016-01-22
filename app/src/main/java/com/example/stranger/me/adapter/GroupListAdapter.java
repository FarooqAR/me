package com.example.stranger.me.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.modal.Group;
import com.example.stranger.me.widget.RobotoTextView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Farooq on 1/19/2016.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    public static Context mContext;
    private ArrayList<Group> mGroups;
    private ViewPager mParentViewPager;
    private PagerAdapter mParentViewPagerAdapter;
    private OnGroupChangeListener[] listeners;
    private HomeActivity mReference;
    public void setParentViewPager(ViewPager viewPager){
        mParentViewPager = viewPager;
        mParentViewPagerAdapter = (PagerAdapter) viewPager.getAdapter();
        listeners = Arrays.copyOf(mParentViewPagerAdapter.getFragments(),
                mParentViewPagerAdapter.getFragments().length,
                OnGroupChangeListener[].class);
    }
    public GroupListAdapter(Context context, ArrayList<Group> mGroups) {
        mContext = context;
        this.mGroups = mGroups;
        mReference = (HomeActivity) context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Group group = mGroups.get(position);
        holder.name.setText(group.getName());
        holder.desc.setText(group.getDescription());
        final String group_key = group.getKey();
        long accessLevel = GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), group_key);
        switch ((int) accessLevel){
            case 0:
            case 1:
            case 2:
                holder.enableLeaveGroup();
                holder.leave_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.showLeaveGroupMenu(v,group_key);
                    }
                });
                break;
            case 3:
                holder.enableDeleteGroup();
                holder.delete_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.showDeleteGroupPopup(position,v,group_key);
                    }
                });
                break;
            default:
                if(GroupHelper.isRequested(FirebaseHelper.getAuthId(),group_key)){
                    holder.enableRequestSent();
                }
                else {
                    holder.enableJoinGroup();
                    holder.join_group.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.showJoinGroupProgress();
                            GroupHelper.sendRequest(FirebaseHelper.getAuthId(), group_key, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if (firebaseError == null) {
                                        holder.hideJoinGroupProgress();
                                        holder.enableRequestSent();
                                    }
                                }
                            });
                        }
                    });
                }
        }
        holder.see_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentViewPager.setCurrentItem(1);
                setToolbarText(group.getName());
                GroupHelper.setPreviousGroup(GroupHelper.getCurrentGroup());
                GroupHelper.setCurrentGroup(group_key);
                callGroupChangeOnListeners();
            }
        });
        holder.see_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParentViewPager.setCurrentItem(0);
                setToolbarText(group.getName());
                GroupHelper.setPreviousGroup(GroupHelper.getCurrentGroup());
                GroupHelper.setCurrentGroup(group_key);
                callGroupChangeOnListeners();
            }
        });
        holder.see_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParentViewPager.setCurrentItem(2);
                setToolbarText(group.getName());
                GroupHelper.setPreviousGroup(GroupHelper.getCurrentGroup());
                GroupHelper.setCurrentGroup(group_key);
                callGroupChangeOnListeners();
            }
        });
    }
    public void callGroupChangeOnListeners(){

        for (OnGroupChangeListener listener : listeners) {
            listener.onGroupChange();
        }
    }
    public void setToolbarText(String groupName){
        mReference.getSupportActionBar().setTitle(groupName);
    }
    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView request_sent;
        RobotoTextView name;
        RobotoTextView desc;
        Button delete_group;//only member of accesslevel 3 can see this
        Button join_group;
        Button leave_group;
        Button see_posts;
        Button see_chat;
        Button see_members;
        ProgressBar join_group_progress;
        ProgressBar leave_group_progress;
        ProgressBar delete_group_progress;
        public ViewHolder(View itemView) {
            super(itemView);
            request_sent = (TextView) itemView.findViewById(R.id.join_group_request_sent);
            delete_group = (Button) itemView.findViewById(R.id.action_delete_group);
            join_group = (Button) itemView.findViewById(R.id.action_join_group);
            leave_group = (Button) itemView.findViewById(R.id.action_leave_group);
            see_chat = (Button) itemView.findViewById(R.id.action_see_group_chat);
            see_posts = (Button) itemView.findViewById(R.id.action_see_group_posts);
            see_members= (Button) itemView.findViewById(R.id.action_see_group_members);
            join_group_progress = (ProgressBar) itemView.findViewById(R.id.action_join_group_progress);
            leave_group_progress= (ProgressBar) itemView.findViewById(R.id.action_leave_group_progress);
            delete_group_progress= (ProgressBar) itemView.findViewById(R.id.action_delete_group_progress);
            name = (RobotoTextView) itemView.findViewById(R.id.item_group_name);
            desc = (RobotoTextView) itemView.findViewById(R.id.item_group_desc);
        }
        public void enableRequestSent(){
            request_sent.setVisibility(View.VISIBLE);
            delete_group.setVisibility(View.GONE);
            join_group.setVisibility(View.GONE);
            leave_group.setVisibility(View.GONE);
            join_group_progress.setVisibility(View.GONE);
            leave_group_progress.setVisibility(View.GONE);
            delete_group_progress.setVisibility(View.GONE);
        }
        public void enableDeleteGroup(){
            request_sent.setVisibility(View.GONE);
            delete_group.setVisibility(View.VISIBLE);
            join_group.setVisibility(View.GONE);
            leave_group.setVisibility(View.GONE);
            join_group_progress.setVisibility(View.GONE);
            leave_group_progress.setVisibility(View.GONE);
            delete_group_progress.setVisibility(View.GONE);
        }
        public void enableLeaveGroup(){
            request_sent.setVisibility(View.GONE);
            delete_group.setVisibility(View.GONE);
            join_group.setVisibility(View.GONE);
            leave_group.setVisibility(View.VISIBLE);
            join_group_progress.setVisibility(View.GONE);
            leave_group_progress.setVisibility(View.GONE);
            delete_group_progress.setVisibility(View.GONE);
        }
        public void enableJoinGroup(){
            request_sent.setVisibility(View.GONE);
            delete_group.setVisibility(View.GONE);
            join_group.setVisibility(View.VISIBLE);
            leave_group.setVisibility(View.GONE);
            join_group_progress.setVisibility(View.GONE);
            leave_group_progress.setVisibility(View.GONE);
            delete_group_progress.setVisibility(View.GONE);
        }
        public void showDeleteGroupProgress(){
            delete_group.setEnabled(false);
            delete_group_progress.setVisibility(View.VISIBLE);
            see_chat.setEnabled(false);
            see_posts.setEnabled(false);

        }
        public void hideDeleteGroupProgress(){
            delete_group.setEnabled(true);
            see_chat.setEnabled(true);
            see_posts.setEnabled(true);
            see_members.setEnabled(true);
            delete_group_progress.setVisibility(View.GONE);
        }
        public void showLeaveGroupProgress(){
            leave_group_progress.setVisibility(View.VISIBLE);
            see_chat.setEnabled(false);
            see_posts.setEnabled(false);
            see_members.setEnabled(false);
            leave_group.setEnabled(false);
        }
        public void hideLeaveGroupProgress(){
            leave_group_progress.setVisibility(View.GONE);
            leave_group.setEnabled(true);
            see_chat.setEnabled(true);
            see_posts.setEnabled(true);
            see_members.setEnabled(true);
        }
        public void showJoinGroupProgress(){
            join_group_progress.setVisibility(View.VISIBLE);
            join_group.setEnabled(false);
            see_chat.setEnabled(false);
            see_posts.setEnabled(false);
            see_members.setEnabled(false);

        }
        public void hideJoinGroupProgress(){
            join_group_progress.setVisibility(View.GONE);
            join_group.setEnabled(true);
            see_chat.setEnabled(true);
            see_posts.setEnabled(true);
            see_members.setEnabled(true);
        }
        public void showDeleteGroupPopup(final int position,View v, final String group_key) {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.inflate(R.menu.action_delete_group);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_delete_group) {
                        showDeleteGroupProgress();
                        showGroupDeletionConfirmationDialog(position, group_key);
                        return true;
                    }

                    return false;
                }
            });
            popupMenu.show();
        }
        public void showGroupDeletionConfirmationDialog(final int position,final String groupKey){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this group?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GroupHelper.deleteGroup(groupKey, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    if(firebaseError==null) {
                                        enableJoinGroup();
                                        hideDeleteGroupProgress();
                                        mGroups.remove(position);
                                        GroupListAdapter.this.notifyItemRemoved(position);
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            enableDeleteGroup();
                            hideDeleteGroupProgress();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        public void showLeaveGroupMenu(View v, final String groupkey){
            PopupMenu menu = new PopupMenu(mContext,v);
            menu.inflate(R.menu.action_leave_group);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_leave_group) {
                        showLeaveGroupProgress();
                        GroupHelper.removeMember(FirebaseHelper.getAuthId(), groupkey, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError == null) {
                                    hideLeaveGroupProgress();
                                    enableJoinGroup();
                                }
                            }
                        });
                    }
                    return true;
                }
            });
            menu.show();
        }
    }
    public interface OnGroupChangeListener{
        void onGroupChange();
    }
}
