package com.example.stranger.me.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;

public class ProfileFragment extends Fragment {
    private static final String USER_ID = "user_id";
    private static final String TAG = "ProfileFragment";
    private ProfileListener mListener;
    private ViewPager mViewPager;
    private Fragment[] mFragments;
    private String[] mFragmentTitles = {"Posts", "Friends", "Images"};
    private PagerAdapter mAdapter;
    private int mCurrentIndex;
    private String mUserId;
    private PagerTabStrip mPagerTabStrip;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(USER_ID);
        } else {
//            mUserId = FirebaseHelper.getAuthId();
        }
        mFragments = new Fragment[3];
        mFragments[0] = HomeFragment.newInstance(mUserId);
        mFragments[1] = FriendsListFragment.newInstance();
        mFragments[2] = ImagesFragment.newInstance(mUserId);
        mAdapter = new PagerAdapter(getChildFragmentManager(), mFragments, mFragmentTitles);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        mViewPager.setAdapter(mAdapter);
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        return view;
    }

    private void init(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.profile_viewpager);
        mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.profile_viewpager_indicator);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileListener) {
            mListener = (ProfileListener) context;
        } else {
          //  throw new RuntimeException(context.toString() + " must implement ProfileListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface ProfileListener {
        // TODO: Update argument type and name
        void methodName();
    }
}
