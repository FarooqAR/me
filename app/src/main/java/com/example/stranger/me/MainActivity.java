package com.example.stranger.me;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.fragments.LoginFragment;
import com.example.stranger.me.fragments.SignUpFragmentMain;
import com.example.stranger.me.fragments.SignUpFragmentScreen1;
import com.example.stranger.me.widget.NonSwipeableViewPager;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity implements SignUpFragmentScreen1.ViewChangeListener{
    private static final String TAG = "MainActivity";
    private LinearLayout splashContainer;
    private Fragment[] mFragments = {new SignUpFragmentMain(),new LoginFragment()};
    private NonSwipeableViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    float top;
    float left;
    RelativeLayout root;
    ProgressBar splashProgress;
    boolean isRotated;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        if (savedInstanceState != null) {
            isRotated = savedInstanceState.getBoolean("isRotated");
        }
        transaction = getSupportFragmentManager().beginTransaction();
        root = (RelativeLayout) findViewById(R.id.root_main);
        splashContainer = (LinearLayout) findViewById(R.id.splashContainer);
        splashProgress = (ProgressBar) findViewById(R.id.splash_progress);
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.signup_signin_viewpager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(),mFragments);
        mViewPager.setAdapter(mPagerAdapter);
        //the x and y position of splashContainer cant be obtained until the viewTree is fully loaded
        //so set the listener that will call when it does.
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] locations = new int[2];
                splashContainer.getLocationOnScreen(locations);
                top = locations[0];
                top = top + top/4;
                left = locations[1];
                left = left + left/2;
                animateSplash(1000,500);
            }

        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRotated", true);
    }
    public void animateSplash(int delay, final int duration) {
        //animate the splash to top if screen is in portrait and not yet rotated
        if (!isLandscape() && !isRotated) {
            splashContainer.animate().setStartDelay(delay).translationY(-top).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {

                    mViewPager.setVisibility(View.VISIBLE);
                }
            }).withStartAction(new Runnable() {
                @Override
                public void run() {
                    splashProgress.animate().alpha(0).setDuration(duration);
                }
            });

        }
        //dont animate the splash if screen is rotated, just change the position of splash
        else if (!isLandscape() && isRotated) {
            splashContainer.setTranslationY(-top);
            mViewPager.setVisibility(View.VISIBLE);
            splashProgress.setVisibility(View.INVISIBLE);
        }
        //animate the splash to left if screen is in landscape and is not yet rotated
        else if (isLandscape() && !isRotated) {
            splashContainer.animate().setStartDelay(delay).translationX(-left).setDuration(duration).withEndAction(new Runnable() {
                @Override
                public void run() {
                    splashProgress.setVisibility(View.INVISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                }
            }).withStartAction(new Runnable() {
                @Override
                public void run() {
                    splashProgress.animate().alpha(0).setDuration(duration);
                }
            });
        }
        //just change the position of splash
        else if (isLandscape() && isRotated) {
            splashContainer.setTranslationX(-left);
            splashProgress.setVisibility(View.INVISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
        }

    }

    //check screen orientation
    public boolean isLandscape() {
        return this.getResources().getBoolean(R.bool.is_landscape);
    }


    @Override
    public void onClick(int i) {
        mViewPager.setCurrentItem(i);
    }
}

