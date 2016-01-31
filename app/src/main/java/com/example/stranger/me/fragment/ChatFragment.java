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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.utils.ObjectUtils;
import com.example.stranger.me.CustomLinearLayoutManager;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.adapter.ChatAdapter;
import com.example.stranger.me.adapter.ChatFriendListAdapter;
import com.example.stranger.me.helper.ChatHelper;
import com.example.stranger.me.helper.CloudinaryHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.Message;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.service.ChatService;
import com.example.stranger.me.widget.RobotoEditText;
import com.example.stranger.me.widget.RobotoTextView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChatFragment extends Fragment implements OnConnectionFailedListener, ConnectionCallbacks {

    private static final String TAG = "ChatFragment";
    public static final String CURRENT_USER = "chat_current_user";
    private static final int PLACE_PICKER_REQUEST = 4361;
    private static final String PREVIOUS_KEY = "previous_key";
    private RecyclerView mRecyclerView;
    private ListView mFriendsListView;
    private RelativeLayout mMainContent;
    private CoordinatorLayout mRootView;

    private RobotoEditText mChatMsgEditText;
    private RobotoTextView mChatMsgLengthView;
    private TextView mNoMessage;
    private ImageButton mChatMsgSendBtn;
    private ImageButton mChatMsgImageBtn;
    private ImageButton mChatMsgFaceBtn;
    private ImageButton mChatMsgMapBtn;

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
    private boolean mSendClicked;
    private String mCurrentUser;
    private String mPreviousKey;//previous conversation for messages were showing
    private GoogleApiClient mGoogleApiClient;
    private View.OnClickListener mChatMapBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
                disableViews();
                displayPlacePicker();
            }

        }
    };
    private ChildEventListener mUsersDataChangeListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            //if the user that has changed his data is friend than update his online status in chatlistadapter
            if (FirebaseHelper.isFriend(dataSnapshot.getKey()))
                new ChangeUserInChatFriendListTask().execute(dataSnapshot);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //if the user that has changed his data is friend than update his online status in chatlistadapter
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
    private Button mFindContactBtn;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_USER, mCurrentUser);
        outState.putString(PREVIOUS_KEY, mPreviousKey);
    }

    private AdapterView.OnItemClickListener mFriendsListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!mCurrentUser.equals(mUsers.get(position).getId())) {//if the user didn't click the same item
                mMessages.clear();
                mChatAdapter.notifyDataSetChanged();
                mCurrentUser = mUsers.get(position).getId();
                mChatMsgEditText.setHint("Send Message to " + mUsers.get(position).getFirstName());
                mSendClicked = false;
                updateChatMessageListener();//first remove and then add the listener again
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

            String sender = String.valueOf(dataSnapshot.child("sender").getValue());
            final String pushKey = dataSnapshot.getKey();

            /*
            * Every time onChildAdded is called, we will think that the user is currently on ChatFragment and has
            * already saw this message, so set seen value of this message to true only if the message is sent his
            * friend not himself
            */
            if (!sender.equals(FirebaseHelper.getAuthId())) {
                FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(ChatHelper.getConversationKey(mCurrentUser)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //if the message is somehow deleted, this will prevent it from recreating
                        if (dataSnapshot.child(pushKey).exists()) {
                            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(ChatHelper.getConversationKey(mCurrentUser))
                                    .child(pushKey).child("seen").setValue(true);
                        }
                        FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(ChatHelper.getConversationKey(mCurrentUser)).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(ChatHelper.getConversationKey(mCurrentUser)).removeEventListener(this);
                    }
                });

            }
            new AddMessageToList().execute(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            new RemoveMessageFromList().execute(dataSnapshot);
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
                    mSendClicked = true;
                    disableViews();
                    Message message = new Message();
                    message.setMessage(msg);
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
                mSendClicked = true;
                disableViews();
                //this is library method and will be called whenever the user click on send image button
                //it will help to choose an image as well as to crop it, maximum size to crop is 720x1280, to reduce size
                Crop.pickImage(getActivity());
            }
        }
    };

    private void beginCrop(Uri source) {
        //get cache directory of this app, and add a file named cropped, it will be used to store the cropped image later
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        //start cropping
        Crop.of(source, destination).asSquare().withMaxSize(720, 1280).start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            //if an image has been picked, then call it
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //the image has been cropped and ready to upload
            SnackbarHelper.create(mRootView, "Uploading Image").setDuration(Snackbar.LENGTH_INDEFINITE).show();
            //upload to cloudinary
            new ImageUploadTask().execute();

        } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            /*
            * get data from place picker and extract latitude,longitude and location name
            * and create a message object
            */
            Place place = PlacePicker.getPlace(
                    data,
                    getActivity());
            if (place.getName() != null && place.getLatLng() != null) {
                Message msg = new Message();
                msg.setLocationLat(place.getLatLng().latitude);
                msg.setLocationLong(place.getLatLng().longitude);
                msg.setLocation(String.valueOf(place.getName()));
                ChatHelper.sendMessage(mCurrentUser, msg, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        enableViews();
                    }
                });
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //the user may have cancelled picking an image or a place, then enable views which were
            //previously got disabled by clicking on send (location or image) button
            enableViews();
        }
    }

    //listener for adding friends to chat list
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

    public ChatFragment() {
        // Required empty public constructor
    }


    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();

        return fragment;
    }

    //it will be used when the user click on a notification
    public static ChatFragment newInstance(String userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentUser = savedInstanceState.getString(CURRENT_USER);
            mPreviousKey = savedInstanceState.getString(PREVIOUS_KEY);
        }
        FirebaseHelper.getRoot().child(FirebaseHelper.USERS_KEY).child(FirebaseHelper.getAuthId()).child("online").setValue(true);
        mUsers = new ArrayList<>();
        mMessages = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .enableAutoManage(getActivity(), 0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mChatFriendListAdapter = new ChatFriendListAdapter(getActivity(), R.layout.chat_friends_list_item, mUsers);
        if (getArguments() != null) {
            mCurrentUser = getArguments().getString(CURRENT_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        init(view);
        if (mCurrentUser != null) {
            //messages have been viewed by the user, so set their seen value to true
            new ResetSeen().execute(ChatHelper.getConversationKey(mCurrentUser));
        }
        mMessages.clear();

        mChatAdapter = new ChatAdapter(getActivity(), mMessages);
        mFriendsListView.setAdapter(mChatFriendListAdapter);
        mChatMsgEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    new ResetSeen().execute(ChatHelper.getConversationKey(mCurrentUser));
                }
            }
        });

        //if the user has no friends this button will be shown and clicking on it will switch fragment to findContact
        mFindContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity activity = (HomeActivity) getActivity();
                activity.setFragment(activity.getFragmentsSize() - 2);
            }
        });

        //listen for changes in chat message edittext and update the ui
        mChatMsgEditText.addTextChangedListener(mChatEditTextListener);

        //listen for item click in friends listview
        mFriendsListView.setOnItemClickListener(mFriendsListItemClickListener);

        //listener on users node to update online status of friends
        FirebaseHelper.getRoot().child(FirebaseHelper.USERS_KEY).addChildEventListener(mUsersDataChangeListener);

        //listener to add friends to list
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIENDS_KEY).child(FirebaseHelper.getAuthId()).addChildEventListener(mFriendsListener);

        //if user has no friends then show mFindContactBtn
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIENDS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(FirebaseHelper.getAuthId()).exists()) {
                    disableViews();
                    mFindContactBtn.setVisibility(View.VISIBLE);
                } else {
                    enableViews();
                    mFindContactBtn.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mChatMsgSendBtn.setOnClickListener(mChatSendBtnListener);
        mChatMsgImageBtn.setOnClickListener(mChatImageBtnListener);
        mChatMsgMapBtn.setOnClickListener(mChatMapBtnListener);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mChatAdapter);
        FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideChatProgress();
                hideFriendsListProgress();
                if (mCurrentUser != null && !dataSnapshot.child(ChatHelper.getConversationKey(mCurrentUser)).exists()) {
                    showNoMessage();
                }
                FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return view;
    }

    public void removeListeners() {
        String conversationKey = ChatHelper.getConversationKey(mCurrentUser);
        if (mPreviousKey != null)
            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(mPreviousKey).removeEventListener(mChatMessageListener);
        if (conversationKey != null)
            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(conversationKey).removeEventListener(mChatMessageListener);

    }

    public void addListeners() {
        String conversationKey = ChatHelper.getConversationKey(mCurrentUser);
        FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(conversationKey)
                .orderByChild("timestamp").startAt().limitToLast(30).addChildEventListener(mChatMessageListener);


    }

    public void updateChatMessageListener() {
        if (mCurrentUser != null && ChatHelper.getConversationKey(mCurrentUser) != null) {
            removeListeners();
            addListeners();
            Log.d(TAG, "previousKey=" + mPreviousKey + "    currentKey=" + ChatHelper.getConversationKey(mCurrentUser));
            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hideChatProgress();
                    hideFriendsListProgress();
                    if (!dataSnapshot.child(ChatHelper.getConversationKey(mCurrentUser)).exists()) {
                        showNoMessage();
                    }
                    FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).removeEventListener(this);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Override
    public void onPause() {
        removeListeners();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (mCurrentUser != null && ChatHelper.getConversationKey(mCurrentUser) != null) {
            //add listener on last conversation key so that chatservice can send notifications for
            ChatService.getInstance().setListenerFor(ChatHelper.getConversationKey(mCurrentUser));
        }
        super.onPause();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    private void displayPlacePicker() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "GooglePlayServicesNotAvailableException thrown");
        }
    }

    public void disableViews() {
        mChatMsgEditText.setEnabled(false);
        mChatMsgSendBtn.setEnabled(false);
        mChatMsgFaceBtn.setEnabled(false);
        mChatMsgImageBtn.setEnabled(false);
        mChatMsgMapBtn.setEnabled(false);
    }

    public void enableViews() {
        mChatMsgEditText.setEnabled(true);
        mChatMsgSendBtn.setEnabled(true);
        mChatMsgFaceBtn.setEnabled(true);
        mChatMsgImageBtn.setEnabled(true);
        mChatMsgMapBtn.setEnabled(true);
    }

    private void init(View view) {
        mNoMessage = (TextView) view.findViewById(R.id.no_message);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.friends_chat_recyclerview);
        mFriendsListView = (ListView) view.findViewById(R.id.friends_chat_list);
        mMainContent = (RelativeLayout) view.findViewById(R.id.friends_chat_content);
        mRootView = (CoordinatorLayout) view.findViewById(R.id.root_view);
        mChatMsgEditText = (RobotoEditText) view.findViewById(R.id.chat_msg_edittext);
        mChatMsgLengthView = (RobotoTextView) view.findViewById(R.id.chat_msg_length);
        mChatMsgSendBtn = (ImageButton) view.findViewById(R.id.chat_msg_send_btn);
        mChatMsgFaceBtn = (ImageButton) view.findViewById(R.id.chat_msg_face_btn);
        mChatMsgImageBtn = (ImageButton) view.findViewById(R.id.chat_msg_photo_btn);
        mChatMsgMapBtn = (ImageButton) view.findViewById(R.id.chat_msg_map_btn);
        mChatProgress = (RelativeLayout) view.findViewById(R.id.chat_progress);
        mFriendsListProgress = (ProgressBar) view.findViewById(R.id.friends_list_progress);
        mFindContactBtn = (Button) view.findViewById(R.id.find_contact_btn);
    }

    private void hideChatProgress() {
        mChatProgress.setVisibility(View.GONE);
        hideNoMessage();
    }

    private void hideNoMessage() {
        mNoMessage.setVisibility(View.GONE);
    }

    private void showNoMessage() {
        mNoMessage.setVisibility(View.VISIBLE);
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
    public void onResume() {
        if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        if (mCurrentUser != null) {
            Integer i = null;
            try {

                i = new GetIndexTask().execute(mCurrentUser).get();
                if (i != null) {
                    mFriendsListView.setItemChecked(i, true);
                    mChatMsgEditText.setHint("Send Message to " + mUsers.get(i).getFirstName());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            updateChatMessageListener();
        } else {
            mFriendsListView.setItemChecked(0, true);

        }
        super.onResume();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

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

    private class RemoveFriendFromList extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            String idOfFriendToRemove = params[0];
            for (int i = 0; i < mUsers.size(); i++) {
                User user = mUsers.get(i);
                if (user.getId().equals(idOfFriendToRemove)) {
                    mUsers.remove(user);
                    return user;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null) {
                mChatFriendListAdapter.notifyDataSetChanged();
                //if the removed friend was the current user (whose messages were showing)
                //then update the listeners for the selected friend
                if (mCurrentUser.equals(user.getId())) {
                    int newPos = mFriendsListView.getSelectedItemPosition();
                    mMessages.clear();
                    mChatAdapter.notifyDataSetChanged();
                    mCurrentUser = mUsers.get(newPos).getId();
                    mChatMsgEditText.setHint("Send Message to " + mUsers.get(newPos).getFirstName());
                    updateChatMessageListener();

                }
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
                    updateChatMessageListener();
                    mChatMsgEditText.setHint("Send Message to " + mUsers.get(0).getFirstName());
                } else {
                    Integer i = null;
                    try {
                        i = new GetIndexTask().execute(mCurrentUser).get();
                        if (i != null) {
                            mFriendsListView.setItemChecked(i, true);
                            mChatMsgEditText.setHint("Send Message to " + mUsers.get(i).getFirstName());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                hideFriendsListProgress();
                mChatFriendListAdapter.notifyDataSetChanged();
            }
        }
    }


    private class AddMessageToList extends AsyncTask<DataSnapshot, Void, Integer> {
        @Override
        protected synchronized Integer doInBackground(DataSnapshot... params) {
            Message message = params[0].getValue(Message.class);
            message.setPush_key(params[0].getKey());

            //it's surely a bad design to check for every message by looping through entire array which can be as big as....100 messages .it will
            //take a while to show a single message, it is just to ensure that a message is not shown twice or more
            for (int i = 0; i < mMessages.size(); i++) {
                if (mMessages.get(i).getPush_key().equals(message.getPush_key()))
                    return null;
            }
            mMessages.add(message);
            return mMessages.indexOf(message);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                //if previous messages are being loaded then call notifyDataSetChanged otherwise use notifyItemInserted
                //or it will show weird behaviour due to continous animations
                if (mSendClicked)
                    mChatAdapter.notifyItemInserted(integer);
                else
                    mChatAdapter.notifyDataSetChanged();
                mSendClicked = false;
                mRecyclerView.scrollToPosition(integer);
                hideChatProgress();
                hideFriendsListProgress();

                //set mPreviousKey variable to current user's...mPreviousKey will be used next time the updateListener is called
                mPreviousKey = ChatHelper.getConversationKey(mCurrentUser);

            }
        }
    }

    private class RemoveMessageFromList extends AsyncTask<DataSnapshot, Void, Integer> {
        @Override
        protected synchronized Integer doInBackground(DataSnapshot... params) {
            Message message = params[0].getValue(Message.class);
            message.setPush_key(params[0].getKey());
            for (int i = 0; i < mMessages.size(); i++) {
                if (mMessages.get(i).getPush_key().equals(message.getPush_key())) {
                    mMessages.remove(i);
                    return i;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer != null) {
                mChatAdapter.notifyItemRemoved(integer);

            }
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

    private class ResetSeen extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            final String key = strings[0];
            for (int i = 0; i < mMessages.size(); i++) {
                final Message current = mMessages.get(i);
                if (!current.getSender().equals(FirebaseHelper.getAuthId())) {

                    FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(current.getPush_key()).exists()) {
                                Map<String, Object> update = new HashMap<String, Object>();
                                update.put("seen", true);
                                FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(key).child(current.getPush_key()).updateChildren(update);
                            }
                            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(key).removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                            FirebaseHelper.getRoot().child(FirebaseHelper.PRIVATE_CONVERSATION).child(key).removeEventListener(this);
                        }
                    });

                }
            }
            if (ChatService.getInstance() != null) {//service may have not yet started
                ChatService.getInstance().removeNotificationsFor(strings[0]);
            }
            return null;
        }

    }
}
