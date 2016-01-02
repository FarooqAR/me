package com.example.stranger.me.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;

import java.util.ArrayList;

/**
 * Created by Farooq on 1/2/2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<Message> mMessages;
    public ChatAdapter(ArrayList<Message> messages) {
        mMessages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if(viewType==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent,false);
        }
        else if(viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //set views
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView mSenderProfileImage;
        public RobotoTextView mMessageText;
        public RobotoTextView mMessageDate;

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }
        public void init(View view){
            mSenderProfileImage = (CircleImageView) view.findViewById(R.id.chat_msg_sender_image);
            mMessageText = (RobotoTextView) view.findViewById(R.id.chat_msg_text);
            mMessageDate = (RobotoTextView) view.findViewById(R.id.chat_msg_date);
        }
    }
}
