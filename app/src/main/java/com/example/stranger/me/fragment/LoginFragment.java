package com.example.stranger.me.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.stranger.me.R;
import com.example.stranger.me.activity.FacebookActivity;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.InputHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LoginFragment";
    public static final int REQUEST_GOOGLE_LOGIN = 61;
    public static final int REQUEST_FB_LOGIN = 55;

    private Button mSignUpButton;
    private Button mSignInButton;
    private EditText mSignInEmail;
    private EditText mSignInPassword;
    private ImageButton mSignInFbButton;
    private ImageButton mSignInGoogleButton;
    private ProgressBar mSignInProgress;
    private RelativeLayout mRootView;

    private boolean mGoogleLoginClicked;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mGoogleConnectionResult;
    private boolean mGoogleIntentInProgress;


    public void authWithFacebook(String token) {
        if (token != null) {
            disableViews();
            FirebaseHelper.getRoot().authWithOAuthToken("facebook", token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    if (FirebaseHelper.isUser(FirebaseHelper.getAuthId())) {
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        startActivity(i);
                        getActivity().finish();

                    } else {
                        SnackbarHelper.create(mRootView, "You're not registered. Please Sign up").show();
                    }
                    enableViews();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {

                    SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                    enableViews();
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_LOGIN) {
            /* This was a request by the Google API */
            if (resultCode != Activity.RESULT_OK) {
                mGoogleLoginClicked = false;
            }
            mGoogleIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else if (requestCode == REQUEST_FB_LOGIN) {
            String token = data.getStringExtra(FacebookActivity.TOKEN);
            if (token != null) {
                authWithFacebook(token);
            }
        }
    }

    private void enableViews() {
        mSignInProgress.setVisibility(View.GONE);
        mSignInEmail.setEnabled(true);
        mSignInPassword.setEnabled(true);
        mSignInFbButton.setEnabled(true);
        mSignInGoogleButton.setEnabled(true);
        mSignInButton.setEnabled(true);
        mSignUpButton.setEnabled(true);
        mSignInButton.setText("Sign In");
    }

    private void disableViews() {
        mSignInProgress.setVisibility(View.VISIBLE);
        mSignInEmail.setEnabled(false);
        mSignInPassword.setEnabled(false);
        mSignInFbButton.setEnabled(false);
        mSignInGoogleButton.setEnabled(false);
        mSignInButton.setEnabled(false);
        mSignUpButton.setEnabled(false);
        mSignInButton.setText("");
    }

    private SignUpFragmentScreen1.SignUpScreen1Listener mListener;
    private View.OnClickListener mSignUpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onClick(1);
        }
    };
    private View.OnClickListener mSignInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String e = String.valueOf(mSignInEmail.getText());
            String p = String.valueOf(mSignInPassword.getText());

            if (InputHelper.isVaildEmailPassword(mRootView, e, p)) {
                FirebaseHelper.getRoot().unauth();
                FirebaseHelper.setAuthId(null);
                if (FirebaseHelper.getAuthId() == null) {
                    disableViews();
                    FirebaseHelper.getRoot().authWithPassword(e, p, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            startActivity(i);
                            getActivity().finish();
                            enableViews();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                            enableViews();

                        }
                    });
                } else {
                    Log.d(TAG, "auth id:" + FirebaseHelper.getAuthId());
                }
            }
        }
    };
    private View.OnClickListener mSignInFbButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseHelper.getRoot().unauth();
            FirebaseHelper.setAuthId(null);
            Intent intent = new Intent(getActivity(), FacebookActivity.class);
            startActivityForResult(intent, REQUEST_FB_LOGIN);
        }
    };
    private View.OnClickListener mSignInGoogleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseHelper.getRoot().unauth();
            FirebaseHelper.setAuthId(null);
            mGoogleLoginClicked = true;
            if (!mGoogleApiClient.isConnecting()) {

                if (mGoogleConnectionResult != null) {
                    resolveSignInError();
                } else if (mGoogleApiClient.isConnected()) {
                    getGoogleOAuthTokenAndLogin();
                } else {
                    /* connect API now */
                    Log.d(TAG, "Trying to connect to Google API");
                    mGoogleApiClient.connect();
                }
            }
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SignUpFragmentScreen1.SignUpScreen1Listener) {
            mListener = (SignUpFragmentScreen1.SignUpScreen1Listener) activity;
        } else {
            throw new ClassCastException(activity.toString() +
                    " must implement SignUpFragmentScreen1.ViewChangeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /* Setup the Google API object to allow Google+ logins */
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        init(view);
        mSignUpButton.setOnClickListener(mSignUpButtonListener);
        mSignInButton.setOnClickListener(mSignInButtonListener);
        mSignInGoogleButton.setOnClickListener(mSignInGoogleButtonListener);
        mSignInFbButton.setOnClickListener(mSignInFbButtonListener);
        return view;
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    public void init(View view) {
        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);
        mSignInFbButton = (ImageButton) view.findViewById(R.id.sign_in_fb_btn);
        mSignInGoogleButton = (ImageButton) view.findViewById(R.id.sign_in_google_btn);
        mSignInEmail = (EditText) view.findViewById(R.id.sign_in_email);
        mSignInPassword = (EditText) view.findViewById(R.id.sign_in_pass);
        mSignInProgress = (ProgressBar) view.findViewById(R.id.sign_in_progress);
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mGoogleConnectionResult.hasResolution()) {
            try {
                mGoogleIntentInProgress = true;
                mGoogleConnectionResult.startResolutionForResult(getActivity(), REQUEST_GOOGLE_LOGIN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mGoogleIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    //perform google authentication
    private void getGoogleOAuthTokenAndLogin() {
        disableViews();
        /* Get OAuth token in Background */
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try {
                    String scope = String.format("oauth2:%s", Scopes.PLUS_LOGIN);
                    token = GoogleAuthUtil.getToken(getActivity(), Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                } catch (IOException transientEx) {
                    /* Network or server error */
                    Log.e(TAG, "Error authenticating with Google: " + transientEx);
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (UserRecoverableAuthException e) {
                    Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
                    if (!mGoogleIntentInProgress) {
                        mGoogleIntentInProgress = true;
                        Intent recover = e.getIntent();
                        startActivityForResult(recover, REQUEST_GOOGLE_LOGIN);
                    }
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
                    errorMessage = "Error authenticating with Google: " + authEx.getMessage();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                mGoogleLoginClicked = false;
                if (token != null) {
                    /* Successfully got OAuth token, now login with Google */

                    FirebaseHelper.getRoot().authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            if (FirebaseHelper.isUser(FirebaseHelper.getAuthId())) {
                                Intent i = new Intent(getActivity(), HomeActivity.class);
                                startActivity(i);
                                getActivity().finish();

                            } else {
                                SnackbarHelper.create(mRootView, "You're not registered. Please Sign up").show();
                            }
                            enableViews();
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                            enableViews();
                        }
                    });
                } else if (errorMessage != null) {
                    enableViews();
                    SnackbarHelper.create(mRootView, errorMessage).show();
                }
            }
        };
        task.execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getGoogleOAuthTokenAndLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mGoogleIntentInProgress) {
            /* Store the ConnectionResult so that we can use it later when the user clicks on the Google+ login button */
            mGoogleConnectionResult = connectionResult;

            if (mGoogleLoginClicked) {
                /* The user has already clicked login so we attempt to resolve all errors until the user is signed in,
                 * or they cancel. */
                resolveSignInError();
            } else {
                Log.e(TAG, connectionResult.toString());
            }
        }
    }
}
