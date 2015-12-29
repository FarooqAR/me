package com.example.stranger.me.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen3 extends Fragment {


    public SignUpFragmentScreen3() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen3 newInstance(){
        SignUpFragmentScreen3 fragment = new SignUpFragmentScreen3();

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sign_up_fragment_screen3, container, false);
    }

}
