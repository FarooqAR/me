package com.example.stranger.me.fragment;


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
public class SignUpFragmentScreen4 extends Fragment {


    public SignUpFragmentScreen4() {
        // Required empty public constructor
    }
    public static SignUpFragmentScreen4 newInstance(){
        SignUpFragmentScreen4 fragment = new SignUpFragmentScreen4();

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
        return inflater.inflate(R.layout.sign_up_fragment_screen4, container, false);
    }

}