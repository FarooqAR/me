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
import com.example.stranger.me.adapter.ChatAdapter;
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
import com.firebase.client.ValueEventListener;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class GroupConversationFragment extends Fragment {
    private static final String TAG = "GroupChatFragment";
    private static final String CONVERSATION_KEY = "conversation_key";
    private static final String GROUP_KEY = "group_key";
    private String mConversationKey;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RobotoEditText mChatEditText;
    private RobotoTextView mChatMsgLengthView;
    private ImageButton mChatMsgSendBtn;
    private ImageButton mChatMsgSendPhotoBtn;
    private ImageButton mChatMsgFaceBtn;
    private LinearLayout mChatProgress;
    private TextView mAccessRestrict;
    private TextView mNoMesssages;
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
    private String mGroupKey;
    private View.OnClickListener mChatSendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(),mGroupKey ) != -1) {
                String msg = String.valueOf(mChatEditText.getText());
                if (!msg.equals("")) {
                    disableViews();
                    Message message = new Message(msg, FirebaseHelper.getAuthId());
                    mChatEditText.setText("");
                    mSendClicked = true;
                    GroupHelper.sendMessage(mConversationKey, message, new Firebase.CompletionListener() {
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
            if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), mGroupKey) != -1) {
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

    public static GroupConversationFragment newInstance(String mGroupConversation,String mGroupKey) {
        GroupConversationFragment fragment = new GroupConversationFragment();
        Bundle args = new Bundle();
        args.putString(CONVERSATION_KEY, mGroupConversation);
        args.putString(GROUP_KEY,mGroupKey);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessages = new ArrayList<>();
        if (getArguments() != null) {
            mConversationKey = getArguments().getString(CONVERSATION_KEY);
            mGroupKey = getArguments().getString(GROUP_KEY);
        }
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
        mChatEditText.addTextChangedListener(mChatEditTextListener);
        mChatMsgSendBtn.setOnClickListener(mChatSendBtnListener);
        mChatMsgSendPhotoBtn.setOnClickListener(mChatMsgSendPhotoBtnListener);
        if (GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(), mGroupKey) != -1) {
            mAccessRestrict.setVisibility(View.GONE);
            FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).child(mConversationKey).orderByChild("timestamp").startAt()
                    .limitToLast(40).addChildEventListener(mChatMessageListener);
        } else {
            disableViews();
            mChatProgress.setVisibility(View.GONE);
            mAccessRestrict.setVisibility(View.VISIBLE);
            mNoMesssages.setVisibility(View.GONE);
        }
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(mConversationKey).exists()){
                    mNoMesssages.setVisibility(View.VISIBLE);
                    mChatProgress.setVisibility(View.GONE);
                }
                FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).removeEventListener(this);
            }
        });
        return view;
    }

    private void init(View view) {
        mChatEditText = (RobotoEditText) view.findViewById(R.id.group_chat_msg_edittext);
        mChatMsgFaceBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_face_btn);
        mChatMsgLengthView = (RobotoTextView) view.findViewById(R.id.group_chat_msg_length);
        mChatMsgSendBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_send_btn);
        mChatMsgSendPhotoBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_photo_btn);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_chat_recyclerview);
        mAccessRestrict = (TextView) view.findViewById(R.id.access_restrict);
        mChatProgress = (LinearLayout) view.findViewById(R.id.group_chat_progress);
        mNoMesssages = (TextView) view.findViewById(R.id.no_message);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(720, 1280).start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"ConversationFragment onActivityResult");
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //the image has been cropped and ready to upload
            SnackbarHelper.create(mRecyclerView, "Uploading Image").setDuration(Snackbar.LENGTH_INDEFINITE).show();
            new ImageUploadTask().execute();
        } else{
            enableViews();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onDestroyView() {
        FirebaseHelper.getRoot().child(FirebaseHelper.GROUP_CONVERSATION).child(mConversationKey).orderByChild("timestamp").startAt()
                .limitToLast(40).removeEventListener(mChatMessageListener);
        super.onDestroyView();

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
            GroupHelper.sendMessage(mConversationKey, message, new Firebase.CompletionListener() {
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
            mMessages.add(message);
            return mMessages.indexOf(message);

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            enableViews();
            mChatProgress.setVisibility(View.GONE);
            mNoMesssages.setVisibility(View.GONE);
            if (integer != null) {
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
