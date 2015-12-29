package com.example.stranger.me.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.widget.NonSwipeableViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentMain extends Fragment implements SignUpFragmentScreen1.ViewChangeListener{

    private PagerAdapter mSignUpPagerAdapter;
    //fragments that will be shown in viewpager
    private final Fragment[] mFragments = {SignUpFragmentScreen1.newInstance(),SignUpFragmentScreen2.newInstance(),
            SignUpFragmentScreen3.newInstance(),SignUpFragmentScreen4.newInstance()};

    private NonSwipeableViewPager mSignUpViewPager;

    private Button mNextBtn,mSkipBtn;
    private ImageButton mBackBtn;
    private SignUpPagerChangeListener mSignUpPageChangeListener;


    //initializing listeners
    /*Next button listener*/
    private View.OnClickListener mNextBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mSignUpViewPager.setCurrentItem(mSignUpViewPager.getCurrentItem() + 1);
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
                    mBackBtn.setVisibility(View.VISIBLE);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the layout of fragment_sign_up_main
        View view = inflater.inflate(R.layout.fragment_sign_up_main, container, false);
        //initializing views
        init(view);

        //initialize pager adapter for view pager
        mSignUpPagerAdapter = new PagerAdapter(getChildFragmentManager(),mFragments);
        mSignUpPageChangeListener = (SignUpPagerChangeListener) getActivity();

        //set pagerAdapter to the viewpager
        mSignUpViewPager.setAdapter(mSignUpPagerAdapter);
        mSignUpViewPager.addOnPageChangeListener(mPageChangeListener);

        //set listeners to buttons
        mNextBtn.setOnClickListener(mNextBtnListener);
        mSkipBtn.setOnClickListener(mSkipBtnListener);
        mBackBtn.setOnClickListener(mBackBtnListener);

        return view;
    }
    private void init(View view){
        mSignUpViewPager = (NonSwipeableViewPager) view.findViewById(R.id.sign_up_viewpager);
        mNextBtn = (Button) view.findViewById(R.id.btn_sign_up_next);
        mSkipBtn = (Button) view.findViewById(R.id.btn_sign_up_skip);
        mBackBtn = (ImageButton) view.findViewById(R.id.btn_sign_up_back);
    }
    @Override
    public void onClick(int i) {
        mSignUpViewPager.setCurrentItem(i);
    }

    public interface SignUpPagerChangeListener{
        public void onChange(int position);
    }


}
