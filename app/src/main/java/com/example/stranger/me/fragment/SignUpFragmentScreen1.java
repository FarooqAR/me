package com.example.stranger.me.fragment;


import android.app.Activity;
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
    private View.OnClickListener mSignInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListenerActivity.onClick(0);
        }
    };
    private View.OnClickListener mSignUpButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mListenerFragment.onClick(1);
        }
    };
    public SignUpFragmentScreen1() {
        // Required empty public constructor
    }
    public static SignUpFragmentScreen1 newInstance(){
        SignUpFragmentScreen1 fragment = new SignUpFragmentScreen1();

        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ViewChangeListener) {
            mListenerActivity = (ViewChangeListener) activity;
        } else {
            throw new ClassCastException(activity.toString() +
                    " must implement ViewChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up_screen1, container, false);
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);

        mListenerFragment = (ViewChangeListener) getParentFragment();
        mSignInButton.setOnClickListener(mSignInButtonListener);
        mSignUpButton.setOnClickListener(mSignUpButtonListener);
        return view;
    }

    public interface ViewChangeListener {
        void onClick(int i);
    }

}
