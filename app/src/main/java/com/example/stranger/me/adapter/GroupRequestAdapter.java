package com.example.stranger.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stranger.me.R;
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
public class GroupRequestAdapter extends RecyclerView.Adapter<GroupRequestAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<User> mRequests;
    private String mGroupKey;

    public GroupRequestAdapter(Context mContext, String groupKey, ArrayList<User> mRequests) {
        this.mContext = mContext;
        this.mRequests = mRequests;
        this.mGroupKey = groupKey;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final User user = mRequests.get(position);
        Picasso.with(mContext).load(user.getProfileImageURL()).into(holder.image);
        holder.name.setText(user.getFirstName() + " " + user.getLastName());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.add.setEnabled(false);
                GroupHelper.addMember(user.getId(), mGroupKey, 2, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            holder.add.setEnabled(true);
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return mRequests.size();
    }

class ViewHolder extends RecyclerView.ViewHolder {
    CircleImageView image;
    RobotoTextView name;
    Button add;

    public ViewHolder(View itemView) {
        super(itemView);
        image = (CircleImageView) itemView.findViewById(R.id.item_group_request_image);
        name = (RobotoTextView) itemView.findViewById(R.id.item_group_request_name);
        add = (Button) itemView.findViewById(R.id.item_group_request_add);

    }
}
}
