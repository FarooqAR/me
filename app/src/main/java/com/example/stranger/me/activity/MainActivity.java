package com.example.stranger.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.fragment.LoginFragment;
import com.example.stranger.me.fragment.SignUpFragmentMain;
import com.example.stranger.me.fragment.SignUpFragmentScreen1;
import com.example.stranger.me.helper.ChatHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SharedPreferenceHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.widget.NonSwipeableViewPager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.soundcloud.android.crop.Crop;

public class MainActivity extends AppCompatActivity implements SignUpFragmentScreen1.SignUpScreen1Listener, SignUpFragmentMain.SignUpPagerChangeListener {
    private static final String TAG = "MainActivity";

    private LinearLayout mSplashContainer;
    private ProgressBar mSplashProgress;
    private NonSwipeableViewPager mViewPager;
    private LinearLayout mRingsContainer;
    private RelativeLayout mRoot;
    private RelativeLayout mProfileStep1;
    private RelativeLayout mProfileStep2;
    private RelativeLayout mProfileStep3;
    private TextView mProfileStepText1;
    private TextView mProfileStepText2;
    private TextView mProfileStepText3;
    private ImageView mProfileStepImage1;
    private ImageView mProfileStepImage2;
    private ImageView mProfileStepImage3;

    private Fragment[] mFragments = {LoginFragment.newInstance(), SignUpFragmentMain.newInstance()};
    private PagerAdapter mPagerAdapter;
    float top;
    float left;
    int splashH;
    boolean isRotated;
    FragmentTransaction transaction;
    private Firebase.AuthStateListener mAuthStateListener = new Firebase.AuthStateListener() {
        @Override
        public void onAuthStateChanged(AuthData authData) {

            if (authData == null || FirebaseHelper.getAuthId() == null) {
                animateSplash(0, 500);
                FirebaseHelper.getRoot().removeAuthStateListener(mAuthStateListener);
            } else if (FirebaseHelper.getAuthId() != null){
                final Intent i = new Intent(MainActivity.this, HomeActivity.class);
                if (getIntent() != null && getIntent().getStringExtra(HomeActivity.FRIEND_ID) != null) {
                    i.putExtra(HomeActivity.FRIEND_ID, getIntent().getStringExtra(HomeActivity.FRIEND_ID));
                }
                FirebaseHelper.getRoot().child("private_chat").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChatHelper.setPrivateChatNode(dataSnapshot);
                        startActivity(i);
                        finish();
                        animateSplash(1000, 500);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        animateSplash(0, 500);
                        SnackbarHelper.create(mRoot,"There is a problem in connection").show();
                    }
                });


            }

        }
    };
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        public void onGlobalLayout() {
            mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            int[] locations = new int[2];
            mSplashContainer.getLocationOnScreen(locations);
            top = locations[0];
            top = top + top / 3;
            left = locations[1];
            left = left + left / 2;
            FirebaseHelper.getRoot().addAuthStateListener(mAuthStateListener);


            splashH = mSplashContainer.getHeight();
            if (!isLandscape())
                mRingsContainer.setPadding(0, splashH, 0, 0);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
        SharedPreferenceHelper.setContext(this);
        if (savedInstanceState != null) {
            isRotated = savedInstanceState.getBoolean("isRotated");
        }
        FirebaseHelper.getRoot().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseHelper.setUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        init();
        transaction = getSupportFragmentManager().beginTransaction();

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mPagerAdapter);
        //the x and y position of mSplashContainer cant be obtained until the viewTree is fully loaded
        //so set the listener that will call when it does.
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRotated", true);
    }

    //check screen orientation
    public boolean isLandscape() {
        return this.getResources().getBoolean(R.bool.is_landscape);
    }

    public void init() {
        mRoot = (RelativeLayout) findViewById(R.id.root_main);
        mSplashContainer = (LinearLayout) findViewById(R.id.splashContainer);
        mSplashProgress = (ProgressBar) findViewById(R.id.splash_progress);
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.signup_signin_viewpager);
        mRingsContainer = (LinearLayout) findViewById(R.id.step_rings_container);
        mProfileStep1 = (RelativeLayout) findViewById(R.id.step_1);
        mProfileStep2 = (RelativeLayout) findViewById(R.id.step_2);
        mProfileStep3 = (RelativeLayout) findViewById(R.id.step_3);
        mProfileStepText1 = (TextView) findViewById(R.id.profile_step_text_1);
        mProfileStepText2 = (TextView) findViewById(R.id.profile_step_text_2);
        mProfileStepText3 = (TextView) findViewById(R.id.profile_step_text_3);
        mProfileStepImage1 = (ImageView) findViewById(R.id.profile_step_img_1);
        mProfileStepImage2 = (ImageView) findViewById(R.id.profile_step_img_2);
        mProfileStepImage3 = (ImageView) findViewById(R.id.profile_step_img_3);
    }

    public void animateSplash(int delay, final int duration) {
        //animate the splash to top if screen is in portrait and not yet rotated
        if (!isLandscape() && !isRotated) {
            mSplashContainer.animate().setStartDelay(delay).translationY(-top).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {

                    mViewPager.setVisibility(View.VISIBLE);
                }
            }).withStartAction(new Runnable() {
                @Override
                public void run() {
                    mSplashProgress.animate().alpha(0).setDuration(duration);
                }
            });

        }
        //dont animate the splash if screen is rotated, just change the position of splash
        else if (!isLandscape() && isRotated) {
            mSplashContainer.setTranslationY(-top);
            mViewPager.setVisibility(View.VISIBLE);
            mSplashProgress.setVisibility(View.INVISIBLE);
        }
        //animate the splash to left if screen is in landscape and is not yet rotated
        else if (isLandscape() && !isRotated) {
            mSplashContainer.animate().setStartDelay(delay).translationX(-left).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mSplashProgress.setVisibility(View.INVISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                }
            }).withStartAction(new Runnable() {
                @Override
                public void run() {
                    mSplashProgress.animate().alpha(0).setDuration(duration);
                }
            });
        }
        //just change the position of splash
        else if (isLandscape() && isRotated) {
            mSplashContainer.setTranslationX(-left);
            mSplashProgress.setVisibility(View.INVISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(int i) {
        mViewPager.setCurrentItem(i);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPagerAdapter = null;
    }

    @Override
    public void onChange(int position) {
        switch (position) {
            case 0:
                mRingsContainer.setVisibility(View.GONE);
                break;
            case 1:
                mRingsContainer.setVisibility(View.VISIBLE);
                mProfileStep1.setAlpha(1);
                mProfileStep2.setAlpha((float) 0.2);
                mProfileStep3.setAlpha((float) 0.2);
                mProfileStepImage1.setVisibility(View.INVISIBLE);
                mProfileStepImage2.setVisibility(View.INVISIBLE);
                mProfileStepImage3.setVisibility(View.INVISIBLE);
                mProfileStepText1.setVisibility(View.VISIBLE);
                mProfileStepText2.setVisibility(View.VISIBLE);
                mProfileStepText3.setVisibility(View.VISIBLE);
                break;
            case 2:
                mSplashContainer.setVisibility(View.VISIBLE);
                mRingsContainer.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                if (!isLandscape()) {
                    mRingsContainer.setPadding(0, splashH, 0, 0);
                } else {
                    mRingsContainer.setPadding(0, 0, 0, 0);
                }
                mProfileStep1.setAlpha(1);
                mProfileStep2.setAlpha(1);
                mProfileStep3.setAlpha((float) 0.2);
                mProfileStepImage1.setVisibility(View.VISIBLE);
                mProfileStepImage2.setVisibility(View.INVISIBLE);
                mProfileStepImage3.setVisibility(View.INVISIBLE);
                mProfileStepText1.setVisibility(View.INVISIBLE);
                mProfileStepText2.setVisibility(View.VISIBLE);
                mProfileStepText3.setVisibility(View.VISIBLE);

                break;
            case 3:
                mRingsContainer.setPadding(0, 0, 0, 0);
                mRingsContainer.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                mSplashContainer.setVisibility(View.INVISIBLE);
                mProfileStep1.setAlpha(1);
                mProfileStep2.setAlpha(1);
                mProfileStep3.setAlpha(1);
                mProfileStepImage1.setVisibility(View.VISIBLE);
                mProfileStepImage2.setVisibility(View.VISIBLE);
                mProfileStepImage3.setVisibility(View.INVISIBLE);
                mProfileStepText1.setVisibility(View.INVISIBLE);
                mProfileStepText2.setVisibility(View.INVISIBLE);
                mProfileStepText3.setVisibility(View.VISIBLE);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SignUpFragmentMain f = (SignUpFragmentMain) mFragments[1];
        if (requestCode >= 0 && requestCode == Crop.REQUEST_PICK) {
            f.getScreen4().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode >= 0 && requestCode == Crop.REQUEST_CROP) {
            f.getScreen4().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == SignUpFragmentScreen1.REQUEST_GOOGLE_LOGIN) {
            /* This was a request by the Google API */
            mFragments[0].onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNextButtonClick() {

    }
}

