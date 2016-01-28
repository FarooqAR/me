package com.example.stranger.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PagerAdapter;
import com.example.stranger.me.fragment.GroupConversationFragment;
import com.example.stranger.me.fragment.GroupMemberFragment;
import com.example.stranger.me.fragment.GroupRequestsFragment;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.GroupHelper;

public class GroupActivity extends AppCompatActivity {
    public static final String CURRENT_INDEX = "current_index";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_KEY = "group_key";
    public static final String GROUP_CONVERSATION = "group_conversation";
    private Fragment[] mFragments;
    private String[] mFragmentTitles = {"Chat", "Members", "Requests"};
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private String mGroupName;
    private String mGroupKey;
    private String mGroupConversation;
    private PagerTitleStrip mPagerStrip;
    private PagerAdapter mAdapter;
    private int mCurrentIndex;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            mCurrentIndex = getIntent().getIntExtra(CURRENT_INDEX, 0);
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mGroupKey = getIntent().getStringExtra(GROUP_KEY);
            mGroupConversation = getIntent().getStringExtra(GROUP_CONVERSATION);
        }
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(CURRENT_INDEX);
        }
        if(GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(),mGroupKey) == -1){
            mFragments = new Fragment[]
                    {GroupMemberFragment.newInstance(mGroupKey)};
            mFragmentTitles = new String[]{"Members"};
        }
        else if(GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(),mGroupKey) != 3 && GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(),mGroupKey) != -1) {
            mFragments = new Fragment[]
                    {GroupConversationFragment.newInstance(mGroupConversation, mGroupKey), GroupMemberFragment.newInstance(mGroupKey)};
            mFragmentTitles = new String[]{"Chat", "Members"};
        }
        else if(GroupHelper.getAccessLevel(FirebaseHelper.getAuthId(),mGroupKey) ==3){
            mFragments = new Fragment[]
                    {GroupConversationFragment.newInstance(mGroupConversation, mGroupKey), GroupMemberFragment.newInstance(mGroupKey), GroupRequestsFragment.newInstance(mGroupKey)};
            mFragmentTitles = new String[]{"Chat", "Members","Requests"};
        }
        setContentView(R.layout.activity_group);
        mViewPager = (ViewPager) findViewById(R.id.group_viewpager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPagerStrip = (PagerTitleStrip) findViewById(R.id.group_viewPager_indicator);
        mAdapter = new PagerAdapter(getSupportFragmentManager(), mFragments, mFragmentTitles);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mPageListener);
        mViewPager.setCurrentItem(mCurrentIndex);
        mToolbar.setTitle(mGroupName);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragments[0].onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_INDEX, mCurrentIndex);
    }
}
