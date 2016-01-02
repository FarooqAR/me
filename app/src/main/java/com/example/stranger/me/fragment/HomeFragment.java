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
import com.example.stranger.me.modal.Post;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FloatingActionButton mHomePostBtn;// button to create posts
    private PostAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mAuthId;
    private String mStartKey; // index where querying data starts on firebase    must change on scroll
    private int mLimit;//limit for # of data to be queried      must change on scroll
    private ChildEventListener mPostListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Post post = dataSnapshot.getValue(Post.class);
            mStartKey = dataSnapshot.getKey();
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
            Snackbar.make(v,"Hey There",Snackbar.LENGTH_SHORT).show();
        }
    };
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           //set arguments if available
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        mHomePostBtn.setOnClickListener(mHomePostBtnListener);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        //set listeners for posts here
        //FirebaseHelper.getRoot().child("posts").addChildEventListener(mPostListener);
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
        mStartKey = null;
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



    public void init(View view){
        mHomePostBtn = (FloatingActionButton) view.findViewById(R.id.home_post_add);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_posts_recyclerview);
    }

    public void setupRecyclerView(ArrayList<Post> posts){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PostAdapter(posts);
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    //see notebook for guide
    class PostPopulateTask extends AsyncTask<Integer,Void,ArrayList<Post>>{
        @Override
        protected ArrayList<Post> doInBackground(Integer... params) {

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }



        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            setupRecyclerView(posts);
        }
    }
}
