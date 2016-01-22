package com.example.stranger.me.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudinary.utils.ObjectUtils;
import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.adapter.ChatAdapter;
import com.example.stranger.me.adapter.GroupListAdapter;
import com.example.stranger.me.helper.CloudinaryHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.widget.RobotoEditText;
import com.example.stranger.me.widget.RobotoTextView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GroupConversationFragment extends Fragment implements GroupListAdapter.OnGroupChangeListener {
    private static final String TAG = "GroupChatFragment";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RobotoEditText mChatEditText;
    private RobotoTextView mChatMsgLengthView;
    private ImageButton mChatMsgSendBtn;
    private ImageButton mChatMsgSendPhotoBtn;
    private ImageButton mChatMsgFaceBtn;
    private LinearLayout mChatProgress;
    private TextView mSelectGroup;
    private TextView mAccessRestrict;
    private int mChatMsgLength;
    private int mChatMsgLengthLeft;
    private int mChatMsgMaxLength = 150;
    private ArrayList<Message> mMessages;
    private ChatAdapter mChatAdapter;
    private boolean mSendClicked;
    private ChildEventListener mChatMessageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new AddMessageToList().execute(dataSnapshot);
            Log.d(TAG, "onChildAdded");
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.d(TAG, "onCancelled [chat]");
        }
    };
    private TextWatcher mChatEditTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mChatMsgLength = s.length();
            if (mChatMsgLength <= 0) {
                mChatMsgSendBtn.setEnabled(false);
                mChatMsgSendBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_gray_24dp));
            } else {
                mChatMsgSendBtn.setEnabled(true);
                mChatMsgSendBtn.setImageDrawable(getResources().getDrawable(R.drawable.send_btn));
            }
            mChatMsgLengthLeft = mChatMsgMaxLength - mChatMsgLength;
            mChatMsgLengthLeft = (mChatMsgLengthLeft < 0) ? 0 : mChatMsgLengthLeft;
            mChatMsgLengthView.setText("" + mChatMsgLengthLeft);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private View.OnClickListener mChatSendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (GroupHelper.getGROUPS() != null && GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), GroupHelper.getCurrentGroup()) != -1) {//if data has been retrieved
                String msg = String.valueOf(mChatEditText.getText());
                if (!msg.equals("")) {
                    disableViews();
                    Message message = new Message(msg, FirebaseHelper.getAuthId());
                    mChatEditText.setText("");
                    mSendClicked = true;
                    GroupHelper.sendMessage(GroupHelper.getCurrentGroup(), message, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            if (firebaseError != null) {
                                Log.d(TAG, firebaseError.getMessage());
                            }
                            enableViews();
                        }
                    });
                }
            }
        }
    };
    private View.OnClickListener mChatMsgSendPhotoBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (GroupHelper.getGROUPS() != null && GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), GroupHelper.getCurrentGroup()) != -1) {
                disableViews();
                mSendClicked = true;
                Crop.pickImage(getActivity());
            }
        }
    };

    public void disableViews() {
        mChatEditText.setEnabled(false);
        mChatMsgSendBtn.setEnabled(false);
        mChatMsgFaceBtn.setEnabled(false);
        mChatMsgSendPhotoBtn.setEnabled(false);
    }

    public void enableViews() {
        mChatEditText.setEnabled(true);
        mChatMsgSendBtn.setEnabled(true);
        mChatMsgFaceBtn.setEnabled(true);
        mChatMsgSendPhotoBtn.setEnabled(true);
    }

    public GroupConversationFragment() {
        // Required empty public constructor
    }

    public static GroupConversationFragment newInstance() {
        GroupConversationFragment fragment = new GroupConversationFragment();

        return fragment;
    }

    private void updateListeners(String groupkey) {
        if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), groupkey) != -1) {
            mMessages.clear();
            mChatAdapter.notifyDataSetChanged();
            mAccessRestrict.setVisibility(View.GONE);

            if (GroupHelper.getPreviousGroup() != null)//it will be null for first time
                FirebaseHelper.getRoot().child("group_conversation").child(GroupHelper.getPreviousGroup()).removeEventListener(mChatMessageListener);

            FirebaseHelper.getRoot().child("group_conversation").child(groupkey).startAt().orderByChild("timestamp")
                    .limitToLast(40).addChildEventListener(mChatMessageListener);

        } else {
            disableViews();
            mChatProgress.setVisibility(View.GONE);
            mAccessRestrict.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_conversation, container, false);
        init(view);
        mChatAdapter = new ChatAdapter(getActivity(), mMessages);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.setRecyclerView(mRecyclerView);
        if (GroupHelper.getCurrentGroup() != null) {
            //update chat
            HomeActivity activity = (HomeActivity) getActivity();
            activity.getSupportActionBar().setTitle(GroupHelper.getGroupTitle(GroupHelper.getCurrentGroup()));
            mChatProgress.setVisibility(View.VISIBLE);
            mSelectGroup.setVisibility(View.GONE);
            updateListeners(GroupHelper.getCurrentGroup());
        } else {
            disableViews();
            mChatProgress.setVisibility(View.GONE);
            mSelectGroup.setVisibility(View.VISIBLE);
        }
        mChatEditText.addTextChangedListener(mChatEditTextListener);
        mChatMsgSendBtn.setOnClickListener(mChatSendBtnListener);
        mChatMsgSendPhotoBtn.setOnClickListener(mChatMsgSendPhotoBtnListener);
        return view;
    }

    private void init(View view) {
        mChatEditText = (RobotoEditText) view.findViewById(R.id.group_chat_msg_edittext);
        mChatMsgFaceBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_face_btn);
        mChatMsgLengthView = (RobotoTextView) view.findViewById(R.id.group_chat_msg_length);
        mChatMsgSendBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_send_btn);
        mChatMsgSendPhotoBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_photo_btn);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_chat_recyclerview);
        mSelectGroup = (TextView) view.findViewById(R.id.select_group);
        mAccessRestrict = (TextView) view.findViewById(R.id.access_restrict);
        mChatProgress = (LinearLayout) view.findViewById(R.id.group_chat_progress);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(720, 1280).start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //the image has been cropped and ready to upload
            SnackbarHelper.create(mRecyclerView, "Uploading Image").setDuration(Snackbar.LENGTH_INDEFINITE).show();
            new ImageUploadTask().execute();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            enableViews();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onGroupChange() {
        mChatProgress.setVisibility(View.VISIBLE);


        mSelectGroup.setVisibility(View.GONE);
        updateListeners(GroupHelper.getCurrentGroup());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class ImageUploadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = null;
            try {
                File file = new File(getActivity().getCacheDir(), "cropped");
                Map uploadResult = CloudinaryHelper.getInstance().uploader().upload(file, ObjectUtils.emptyMap());
                /*
                uploadResult contains following keys
                public_id,version,signature,height,width,format,resource_type,created_at,bytes,type,url,secure_url,etag
                */
                url = (String) uploadResult.get("secure_url");

                url = CloudinaryHelper.getInstance().url().generate(String.valueOf(uploadResult.get("public_id")));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Message message = new Message();
            message.setImageUrl(s);
            message.setSender(FirebaseHelper.getAuthId());
            GroupHelper.sendMessage(GroupHelper.getCurrentGroup(), message, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        enableViews();
                        SnackbarHelper.create(mRecyclerView, "Image Uploaded").setDuration(Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private class AddMessageToList extends AsyncTask<DataSnapshot, Void, Integer> {
        @Override
        protected synchronized Integer doInBackground(DataSnapshot... params) {
            Message message = params[0].getValue(Message.class);
            message.setPush_key(params[0].getKey());
            Log.d(TAG, "doInBackground");
            if(mMessages.size()==0){
                mMessages.add(message);
                return mMessages.indexOf(message);
            }
            if(mMessages.size()!=0 && !mMessages.get(mMessages.size()-1).getPush_key().equals(message.getPush_key())) {
                mMessages.add(message);
                return mMessages.indexOf(message);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            enableViews();
            mChatProgress.setVisibility(View.GONE);
            if(integer != null) {
                if (mSendClicked)
                    mChatAdapter.notifyItemInserted(integer);
                else
                    mChatAdapter.notifyDataSetChanged();
                mSendClicked = false;
                mRecyclerView.scrollToPosition(integer);
            }
        }
    }
}
