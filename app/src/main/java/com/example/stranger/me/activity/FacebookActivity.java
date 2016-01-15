package com.example.stranger.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.stranger.me.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FacebookActivity extends Activity {
    public static final String TOKEN = "facebook_token";
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        mCallbackManager = CallbackManager.Factory.create();
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken token) {
                if (token != null) {
                    Intent i = new Intent();
                    i.putExtra(TOKEN, token.getToken());
                    setResult(RESULT_OK, i);
                }
                finish();
            }
        };
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onError(FacebookException error) {
                finish();
            }
        });


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTokenTracker.startTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTokenTracker.stopTracking();
    }
}
