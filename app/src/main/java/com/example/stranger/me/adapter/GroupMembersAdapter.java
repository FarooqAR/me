package com.example.stranger.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Farooq on 1/20/2016.
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<User> members;
    private String groupKey;

    public GroupMembersAdapter(Context mContext, String groupKey, ArrayList<User> members) {
        this.mContext = mContext;
        this.members = members;
        this.groupKey = groupKey;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_member_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = members.get(position);
        Picasso.with(mContext).load(user.getProfileImageURL()).into(holder.image);
        holder.name.setText(user.getFirstName() + " " + user.getLastName());
        if (user.isOnline()) {
            holder.status.setVisibility(View.VISIBLE);
        } else {
            holder.status.setVisibility(View.INVISIBLE);
        }
        if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), groupKey) == 3) {
            if(user.getId().equals(FirebaseHelper.getAuthId())){
                holder.remove.setEnabled(false);
                holder.remove.setText("Admin");
            }
            else{
                holder.remove.setEnabled(true);
                holder.remove.setText("Remove");
            }
            holder.remove.setVisibility(View.VISIBLE);
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.remove.setEnabled(false);
                    GroupHelper.removeMember(user.getId(), groupKey, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            holder.remove.setEnabled(true);
                            if(firebaseError==null){
                                members.remove(position);
                                GroupMembersAdapter.this.notifyItemRemoved(position);
                            }
                        }
                    });
                }
            });
        } else {
            holder.remove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView status;
        CircleImageView image;
        RobotoTextView name;
        Button remove;

        public ViewHolder(View itemView) {
            super(itemView);
            status = (ImageView) itemView.findViewById(R.id.item_group_member_status);
            image = (CircleImageView) itemView.findViewById(R.id.item_group_member_image);
            name = (RobotoTextView) itemView.findViewById(R.id.item_group_member_name);
            remove = (Button) itemView.findViewById(R.id.item_group_member_remove);
        }
    }
}
