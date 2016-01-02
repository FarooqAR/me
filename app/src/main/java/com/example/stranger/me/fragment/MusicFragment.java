package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;


public class MusicFragment extends Fragment {

    private static final String TAG = "MusicFragment";
    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private Fragment[] mFragments = {AllMusicFragment.newInstance(), FavoriteMusicFragment.newInstance(), PlaylistFragment.newInstance()};
    private String[] mFragmentTitles = {"All Music", "Favorite", "Playlist"};
    private PagerAdapter mAdapter;
    private int mCurrentIndex;
    private PagerTabStrip mPagerTabStrip;

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

    public MusicFragment() {
        // Required empty public constructor
    }


    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();

        return fragment;
    }

    public void init(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.music_viewpager);
        mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.music_viewpager_indicator);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new PagerAdapter(getChildFragmentManager(), mFragments, mFragmentTitles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        init(view);
        mViewPager.setAdapter(mAdapter);
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt("currentIndex");
        } else {
            mCurrentIndex = 0;
        }
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(mPageListener);
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", mCurrentIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewPager.removeOnPageChangeListener(mPageListener);
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
