package com.example.stranger.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.stranger.me.R;
import com.example.stranger.me.activity.MapsActivity;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;
import com.squareup.picasso.Callback;
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
    private RecyclerView mRecyclerView;
    public ChatAdapter(Context context, ArrayList<Message> messages) {
        mMessages = messages;
        mContext = context;
    }
    public void setRecyclerView(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Message message = mMessages.get(position);
        if (message.getMessage() != null) {
            holder.showMessageView();
            holder.mMessageText.setText(message.getMessage());
        } else if (message.getImageUrl() != null) {
            holder.showMessageImage();
            Picasso.with(mContext).load(message.getImageUrl()).into(holder.mChatImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.mImageProgress.setVisibility(View.GONE);
                    if(mRecyclerView != null) mRecyclerView.scrollToPosition(position);
                }

                @Override
                public void onError() {

                }
            });

        }
        else if(message.getLocationLat()!=null ){
            holder.showMessageMapBtn();
            holder.mChatMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, MapsActivity.class);
                    intent.putExtra("location",message.getLocation());
                    intent.putExtra("locationLat",message.getLocationLat());
                    intent.putExtra("locationLong",message.getLocationLong());
                    mContext.startActivity(intent);
                }
            });
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
        public ImageButton mChatMapBtn;
        public ProgressBar mImageProgress;
        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        public void init(View view) {
            mSenderProfileImage = (CircleImageView) view.findViewById(R.id.chat_msg_sender_image);
            mMessageText = (RobotoTextView) view.findViewById(R.id.chat_msg_text);
            mMessageDate = (RobotoTextView) view.findViewById(R.id.chat_msg_date);
            mChatImage = (ImageView) view.findViewById(R.id.chat_image);
            mImageProgress = (ProgressBar) view.findViewById(R.id.chat_image_progress);
            mChatMapBtn = (ImageButton) view.findViewById(R.id.chat_msg_item_map_btn);
        }
        public void showMessageImage(){
            mMessageText.setVisibility(View.GONE);
            mChatMapBtn.setVisibility(View.GONE);
            mChatImage.setVisibility(View.VISIBLE);
            mImageProgress.setVisibility(View.VISIBLE);
        }
        public void showMessageView(){
            mMessageText.setVisibility(View.VISIBLE);
            mChatImage.setVisibility(View.GONE);
            mImageProgress.setVisibility(View.GONE);
            mChatMapBtn.setVisibility(View.GONE);
        }
        public void showMessageMapBtn(){
            mMessageText.setVisibility(View.GONE);
            mChatImage.setVisibility(View.GONE);
            mImageProgress.setVisibility(View.GONE);
            mChatMapBtn.setVisibility(View.VISIBLE);
        }
    }
}
