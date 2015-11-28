package com.example.stranger.me.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen1 extends Fragment {


    public SignUpFragmentScreen1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_screen1, container, false);
    }

}
