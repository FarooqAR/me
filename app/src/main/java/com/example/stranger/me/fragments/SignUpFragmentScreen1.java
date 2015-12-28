package com.example.stranger.me.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stranger.me.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen1 extends Fragment {

    private Button mSignInButton;
    private Button mSignUpButton;
    private ViewChangeListener mListenerActivity;
    private ViewChangeListener mListenerFragment;
    public SignUpFragmentScreen1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_up_screen1, container, false);
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);

        mListenerActivity = (ViewChangeListener) getActivity();
        mListenerFragment = (ViewChangeListener) getParentFragment();
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListenerActivity.onClick(1);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListenerFragment.onClick(1);
            }
        });
        return  view;
    }
    public interface ViewChangeListener{
        public void onClick(int i);
    }

}
