package com.example.stranger.me;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stranger.me.adapter.NavDrawerListAdapter;
import com.example.stranger.me.fragment.ChatFragment;
import com.example.stranger.me.fragment.FindContactFragment;
import com.example.stranger.me.fragment.GroupsFragment;
import com.example.stranger.me.fragment.HomeFragment;
import com.example.stranger.me.fragment.MusicFragment;
import com.example.stranger.me.fragment.ProfileFragment;
import com.example.stranger.me.fragment.SettingsFragment;
import com.example.stranger.me.modal.NavDrawerListItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private String mToolbarTitle;
    private FragmentManager mFragmentManager;
    private TypedArray mNavListItems;
    private int mIndex;
    private Fragment[] mFragments = {HomeFragment.newInstance(), ProfileFragment.newInstance(), MusicFragment.newInstance(),
             GroupsFragment.newInstance(), ChatFragment.newInstance(), FindContactFragment.newInstance(),
            SettingsFragment.newInstance()};
    private String[] mFragmentTags = {"Home", "Profile", "My Music", "Groups", "Chat", "Find Contacts", "Settings"};
    private FragmentManager.OnBackStackChangedListener mBackStackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            Fragment currentFragment = mFragmentManager.findFragmentById(R.id.content_frame);
            mToolbarTitle = currentFragment.getTag();
            getSupportActionBar().setTitle(mToolbarTitle);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            mToolbarTitle = "Home";
            mIndex = 0;
            ft.replace(R.id.content_frame,
                    mFragments[mIndex], mFragmentTags[mIndex])
                    .commit();
        } else {
            mToolbarTitle = savedInstanceState.getString("toolbarTitle");
            mIndex = savedInstanceState.getInt("currentFragmentIndex");
        }


        DrawerListPopulateTask task = new DrawerListPopulateTask();
        task.execute();
        setupDrawer();
        mFragmentManager.addOnBackStackChangedListener(mBackStackListener);

    }

    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.nav_drawer_list);
    }

    public void setupDrawer() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mToolbarTitle);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mToolbarTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    public void setFragment(int index) {
        Fragment fragment = mFragments[index];
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        mIndex = index;
        ft.replace(R.id.content_frame, fragment, mFragmentTags[mIndex]);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {

            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("toolbarTitle", mToolbarTitle);
        outState.putInt("currentFragmentIndex", mIndex);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_settings:
                getSupportActionBar().setTitle("Settings");
                mIndex = mFragments.length - 1;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.content_frame, mFragments[mIndex], mFragmentTags[mIndex]);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case R.id.menu_logout:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }


    public class DrawerListPopulateTask extends AsyncTask<Void, Void, ArrayList> {
        TypedArray navlistitems;
        TypedArray navlistitemiconids;
        ArrayList<NavDrawerListItem> list;

        @Override
        protected ArrayList doInBackground(Void... params) {
            navlistitems = getResources().obtainTypedArray(R.array.navlistitems);
            navlistitemiconids = getResources().obtainTypedArray(R.array.navlistitemicons);
            list = new ArrayList<>();
            for (int i = 0; i < navlistitems.length(); i++) {
                String text = navlistitems.getString(i);
                int id = navlistitemiconids.getResourceId(i, 0);
                NavDrawerListItem item = new NavDrawerListItem(id, text);
                list.add(item);
            }
            mNavListItems = navlistitems;
            navlistitems.recycle();
            navlistitemiconids.recycle();
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            NavDrawerListAdapter adapter = new NavDrawerListAdapter(HomeActivity.this, R.layout.nav_drawer_listitem, arrayList);
            mDrawerListView.setAdapter(adapter);
            mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    TextView item = (TextView) view.findViewById(R.id.nav_drawer_item_text);
                    mToolbarTitle = (String) item.getText();
                    mDrawerLayout.closeDrawers();
                    setFragment(position);
                }
            });

        }
    }
}
