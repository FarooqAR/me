package com.example.stranger.me.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.AvatarAdapter;

import java.util.ArrayList;

public class AvatarActivity extends Activity implements AvatarAdapter.onItemClickListener{
    public static final String AVATAR_LINKS = "avatar_links";
    private static final String TAG = "AvatarActivity";
    private TypedArray mLinksTypedArray;// to hold array from resource file
    private ArrayList<String> mLinks;//to hold links
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        mRecyclerView = (RecyclerView) findViewById(R.id.avatar_recyclerview);
        new AvatarListTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onItemClick(final int position) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.rootView), "Select this image?",Snackbar.LENGTH_LONG);

        snackbar.setAction("SELECT", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(AVATAR_LINKS, mLinks.get(position));
                setResult(RESULT_OK, i);
                finish();
            }
        });
        snackbar.show();

    }
    public class AvatarListTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            mLinksTypedArray = getResources().obtainTypedArray(R.array.avatar_links);
            mLinks = new ArrayList<>();
            for (int i = 0; i < mLinksTypedArray.length(); i++) {
                String imgName = mLinksTypedArray.getString(i);
                String link = "http://i.imgur.com/" + imgName;
                mLinks.add(link);
            }
            mLinksTypedArray.recycle();
            return mLinks;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            AvatarAdapter adapter = new AvatarAdapter(AvatarActivity.this, strings);
            mRecyclerView.setLayoutManager(new GridLayoutManager(AvatarActivity.this, 3));
            mRecyclerView.setAdapter(adapter);
        }
    }

}
