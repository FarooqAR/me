package com.example.stranger.me.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.helper.GroupHelper;


public class GroupsFragment extends Fragment {
    private static final String TAG = "GroupsFragment";
    private static final String CURRENT_INDEX = "viewpager_current_index";
    private ViewPager mViewPager;
    private Fragment[] mFragments = {GroupConversationFragment.newInstance(), HomeFragment.newInstance(), GroupMemberFragment.newInstance(),GroupRequestsFragment.newInstance(), GroupListFragment.newInstance()};
    private String[] mFragmentTitles = {"Chat", "Posts", "Members","Requests", "Groups"};
    private PagerAdapter mAdapter;
    private int mCurrentIndex;
    private PagerTabStrip mPagerTabStrip;
    private OnFragmentInteractionListener mListener;

    public Fragment[] getmFragments() {
        return mFragments;
    }

    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentIndex = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public GroupsFragment() {
        // Required empty public constructor
    }


    public static GroupsFragment newInstance() {
        GroupsFragment fragment = new GroupsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX, 0);
        }

        if(GroupHelper.getCurrentGroup()==null){
            mCurrentIndex = mFragments.length - 1;//get to GroupListFragment as there is no conversation to show
        }
        mAdapter = new PagerAdapter(getChildFragmentManager(), mFragments, mFragmentTitles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        init(view);
        GroupListFragment fragment = (GroupListFragment) mFragments[mFragments.length - 1];
        fragment.setParentViewPager(mViewPager);
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(mPageListener);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN &&
                        v instanceof ViewGroup) {
                    ((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        return view;
    }

    private void init(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.groups_viewpager);
        mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.groups_viewpager_indicator);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX, mCurrentIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragments[0].onActivityResult(requestCode,resultCode,data);
        mFragments[1].onActivityResult(requestCode,resultCode,data);
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
