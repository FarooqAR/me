package com.example.stranger.me;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.stranger.me.adapter.NavDrawerListAdapter;
import com.example.stranger.me.modal.NavDrawerListItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mToolbarTitle;
    private ListView mDrawerListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        mToolbarTitle = "Home";
        DrawerListPopulateTask task = new DrawerListPopulateTask();
        task.execute();

        setupDrawer();
    }
    public void init(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.nav_drawer_list);
    }
    public void setupDrawer(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close){
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DrawerListPopulateTask extends AsyncTask<Void,Void,ArrayList>{
        TypedArray navlistitems;
        TypedArray navlistitemiconids;
        ArrayList<NavDrawerListItem> list;
        @Override
        protected ArrayList doInBackground(Void... params) {
            navlistitems = getResources().obtainTypedArray(R.array.navlistitems);
            navlistitemiconids = getResources().obtainTypedArray(R.array.navlistitemicons);
            list = new ArrayList<>();
            for(int i = 0; i < navlistitems.length(); i++){
                String text = navlistitems.getString(i);
                int id = navlistitemiconids.getResourceId(i, 0);
                NavDrawerListItem item =  new NavDrawerListItem(id,text);
                list.add(item);
            }
            navlistitems.recycle();
            navlistitemiconids.recycle();
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
            NavDrawerListAdapter adapter = new NavDrawerListAdapter(HomeActivity.this,R.layout.nav_drawer_listitem,arrayList);
            mDrawerListView.setAdapter(adapter);
        }
    }
}
