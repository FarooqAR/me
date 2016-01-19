package com.example.stranger.me.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.utils.ObjectUtils;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity.PrivateChatListener;
import com.example.stranger.me.adapter.ChatAdapter;
import com.example.stranger.me.adapter.ChatFriendListAdapter;
import com.example.stranger.me.helper.ChatHelper;
import com.example.stranger.me.helper.CloudinaryHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SharedPreferenceHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.modal.User;
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
import java.util.concurrent.ExecutionException;

public class ChatFragment extends Fragment implements PrivateChatListener {

    private static final String TAG = "ChatFragment";
    private static final String CURRENT_USER = "chat_current_user";
    private static final String DATA_RETRIEVED = "data_retrieved";
    private RecyclerView mRecyclerView;
    private ListView mFriendsListView;
    private RelativeLayout mMainContent;
    private CoordinatorLayout mRootView;

    private RobotoEditText mChatMsgEditText;
    private RobotoTextView mChatMsgLengthView;
    private ImageButton mChatMsgSendBtn;
    private ImageButton mChatMsgImageBtn;
    private ImageButton mChatMsgFaceBtn;

    private int mChatMsgLength;
    private int mChatMsgLengthLeft;
    private int mChatMsgMaxLength = 150;
    private ArrayList<User> mUsers;
    private ArrayList<Message> mMessages;
    private RelativeLayout mChatProgress;
    private ProgressBar mFriendsListProgress;
    private ChatAdapter mChatAdapter;
    private ChatFriendListAdapter mChatFriendListAdapter;
    private OnFragmentInteractionListener mListener;
    private SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance();
    private String mCurrentUser;
    private ChildEventListener mUsersDataChangeListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (FirebaseHelper.isFriend(dataSnapshot.getKey()))
                new ChangeUserInChatFriendListTask().execute(dataSnapshot);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            if (FirebaseHelper.isFriend(dataSnapshot.getKey()))
                new RemoveUserFromChatFriendListTask().execute(dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_RETRIEVED, dataRetrieved);
    }

    private AdapterView.OnItemClickListener mFriendsListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!mCurrentUser.equals(mUsers.get(position).getId()) && dataRetrieved) {//if the user didn't click the same item
                mMessages.clear();
                mChatAdapter.notifyDataSetChanged();
                mCurrentUser = mUsers.get(position).getId();
                mChatMsgEditText.setHint("Send Message to " + mUsers.get(position).getFirstName());
                updateChatMessageListener();
            }
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

        }
    };
    private View.OnClickListener mChatSendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ChatHelper.getPrivateChatNode() != null) {//if data has been retrieved
                String msg = String.valueOf(mChatMsgEditText.getText());
                if (!msg.equals("")) {
                    disableViews();
                    Message message = new Message(msg, FirebaseHelper.getAuthId());
                    mChatMsgEditText.setText("");
                    ChatHelper.sendMessage(mCurrentUser, message, new Firebase.CompletionListener() {
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
    private View.OnClickListener mChatImageBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ChatHelper.getPrivateChatNode() != null) {//if data has been retrieved
                disableViews();
                Crop.pickImage(getActivity());
            }
        }
    };
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
            SnackbarHelper.create(mRootView,"Uploading Image").setDuration(Snackbar.LENGTH_INDEFINITE).show();
            new ImageUploadTask().execute();
        }
    }

    private ChildEventListener mFriendsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            new AddFriendToChatListTask().execute(dataSnapshot.getKey());//key is the id of friend

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new RemoveFriendFromList().execute(dataSnapshot.getKey());//key is the id of friend
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private boolean dataRetrieved;
    private boolean mViewCreated;

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            dataRetrieved = savedInstanceState.getBoolean(DATA_RETRIEVED,false);
        }
        mUsers = new ArrayList<>();
        mMessages = new ArrayList<>();
        mChatFriendListAdapter = new ChatFriendListAdapter(getActivity(), R.layout.chat_friends_list_item, mUsers);

    }
    public void showFaces(){
        Snackbar snackbar = SnackbarHelper.create(mRootView,"Choose a face");
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.GONE);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);
        mViewCreated = true;
        if(dataRetrieved){
            hideChatProgress();
            hideFriendsListProgress();
        }
        mMessages.clear();
        mChatAdapter = new ChatAdapter(getActivity(), mMessages);
        mFriendsListView.setAdapter(mChatFriendListAdapter);

        if (mCurrentUser != null) {
            Integer i=null;
            try {

                i = new GetIndexTask().execute(mCurrentUser).get();
                if (i != null)
                    mFriendsListView.setItemChecked(i, true);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            mChatMsgEditText.setHint("Send Message to " + mUsers.get(i).getFirstName());
            updateChatMessageListener();
        } else {
            mFriendsListView.setItemChecked(0, true);

        }
        mChatMsgEditText.addTextChangedListener(mChatEditTextListener);
        mFriendsListView.setOnItemClickListener(mFriendsListItemClickListener);
        FirebaseHelper.getRoot().child("users").addChildEventListener(mUsersDataChangeListener);
        FirebaseHelper.getRoot().child("friends").child(FirebaseHelper.getAuthId()).addChildEventListener(mFriendsListener);


        mChatMsgSendBtn.setOnClickListener(mChatSendBtnListener);
        mChatMsgImageBtn.setOnClickListener(mChatImageBtnListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mChatAdapter);
        return view;
    }

    public void updateChatMessageListener() {
        if (mCurrentUser != null && ChatHelper.getConversationKey(mCurrentUser) != null) {
            FirebaseHelper.getRoot().child("private_conversation").child(ChatHelper.getConversationKey(mCurrentUser))
                    .orderByChild("timestamp").limitToLast(30).removeEventListener(mChatMessageListener);
            FirebaseHelper.getRoot().child("private_conversation").child(ChatHelper.getConversationKey(mCurrentUser))
                    .orderByChild("timestamp").limitToLast(30).addChildEventListener(mChatMessageListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void disableViews() {
        mChatMsgEditText.setEnabled(false);
        mChatMsgSendBtn.setEnabled(false);
        mChatMsgFaceBtn.setEnabled(false);
        mChatMsgImageBtn.setEnabled(false);
    }

    public void enableViews() {
        mChatMsgEditText.setEnabled(true);
        mChatMsgSendBtn.setEnabled(true);
        mChatMsgFaceBtn.setEnabled(true);
        mChatMsgImageBtn.setEnabled(true);
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.friends_chat_recyclerview);
        mFriendsListView = (ListView) view.findViewById(R.id.friends_chat_list);
        mMainContent = (RelativeLayout) view.findViewById(R.id.friends_chat_content);
        mRootView = (CoordinatorLayout) view.findViewById(R.id.root_view);
        mChatMsgEditText = (RobotoEditText) view.findViewById(R.id.chat_msg_edittext);
        mChatMsgLengthView = (RobotoTextView) view.findViewById(R.id.chat_msg_length);
        mChatMsgSendBtn = (ImageButton) view.findViewById(R.id.chat_msg_send_btn);
        mChatMsgFaceBtn = (ImageButton) view.findViewById(R.id.chat_msg_face_btn);
        mChatMsgImageBtn = (ImageButton) view.findViewById(R.id.chat_msg_photo_btn);
        mChatProgress = (RelativeLayout) view.findViewById(R.id.chat_progress);
        mFriendsListProgress = (ProgressBar) view.findViewById(R.id.friends_list_progress);
    }

    private void hideChatProgress() {
        mChatProgress.setVisibility(View.GONE);
    }

    private void hideFriendsListProgress() {
        mFriendsListProgress.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPrivateChatDataRetrieved() {
        dataRetrieved = true;
        if (mViewCreated) hideChatProgress();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class RemoveUserFromChatFriendListTask extends AsyncTask<DataSnapshot, Void, Integer> {

        @Override
        protected Integer doInBackground(DataSnapshot... params) {
            for (int i = 0; i < mUsers.size(); i++) {
                User user = mUsers.get(i);
                if (user.getId().equals(params[0].getKey())) {
                    mUsers.remove(i);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                mChatFriendListAdapter.notifyDataSetChanged();
                Log.d(TAG, "adapter notified [item removed]");
            }
        }
    }

    public class ChangeUserInChatFriendListTask extends AsyncTask<DataSnapshot, Void, Integer> {

        @Override
        protected Integer doInBackground(DataSnapshot... params) {
            String id = params[0].getKey();
            User current_data = params[0].getValue(User.class);//user has changed his data (generally online status)
            current_data.setId(id);
            for (int i = 0; i < mUsers.size(); i++) {
                User previous_data = mUsers.get(i);//user data previously showing on screen
                if (previous_data.getId().equals(id)) {
                    mUsers.set(i, current_data);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                mChatFriendListAdapter.notifyDataSetChanged();
                Log.d(TAG, "adapter notified [item changed]");
            }
        }
    }

    private class RemoveFriendFromList extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String idOfFriendToRemove = params[0];
            for (int i = 0; i < mUsers.size(); i++) {
                User user = mUsers.get(i);
                if (user.getId().equals(idOfFriendToRemove)) {
                    mUsers.remove(i);
                    return i;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                mChatFriendListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class AddFriendToChatListTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String idOfFriendToAdd = params[0];
            for (int i = 0; i < mUsers.size(); i++) {
                //check whether friend is already added
                if (mUsers.get(i).getId().equals(idOfFriendToAdd)) {//if friend is already added
                    return null;
                }
            }
            //friend will be added only if the above for loop completed without returning null
            User user = FirebaseHelper.getUsers().child(idOfFriendToAdd).getValue(User.class);
            user.setId(idOfFriendToAdd);

            mUsers.add(user);
            return mUsers.indexOf(user);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                if (mCurrentUser == null) {
                    mCurrentUser = mUsers.get(0).getId();
                    FirebaseHelper.getRoot().child("private_conversation").child(ChatHelper.getConversationKey(mCurrentUser))
                            .orderByChild("timestamp").limitToLast(30).addChildEventListener(mChatMessageListener);
                    mChatMsgEditText.setHint("Send Message to " + mUsers.get(0).getFirstName());
                }
                hideFriendsListProgress();
                mChatFriendListAdapter.notifyDataSetChanged();
            }
        }
    }


    private class AddMessageToList extends AsyncTask<DataSnapshot, Void, Integer> {
        @Override
        protected Integer doInBackground(DataSnapshot... params) {
            Message message = params[0].getValue(Message.class);
            message.setPush_key(params[0].getKey());
            mMessages.add(message);
            return mMessages.indexOf(message);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            //use notifyItemInserted for animations
            //have to use nofifyDataSetChanged due to this bug https://code.google.com/p/android/issues/detail?id=77846
            mChatAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(integer);
        }
    }

    private class GetIndexTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            for (int i = 0; i < mUsers.size(); i++) {
                if (mUsers.get(i).getId().equals(params[0])) {
                    return i;
                }
            }
            return null;
        }
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
            ChatHelper.sendMessage(mCurrentUser, message, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        enableViews();
                        SnackbarHelper.create(mRootView, "Image Uploaded").setDuration(Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
