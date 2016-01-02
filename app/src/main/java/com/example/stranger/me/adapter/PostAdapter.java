package com.example.stranger.me.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stranger.me.R;
import com.example.stranger.me.modal.Post;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;

import java.util.ArrayList;

/**
 * Created by Farooq on 12/30/2015.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private ArrayList<Post> mPosts;
    public PostAdapter(ArrayList<Post> posts){
        mPosts = posts;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.postlistitem, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView mPostOwnerImage;
        public RobotoTextView mPostOwnerName;
        public RobotoTextView mPostDetails;
        public RobotoTextView mPostTime;
        public ImageButton mPostSettingsBtn;
        public RobotoTextView mPostTitle;
        public ImageView mPostImage;
        public TextView mPostLikes;
        public TextView mPostComments;
        public TextView mPostShares;
        public ImageButton mPostLikeBtn;
        public ImageButton mPostCommentBtn;
        public ImageButton mPostShareBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        public void init(View v){
            mPostOwnerImage = (CircleImageView) v.findViewById(R.id.post_owner_image);
            mPostOwnerName = (RobotoTextView) v.findViewById(R.id.post_owner_name);
            mPostDetails = (RobotoTextView) v.findViewById(R.id.post_details);
            mPostTime = (RobotoTextView) v.findViewById(R.id.post_time);
            mPostTitle = (RobotoTextView) v.findViewById(R.id.post_title);
            mPostImage = (ImageView) v.findViewById(R.id.post_image);
            mPostLikes = (TextView) v.findViewById(R.id.post_likes);
            mPostComments = (TextView) v.findViewById(R.id.post_comments);
            mPostShares= (TextView) v.findViewById(R.id.post_shares);
            mPostSettingsBtn = (ImageButton) v.findViewById(R.id.action_post_settings);
            mPostLikeBtn = (ImageButton) v.findViewById(R.id.post_like_btn);
            mPostCommentBtn = (ImageButton) v.findViewById(R.id.post_comment_btn);
            mPostShareBtn = (ImageButton) v.findViewById(R.id.post_share_btn);

        }
    }
}
