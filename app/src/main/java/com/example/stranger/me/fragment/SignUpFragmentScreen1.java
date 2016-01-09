package com.example.stranger.me.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.InputHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen1 extends Fragment {

    private static final String TAG = "SignUpFragmentScreen1";
    private Button mSignInButton;
    private Button mSignUpButton;
    private RobotoEditText mSignUpEmail;
    private RobotoEditText mSignUpPassword;
    private ProgressBar mSignUpProgress;
    private SignUpScreen1Listener mListenerActivity;
    private SignUpScreen1Listener mListenerFragment;
    private View.OnClickListener mSignInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListenerActivity.onClick(0);
        }
    };
    private View.OnClickListener mSignUpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            final String e = String.valueOf(mSignUpEmail.getText());
            final String p = String.valueOf(mSignUpPassword.getText());
            if (isVaildEmailPassword(v, e, p)) {
                mSignUpProgress.setVisibility(View.VISIBLE);
                mSignUpButton.setEnabled(false);
                mSignUpButton.setText("");
                FirebaseHelper.getRoot().createUser(e, p, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> stringObjectMap) {

                        FirebaseHelper.getRoot().authWithPassword(e, p, new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                HandleResultTask task = new HandleResultTask();
                                task.execute();
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                SnackbarHelper.create(mSignInButton,firebaseError.getMessage());//give any view
                            }
                        });
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        SnackbarHelper.create(v, firebaseError.getMessage());
                        mSignUpProgress.setVisibility(View.GONE);
                        mSignUpButton.setEnabled(true);
                        mSignUpButton.setText("Sign Up");
                    }
                });

            }
        }
    };

    public SignUpFragmentScreen1() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen1 newInstance() {
        SignUpFragmentScreen1 fragment = new SignUpFragmentScreen1();
        return fragment;
    }

    private boolean isVaildEmailPassword(View v, String e, String p) {
        if (e.equals("") && p.equals("")) {
            SnackbarHelper.create(v, "Email & Password Required");
            return false;
        } else if (e.equals("")) {
            SnackbarHelper.create(v, "Email Required");
            return false;
        } else if (p.equals("")) {
            SnackbarHelper.create(v, "Password Required");
            return false;
        } else if (InputHelper.isEmailValid(v, e) && InputHelper.isPasswordValid(v, p)) {
            return true;

        }
        return false;
    }

    ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SignUpScreen1Listener) {
            mListenerActivity = (SignUpScreen1Listener) activity;
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
        init(view);
        mListenerFragment = (SignUpScreen1Listener) getParentFragment();
        mSignInButton.setOnClickListener(mSignInButtonListener);
        mSignUpButton.setOnClickListener(mSignUpButtonListener);
        return view;
    }

    public void init(View view) {
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);
        mSignUpEmail = (RobotoEditText) view.findViewById(R.id.sign_up_email);
        mSignUpPassword = (RobotoEditText) view.findViewById(R.id.sign_up_pass);
        mSignUpProgress = (ProgressBar) view.findViewById(R.id.sign_up_progress);
    }

    public interface SignUpScreen1Listener {
        void onClick(int i);

    }

    public class HandleResultTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mListenerFragment.onClick(1);
            mSignUpProgress.setVisibility(View.GONE);
            mSignUpButton.setEnabled(true);
            mSignUpButton.setText("Sign Up");
        }
    }
}
