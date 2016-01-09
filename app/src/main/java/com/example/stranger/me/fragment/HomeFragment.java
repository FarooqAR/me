package com.example.stranger.me.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.PostAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.Post;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private static final String USER_ID = "user_id";
    private static final String POST_COUNT = "post_count";
    private static final String START_KEY = "start_key";
    private static final String POSTS = "posts";

    private OnFragmentInteractionListener mListener;
    private FloatingActionButton mHomePostBtn;// button to create posts
    private PostAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private String mUserId;
    private String mStartKey; // index where querying data starts on firebase    must change on scroll
    private int mLimit = 8;//limit for # of data to be queried      must change on scroll
    private int mPostCount = 0;//# of posts queried yet
    private Firebase mPostRef = FirebaseHelper.getRoot().child("posts"); //reference to posts child
    private ChildEventListener mPostListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            mStartKey = dataSnapshot.getKey();
            mPostCount += 1;
//            new PostPopulateTask().execute(dataSnapshot);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    private View.OnClickListener mHomePostBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Hey There", Snackbar.LENGTH_SHORT).show();
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null) {
            mUserId = getArguments().getString(USER_ID);
        }
        else{
            mUserId = null;
        }
        //mAdapter = new PostAdapter(mPosts);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        if(savedInstanceState != null) {
            mPostCount = savedInstanceState.getInt(POST_COUNT);
            mStartKey = savedInstanceState.getString(START_KEY);
            //mPosts = (ArrayList<Post>) savedInstanceState.getSerializable(POSTS);
        }
        else{
            mPostCount = 0;
            mStartKey = null;
        }
        //mRecyclerView.setAdapter(mAdapter);
        mHomePostBtn.setOnClickListener(mHomePostBtnListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //set listeners for posts here
        /*
        if(mStartKey==null){
            if(mUserId==null){
            mPostRef.orderByChild("timestamp").limitToFirst(mLimit).addChildEventListener(mPostListener);
            else
            mPostRef.equalTo("poster_id",mUserId).orderByChild("timestamp").limitToFirst(mLimit).addChildEventListener(mPostListener);
        }
        else{
            if(mUserId==null){
            mPostRef.startAt(null,mStartKey).orderByChild("timestamp").limitToFirst(mLimit).addChildEventListener(mPostListener);
            else
            mPostRef.startAt(null,mStartKey).equalTo("poster_id",mUserId).orderByChild("timestamp").limitToFirst(mLimit).addChildEventListener(mPostListener);
        }
        */
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop listeners for posts here
        //FirebaseHelper.getRoot().child("posts").removeEventListener(mPostListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onAttach(Activity context) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POST_COUNT, mPostCount);
        outState.putString(START_KEY,mStartKey);
        outState.putSerializable(POSTS,mPosts);
    }

    public void init(View view) {
        mHomePostBtn = (FloatingActionButton) view.findViewById(R.id.home_post_add);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_posts_recyclerview);
    }

    public void setupRecyclerView(ArrayList<Post> posts) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PostAdapter(posts);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //see notebook for guide
    class PostPopulateTask extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... params) {

            Post post = params[0].getValue(Post.class);
            post.setPostId(params[0].getKey());//set post push key
            if ( /*isFriend(post.getPosterId())*/ true) {
                mPosts.add(post);
            } else {
                if ((mPostCount % mLimit) == 0) {//if 8 posts is added then add the listener again
                    mPostRef.removeEventListener(mPostListener);//remove the listener
                    if (mUserId == null) {//whether posts of only given user should be shown or everyone
                        mPostRef.startAt(null, mStartKey).orderByChild("timestamp").limitToFirst(mLimit)
                                .addChildEventListener(mPostListener);
                    } else {
                        mPostRef.startAt(null, mStartKey).equalTo("poster_id", mUserId).orderByChild("timestamp").limitToFirst(mLimit)
                                .addChildEventListener(mPostListener);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            mAdapter.notifyItemInserted(mPosts.size() - 1);
        }
    }
}
