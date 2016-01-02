package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stranger.me.R;


public class AllMusicFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddMusicFab;
    public AllMusicFragment() {
        // Required empty public constructor
    }


    public static AllMusicFragment newInstance() {
        AllMusicFragment fragment = new AllMusicFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_music, container, false);
        init(view);
        return view;
    }
    public void init(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.all_music_recyclerview);
        mAddMusicFab = (FloatingActionButton) view.findViewById(R.id.action_add_music_fab);
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
