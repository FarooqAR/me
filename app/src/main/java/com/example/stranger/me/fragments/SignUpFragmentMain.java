package com.example.stranger.me.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.widget.NonSwipeableViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentMain extends Fragment {

    private PagerAdapter signUpPagerAdapter;
    //fragments that will be shown in viewpager
    private final Fragment[] fragments = {new SignUpFragmentScreen1(),new SignUpFragmentScreen2()};

    private NonSwipeableViewPager signUpViewPager;

    public SignUpFragmentMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get the layout of fragment_sign_up_main
        View view = inflater.inflate(R.layout.fragment_sign_up_main, container, false);

        //initialize pager adapter for view pager
        signUpPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(),fragments);

        //initializing views
        signUpViewPager = (NonSwipeableViewPager) view.findViewById(R.id.sign_up_viewpager);

        //set pagerAdapter to the viewpager
        signUpViewPager.setAdapter(signUpPagerAdapter);

        return view;
    }

}
