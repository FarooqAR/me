package com.example.stranger.me.activity;

import android.content.Intent;
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

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.NavDrawerListAdapter;
import com.example.stranger.me.fragment.ChatFragment;
import com.example.stranger.me.fragment.FindContactFragment;
import com.example.stranger.me.fragment.GroupsFragment;
import com.example.stranger.me.fragment.HomeFragment;
import com.example.stranger.me.fragment.MusicFragment;
import com.example.stranger.me.fragment.ProfileFragment;
import com.example.stranger.me.fragment.SettingsFragment;
import com.example.stranger.me.helper.ChatHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Friend;
import com.example.stranger.me.modal.FriendRequest;
import com.example.stranger.me.modal.NavDrawerListItem;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity{
    private static final String TAG = "HomeActivity";
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private String mToolbarTitle;
    private FragmentManager mFragmentManager;
    private TypedArray mNavListItems;
    private int mIndex;
    private PrivateChatListener mPrivateChatRetrieveListener;
    private boolean privateChatRetrieved;
    private ValueEventListener mPrivateChatListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ChatHelper.setPrivateChatNode(dataSnapshot);
            if(!privateChatRetrieved)
            mPrivateChatRetrieveListener.onPrivateChatDataRetrieved();
            privateChatRetrieved = true;
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private Fragment[] mFragments = {HomeFragment.newInstance(), ProfileFragment.newInstance(), MusicFragment.newInstance(),
             GroupsFragment.newInstance(), ChatFragment.newInstance(), FindContactFragment.newInstance(),
            SettingsFragment.newInstance()};
    private String[] mFragmentTags = {"Home", "Profile", "My Music", "Groups", "Chat", "Find Contacts", "Settings"};
    private FragmentManager.OnBackStackChangedListener mBackStackListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            Fragment currentFragment = mFragmentManager.findFragmentById(R.id.content_frame);
            mToolbarTitle = currentFragment.getTag();
            if(mToolbarTitle.equals("Profile")){
                getSupportActionBar().hide();
            }
            else{
                getSupportActionBar().show();
            }
            getSupportActionBar().setTitle(mToolbarTitle);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //set online status to true
        FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("online").setValue(true);
        init();
        mPrivateChatRetrieveListener = (PrivateChatListener) mFragments[4];
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
        ArrayList<Friend> friends = new ArrayList<Friend>();
        FirebaseHelper.setFriends(friends);

        ArrayList<FriendRequest> friendRequests = new ArrayList<FriendRequest>();
        FirebaseHelper.setFriendRequests(friendRequests);
        FirebaseHelper.getRoot().child("users").startAt().orderByChild("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseHelper.setUsers(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        FirebaseHelper.getRoot().child("friends").child(FirebaseHelper.getAuthId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Friend friend = dataSnapshot.getValue(Friend.class);
                friend.setId(dataSnapshot.getKey());
                FirebaseHelper.addFriend(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FirebaseHelper.removeFriend(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseHelper.setFriendRequests(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        FirebaseHelper.getRoot().child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseHelper.setFRIENDS(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        FirebaseHelper.getRoot().child("private_chat").addValueEventListener(mPrivateChatListener);
        FirebaseHelper.getRoot().child(FirebaseHelper.FRIEND_REQUESTS_KEY).child(FirebaseHelper.getAuthId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendRequest request = dataSnapshot.getValue(FriendRequest.class);
                request.setId(dataSnapshot.getKey());
                FirebaseHelper.addFriendRequest(request);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FirebaseHelper.removeFriendRequest(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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
        //set online status to false

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseHelper.getAuthId() != null) {
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("online").setValue(false);
        }
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
                setFragment(mIndex);
                return true;
            case R.id.menu_logout:
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("online").setValue(false);
                FirebaseHelper.getRoot().unauth();
                FirebaseHelper.setAuthId(null);
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragments[mIndex].onActivityResult(requestCode,resultCode,data);
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
    public interface PrivateChatListener{
        void onPrivateChatDataRetrieved();
    }
}
