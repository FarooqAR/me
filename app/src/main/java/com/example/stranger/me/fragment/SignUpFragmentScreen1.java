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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.stranger.me.R;
import com.example.stranger.me.activity.FacebookActivity;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.InputHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.widget.RobotoEditText;
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
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen1 extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_GOOGLE_LOGIN = 60;
    private static final String TAG = "SignUpFragmentScreen1";
    public static final int REQUEST_FB_SIGNUP = 40;
    private Button mSignInButton;
    private Button mSignUpButton;
    private ImageButton mSignUpGoogleBtn;
    private ImageButton mSignUpFbBtn;
    private RobotoEditText mSignUpEmail;
    private RobotoEditText mSignUpPassword;
    private ProgressBar mSignUpProgress;
    private RelativeLayout mRootView;
    private boolean mGoogleLoginClicked;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mGoogleConnectionResult;
    private boolean mGoogleIntentInProgress;
    //facebook token listener
    //the activity have to change the item in viewpager on click on signin button
    private SignUpScreen1Listener mListenerActivity;

    //the fragment have to change the item in its viewpager on click signUp button
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
            if (InputHelper.isVaildEmailPassword(v, e, p)) {
                FirebaseHelper.getRoot().unauth();
                FirebaseHelper.setAuthId(null);
                disableViews();
                createUser(e, p);
            }
        }
    };

    private View.OnClickListener mSignUpGoogleBtnListener = new View.OnClickListener() {
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
    private View.OnClickListener mSignUpFbBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseHelper.getRoot().unauth();
            FirebaseHelper.setAuthId(null);
            Intent intent = new Intent(getActivity(), FacebookActivity.class);
            getParentFragment().startActivityForResult(intent, REQUEST_FB_SIGNUP);
        }
    };


    public SignUpFragmentScreen1() {
        // Required empty public constructor
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
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void authWithFacebook(String token) {
        if (token != null) {
            disableViews();
            FirebaseHelper.getRoot().authWithOAuthToken("facebook", token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    new HandleFbResultTask().execute(authData);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();

                    enableViews();

                }
            });
        }
    }

    private void createUser(final String e, final String p) {
        FirebaseHelper.getRoot().createUser(e, p, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                FirebaseHelper.getRoot().authWithPassword(e, p, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        mListenerFragment.onClick(1);
                        enableViews();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();//give any view
                        enableViews();
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                enableViews();
            }
        });
    }

    public static SignUpFragmentScreen1 newInstance() {
        SignUpFragmentScreen1 fragment = new SignUpFragmentScreen1();
        return fragment;
    }

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
        mSignUpFbBtn.setOnClickListener(mSignUpFbBtnListener);
        mSignUpGoogleBtn.setOnClickListener(mSignUpGoogleBtnListener);
        return view;
    }

    public void init(View view) {
        mSignInButton = (Button) view.findViewById(R.id.btn_sign_in);
        mSignUpButton = (Button) view.findViewById(R.id.btn_sign_up);
        mSignUpEmail = (RobotoEditText) view.findViewById(R.id.sign_up_email);
        mSignUpPassword = (RobotoEditText) view.findViewById(R.id.sign_up_pass);
        mSignUpProgress = (ProgressBar) view.findViewById(R.id.sign_up_progress);
        mSignUpGoogleBtn = (ImageButton) view.findViewById(R.id.sign_up_google_btn);
        mSignUpFbBtn = (ImageButton) view.findViewById(R.id.sign_up_fb_btn);
        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
    }

    private void disableViews() {
        mSignUpButton.setText("");
        mSignUpButton.setEnabled(false);

        mSignInButton.setEnabled(false);
        mSignUpFbBtn.setEnabled(false);
        mSignUpGoogleBtn.setEnabled(false);
        mSignUpEmail.setEnabled(false);
        mSignUpPassword.setEnabled(false);

        mSignUpProgress.setVisibility(View.VISIBLE);
    }

    private void enableViews() {
        mSignUpButton.setText("Sign Up");
        mSignUpProgress.setVisibility(View.GONE);
        mSignUpFbBtn.setEnabled(true);
        mSignUpGoogleBtn.setEnabled(true);
        mSignUpButton.setEnabled(true);
        mSignInButton.setEnabled(true);
        mSignUpEmail.setEnabled(true);
        mSignUpPassword.setEnabled(true);
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

                            new HandleGoogleResultTask().execute(authData);
                            enableViews();

                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            enableViews();
                            SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
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
        } else if (requestCode == REQUEST_FB_SIGNUP) {
            String token = data.getStringExtra(FacebookActivity.TOKEN);
            if (token != null) {

                authWithFacebook(token);
            }
        }
    }

    public interface SignUpScreen1Listener {

        void onClick(int i);

    }


    private class HandleFbResultTask extends AsyncTask<AuthData, Void, Map<String, Object>> {

        @Override
        protected Map<String, Object> doInBackground(AuthData... params) {
            Map<String, Object> providerData = params[0].getProviderData();
            Map<String, Object> cachedUserProfile = (Map<String, Object>) providerData.get("cachedUserProfile");//contains id,name,first_name,last_name,gender,email

            String firstName = (String) cachedUserProfile.get("first_name");
            String lastName = (String) cachedUserProfile.get("last_name");
            String profileImageUrl = (String) providerData.get("profileImageURL");

            Map<String, Object> user = new HashMap<>();
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("profileImageURL", profileImageUrl);
            return user;
        }

        @Override
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            if (FirebaseHelper.isUser(FirebaseHelper.getAuthId())) {
                SnackbarHelper.create(mRootView, "You're already registered. Please Login").show();
                enableViews();
            } else {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).updateChildren(stringObjectMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            startActivity(i);
                            getActivity().finish();

                        }
                        mSignUpProgress.setVisibility(View.GONE);
                        enableViews();
                        mSignUpButton.setText("Sign Up");
                    }
                });
            }

        }
    }

    private class HandleGoogleResultTask extends AsyncTask<AuthData, Void, Map<String, Object>> {
        @Override
        protected Map<String, Object> doInBackground(AuthData... params) {
            Map<String, Object> providerData = params[0].getProviderData();
            Map<String, Object> cachedUserProfile = (Map<String, Object>) providerData.get("cachedUserProfile");
            Map<String, Object> data = new HashMap<>();
            String firstname = (String) cachedUserProfile.get("given_name");
            String lastname = (String) cachedUserProfile.get("family_name");
            String profileImageURL = (String) providerData.get("profileImageURL");
            data.put("firstName", firstname);
            data.put("lastName", lastname);
            data.put("profileImageURL", profileImageURL);
            return data;
        }

        @Override
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            if (FirebaseHelper.isUser(FirebaseHelper.getAuthId())) {

                enableViews();

            } else {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).updateChildren(stringObjectMap, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            startActivity(i);
                            getActivity().finish();

                        }
                        enableViews();
                    }
                });
            }
        }
    }
}
