package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;

public class FriendsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private String[] mFragmentTitles;
    private Fragment[] mFragments;
    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments = new Fragment[]{FriendsListFragment.newInstance()};
        mFragmentTitles = new String[]{"Friends"};
        mAdapter = new PagerAdapter(getChildFragmentManager(),mFragments,mFragmentTitles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        init(view);
        mViewPager.setAdapter(mAdapter);

        return view;
    }

    private void init(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.friends_viewpager);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
