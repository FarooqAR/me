package com.example.stranger.me;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Farooq on 1/19/2016.
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager (Context context) {
        super (context);
    }

    public CustomLinearLayoutManager (Context context, int orientation, boolean reverseLayout) {
        super (context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren (RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren (recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace ();
        }
    }
}
