package com.example.stranger.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Farooq on 1/2/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<Message> mMessages;
    public Context mContext;

    public ChatAdapter(Context context, ArrayList<Message> messages) {
        mMessages = messages;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        if (message.getMessage() != null) {
            holder.mMessageText.setText(message.getMessage());
        } else if (message.getImageUrl() != null) {
            Picasso.with(mContext).load(message.getImageUrl()).into(holder.mChatImage);
        }

        holder.mMessageDate.setText(getFormattedDate(message.getTimestamp()));
        Picasso.with(mContext).load(FirebaseHelper.getProfileImage(message.getSender())).into(holder.mSenderProfileImage);
    }

    public String getFormattedDate(String time) {
        long currentTime = System.currentTimeMillis();
        long timestamp = Long.parseLong(time);
        SimpleDateFormat simpleDateFormat = null;
        if ((currentTime - timestamp) > (1000 * 60 * 60 * 24)) {//that means more than one day has passed
            simpleDateFormat = new SimpleDateFormat("EEE, hh:mm a");
        } else {
            simpleDateFormat = new SimpleDateFormat("hh:mm a");
        }
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = simpleDateFormat.format(new Date(timestamp));
        return formattedDate;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        return (message.getSender().equals(FirebaseHelper.getAuthId())) ? 0 : 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mSenderProfileImage;
        public RobotoTextView mMessageText;
        public RobotoTextView mMessageDate;
        public ImageView mChatImage;

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        public void init(View view) {
            mSenderProfileImage = (CircleImageView) view.findViewById(R.id.chat_msg_sender_image);
            mMessageText = (RobotoTextView) view.findViewById(R.id.chat_msg_text);
            mMessageDate = (RobotoTextView) view.findViewById(R.id.chat_msg_date);
            mChatImage = (ImageView) view.findViewById(R.id.chat_image);
        }
    }
}
