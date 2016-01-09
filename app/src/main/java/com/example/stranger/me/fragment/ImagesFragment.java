package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;


public class ImagesFragment extends Fragment {


    private static final String USER_ID = "user_id";
    private String mUserId;
    private OnFragmentInteractionListener mListener;

    public ImagesFragment() {
        // Required empty public constructor
    }

    public static ImagesFragment newInstance() {
        ImagesFragment fragment = new ImagesFragment();
        return fragment;
    }
    public static ImagesFragment newInstance(String userId) {
        ImagesFragment fragment = new ImagesFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID,userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(USER_ID);
        }
        else{
//            mUserId = FirebaseHelper.getAuthId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
