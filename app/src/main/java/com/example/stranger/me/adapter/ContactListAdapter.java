package com.example.stranger.me.adapter;

import android.content.Context;
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
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoTextView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Farooq on 1/14/2016.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private ArrayList<User> mUsers;
    public static Context mContext;

    public ContactListAdapter(Context context, ArrayList<User> users) {
        mContext = context;
        mUsers = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.displayName.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.with(mContext).load(user.getProfileImageURL()).placeholder(R.drawable.ic_account_circle).into(holder.profileImage);
        if (user.getCountry() != null)
            holder.country.setText(user.getCountry());
        if (FirebaseHelper.isFriend(user.getId())) {
            holder.enableUnFriend();
            holder.unFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.showUnfriendPopup(holder,v, user.getId());
                }
            });
        } else if (FirebaseHelper.isRequested(user.getId())) {
            holder.enableConfirmAsFriend();
            holder.confirmAsFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmAsFriend(holder, user.getId());
                }
            });
        } else if (FirebaseHelper.isRequestSent(user.getId())) {
            holder.enableRequestSent();
        } else {
            holder.enableAddAsFriend();
            holder.addAsFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {sendFriendRequest(holder,user.getId());}
            });
        }

    }

    //accept user request
    public void confirmAsFriend(final ViewHolder holder, final String id) {
        holder.showConfirmRequestProgress();
        FirebaseHelper.confirmAsFriend(id, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                holder.hideConfirmRequestProgress();
                holder.enableUnFriend();
                holder.unFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.showUnfriendPopup(holder,v,id);
                    }
                });
            }
        });
    }

    //add as friend. this will send request to user
    public static void sendFriendRequest(final ViewHolder holder, String id) {
        holder.showAddFriendProgress();

        FirebaseHelper.sendFriendRequest(id, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                holder.hideAddFriendProgress();
                if (firebaseError == null) {
                    holder.enableRequestSent();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ViewHolder";
        public RobotoTextView displayName;
        public RobotoTextView country;
        public CircleImageView profileImage;
        public Button addAsFriend;
        public Button confirmAsFriend;
        public Button unFriend;
        public TextView requestSent;
        public ProgressBar addAsFriendProgress;
        public ProgressBar confirmAsFriendProgress;
        public ProgressBar unFriendProgress;


        public ViewHolder(View itemView) {
            super(itemView);
            displayName = (RobotoTextView) itemView.findViewById(R.id.item_contact_display_name);
            country = (RobotoTextView) itemView.findViewById(R.id.item_contact_country);
            profileImage = (CircleImageView) itemView.findViewById(R.id.item_contact_profile_image);
            addAsFriend = (Button) itemView.findViewById(R.id.action_send_friend_request);
            confirmAsFriend = (Button) itemView.findViewById(R.id.action_confirm_friend);
            unFriend = (Button) itemView.findViewById(R.id.action_unfriend);
            requestSent = (TextView) itemView.findViewById(R.id.friend_request_sent);
            addAsFriendProgress = (ProgressBar) itemView.findViewById(R.id.action_send_friend_request_progress);
            confirmAsFriendProgress = (ProgressBar) itemView.findViewById(R.id.action_confirm_friend_progress);
            unFriendProgress = (ProgressBar) itemView.findViewById(R.id.action_unfriend_progress);
        }

        public void showUnFriendProgress() {
            unFriend.setEnabled(false);
            unFriendProgress.setVisibility(View.VISIBLE);
        }

        public void hideUnFriendProgress() {
            unFriend.setEnabled(true);
            unFriendProgress.setVisibility(View.GONE);
        }

        public void enableUnFriend() {
            unFriend.setVisibility(View.VISIBLE);
            addAsFriend.setVisibility(View.GONE);
            confirmAsFriend.setVisibility(View.GONE);
            requestSent.setVisibility(View.GONE);
        }

        public void showAddFriendProgress() {
            addAsFriend.setEnabled(false);
            addAsFriendProgress.setVisibility(View.VISIBLE);
        }

        public void hideAddFriendProgress() {
            addAsFriend.setEnabled(true);
            addAsFriendProgress.setVisibility(View.GONE);
        }

        public void enableAddAsFriend() {
            unFriend.setVisibility(View.GONE);
            addAsFriend.setVisibility(View.VISIBLE);
            confirmAsFriend.setVisibility(View.GONE);
            requestSent.setVisibility(View.GONE);
        }

        public void enableRequestSent() {
            unFriend.setVisibility(View.GONE);
            addAsFriend.setVisibility(View.GONE);
            confirmAsFriend.setVisibility(View.GONE);
            requestSent.setVisibility(View.VISIBLE);
        }

        public void showConfirmRequestProgress() {
            confirmAsFriend.setEnabled(false);
            confirmAsFriendProgress.setVisibility(View.VISIBLE);
        }

        public void hideConfirmRequestProgress() {
            confirmAsFriend.setEnabled(true);
            confirmAsFriendProgress.setVisibility(View.GONE);
        }

        public void enableConfirmAsFriend() {
            unFriend.setVisibility(View.GONE);
            addAsFriend.setVisibility(View.GONE);
            confirmAsFriend.setVisibility(View.VISIBLE);
            requestSent.setVisibility(View.GONE);
        }

        public void showUnfriendPopup(final ViewHolder holder,View v, final String id) {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.inflate(R.menu.action_unfriend);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_unfriend) {
                        holder.showUnFriendProgress();
                        //first remove the user from friends list
                        FirebaseHelper.unFriend(id, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                holder.hideUnFriendProgress();
                                if (firebaseError == null) {
                                    holder.enableAddAsFriend();
                                    holder.addAsFriend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //add listener again
                                            sendFriendRequest(holder, id);
                                        }
                                    });
                                }
                            }
                        });

                        return true;
                    }

                    return false;
                }
            });
            popupMenu.show();
        }
    }
}
