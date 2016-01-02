package com.example.stranger.me.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Farooq on 11/22/2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private Fragment[] fragments;
    private String[] titles;
    public PagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments = fragments;
    }
    public PagerAdapter(FragmentManager fm, Fragment[] fragments,String[] titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    public Fragment[] getFragments() {
        return fragments;
    }

}
