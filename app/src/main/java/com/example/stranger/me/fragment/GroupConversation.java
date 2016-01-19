package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.stranger.me.R;
import com.example.stranger.me.widget.RobotoEditText;

public class GroupConversation extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RobotoEditText mChatEditText;
    private RobotoEditText mChatMsgLengthView;
    private ImageButton mChatMsgSendBtn;
    private ImageButton mChatMsgSendPhotoBtn;
    private ImageButton mChatMsgFaceBtn;
    public GroupConversation() {
        // Required empty public constructor
    }

    public static GroupConversation newInstance() {
        GroupConversation fragment = new GroupConversation();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_group_conversation, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        mChatEditText= (RobotoEditText) view.findViewById(R.id.group_chat_msg_edittext);
        mChatMsgFaceBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_face_btn);
        mChatMsgLengthView = (RobotoEditText) view.findViewById(R.id.group_chat_msg_length);
        mChatMsgSendBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_send_btn);
        mChatMsgSendPhotoBtn = (ImageButton) view.findViewById(R.id.group_chat_msg_photo_btn);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_chat_recyclerview);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
