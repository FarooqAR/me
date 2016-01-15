package com.example.stranger.me.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.widget.NonSwipeableViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentMain extends Fragment implements SignUpFragmentScreen1.SignUpScreen1Listener{

    private PagerAdapter mSignUpPagerAdapter;
    //fragments that will be shown in viewpager
    private  Fragment[] mFragments = {SignUpFragmentScreen1.newInstance(),SignUpFragmentScreen2.newInstance(),
            SignUpFragmentScreen3.newInstance(),SignUpFragmentScreen4.newInstance()};

    private NonSwipeableViewPager mSignUpViewPager;

    private Button mNextBtn,mSkipBtn;
    private ImageButton mBackBtn;
    private ProgressBar mNextProgress;

    //the other screens have to listen for changes in viewpager
    private SignUpPagerChangeListener mSignUpPageChangeListener;
    private SignUpPagerChangeListener mSignUpPageChangeListenerScreen2;
    private SignUpPagerChangeListener mSignUpPageChangeListenerScreen3;
    private SignUpPagerChangeListener mSignUpPageChangeListenerScreen4;

    public SignUpFragmentScreen4 getScreen4(){
        return (SignUpFragmentScreen4) mFragments[3];
    }

    //initializing listeners
    /*Next button listener*/
    private View.OnClickListener mNextBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mSignUpViewPager.getCurrentItem() == 1){//means its screen 2
                mSignUpPageChangeListenerScreen2.onNextButtonClick();
            }else if(mSignUpViewPager.getCurrentItem() == 2){//means its screen 3
                mSignUpPageChangeListenerScreen3.onNextButtonClick();
            }else if(mSignUpViewPager.getCurrentItem() == 3) {//means its screen 4
                mSignUpPageChangeListenerScreen4.onNextButtonClick();
            }
            else {
                mSignUpViewPager.setCurrentItem(mSignUpViewPager.getCurrentItem() + 1);
            }

        }
    };
    /*Skip Button Listener*/
    private View.OnClickListener mSkipBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mSignUpViewPager.setCurrentItem(3);
        }
    };
    /*Back button listener*/
    private View.OnClickListener mBackBtnListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            mSignUpViewPager.setCurrentItem(mSignUpViewPager.getCurrentItem()-1);
        }
    };
    /*page change listener*/
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


        }

        @Override
        public void onPageSelected(int position) {
            mSignUpPageChangeListener.onChange(position);
            switch (position){
                case 0:
                    mBackBtn.setVisibility(View.GONE);
                    mNextBtn.setVisibility(View.GONE);
                    mSkipBtn.setVisibility(View.GONE);
                    break;
                case 1:
                    mBackBtn.setVisibility(View.GONE);
                    mNextBtn.setVisibility(View.VISIBLE);
                    mSkipBtn.setVisibility(View.GONE);
                    break;
                case 2:
                    mSkipBtn.setVisibility(View.VISIBLE);
                    mNextBtn.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                    mBackBtn.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                    mNextBtn.setText("NEXT");

                    break;
                case 3:
                    mNextBtn.setText("FINISH");
                    mSkipBtn.setVisibility(View.GONE);
                    mNextBtn.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                    mBackBtn.setVisibility(View.VISIBLE);//to avoid being invisible on rotation
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    public SignUpFragmentMain() {
        // Required empty public constructor
    }


    public static SignUpFragmentMain newInstance() {
        SignUpFragmentMain fragment = new SignUpFragmentMain();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize pager adapter for view pager
        mSignUpPagerAdapter = new PagerAdapter(getChildFragmentManager(),mFragments);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof SignUpPagerChangeListener) {
            mSignUpPageChangeListener = (SignUpPagerChangeListener) activity;
        }
        else{
            throw new ClassCastException(activity.toString() +
                    " must implement SignUpPagerChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSignUpPageChangeListener = null;
        mSignUpPageChangeListenerScreen2 = null;
        mSignUpPageChangeListenerScreen3 = null;
        mSignUpPageChangeListenerScreen4 = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode>=0 && requestCode == SignUpFragmentScreen4.REQUEST_AVATAR){
            mFragments[3].onActivityResult(requestCode,resultCode,data);
        }
        else if(requestCode>=0 && requestCode == SignUpFragmentScreen4.REQUEST_IMAGE_CAPTURE){
            mFragments[3].onActivityResult(requestCode,resultCode,data);
        }
        else if(requestCode>=0 && requestCode == SignUpFragmentScreen4.RESULT_BROWSE_IMAGE){
            mFragments[3].onActivityResult(requestCode,resultCode,data);
        }
        else if(requestCode>=0 && requestCode == SignUpFragmentScreen1.REQUEST_FB_SIGNUP) {
            mFragments[0].onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the layout of fragment_sign_up_main
        View view = inflater.inflate(R.layout.fragment_sign_up_main, container, false);
        //initializing views
        init(view);


        mSignUpPageChangeListener = (SignUpPagerChangeListener) getActivity();

        //set pagerAdapter to the viewpager
        mSignUpViewPager.setAdapter(mSignUpPagerAdapter);


        mSignUpViewPager.addOnPageChangeListener(mPageChangeListener);
        mSignUpPageChangeListenerScreen2 = (SignUpPagerChangeListener) mFragments[1];
        mSignUpPageChangeListenerScreen3 = (SignUpPagerChangeListener) mFragments[2];
        mSignUpPageChangeListenerScreen4 = (SignUpPagerChangeListener) mFragments[3];
        //set listeners to buttons
        mNextBtn.setOnClickListener(mNextBtnListener);
        mSkipBtn.setOnClickListener(mSkipBtnListener);
        mBackBtn.setOnClickListener(mBackBtnListener);

        return view;
    }
    @Override
    public void onClick(int i) {
        mSignUpViewPager.setCurrentItem(i);
    }
    private void init(View view){
        mSignUpViewPager = (NonSwipeableViewPager) view.findViewById(R.id.sign_up_viewpager);
        mNextBtn = (Button) view.findViewById(R.id.btn_sign_up_next);
        mSkipBtn = (Button) view.findViewById(R.id.btn_sign_up_skip);
        mBackBtn = (ImageButton) view.findViewById(R.id.btn_sign_up_back);
        mNextProgress = (ProgressBar) view.findViewById(R.id.sign_up_next_progress);
    }
    public void disableButtons(){
        mNextBtn.setText("");
        mBackBtn.setEnabled(false);
        mNextBtn.setEnabled(false);
        mSkipBtn.setEnabled(false);
        mNextProgress.setVisibility(View.VISIBLE);
    }
    public void enableButtons(){
        mNextBtn.setText("Next");
        mBackBtn.setEnabled(true);
        mNextBtn.setEnabled(true);
        mSkipBtn.setEnabled(true);
        mNextProgress.setVisibility(View.GONE);
    }
    public void setViewPagerItem(int position){
        mSignUpViewPager.setCurrentItem(position);
    }

    public interface SignUpPagerChangeListener{
        public void onChange(int position);
        public void onNextButtonClick();
    }


}
