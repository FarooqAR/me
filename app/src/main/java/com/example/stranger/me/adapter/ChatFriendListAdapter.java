package com.example.stranger.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.stranger.me.R;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farooq on 1/16/2016.
 */
public class ChatFriendListAdapter extends ArrayAdapter<User> {
    private ArrayList<User> mUsers;
    public Context mContext;
    public ChatFriendListAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        mUsers = (ArrayList<User>) objects;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public int getPosition(User item) {
        return mUsers.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_friends_list_item, parent, false);
            holder.chat_status = (ImageView) convertView.findViewById(R.id.chat_friend_status);
            holder.username = (RobotoTextView) convertView.findViewById(R.id.chat_friend_name);
            holder.profile_image = (CircleImageView) convertView.findViewById(R.id.chat_friend_icon);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        User user = getItem(position);
        if(user.isOnline()){
            holder.chat_status.setVisibility(View.VISIBLE);
        }
        else{
            holder.chat_status.setVisibility(View.INVISIBLE);
        }
        holder.username.setText(user.getFirstName());
        Picasso.with(mContext).load(user.getProfileImageURL()).into(holder.profile_image);
        return convertView;
    }
    static class ViewHolder {
        RobotoTextView username;
        CircleImageView  profile_image;
        ImageView chat_status;
    }
}
