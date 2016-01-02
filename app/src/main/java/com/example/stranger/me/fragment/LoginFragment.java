package com.example.stranger.me.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stranger.me.HomeActivity;
import com.example.stranger.me.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private Button mSignUpButton;
    private Button mSignInButton;
    private SignUpFragmentScreen1.ViewChangeListener mListener;
    private View.OnClickListener mSignUpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onClick(1);
        }
    };
    private View.OnClickListener mSignInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            startActivity(intent);
        }
    };
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof SignUpFragmentScreen1.ViewChangeListener){
            mListener = (SignUpFragmentScreen1.ViewChangeListener) activity;
        }
        else{
            throw new ClassCastException(activity.toString()+
            " must implement SignUpFragmentScreen1.ViewChangeListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
        mSignUpButton.setOnClickListener(mSignUpButtonListener);
        mSignInButton.setOnClickListener(mSignInButtonListener);
        return view;
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    public void init(View view){
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);
    }
}
