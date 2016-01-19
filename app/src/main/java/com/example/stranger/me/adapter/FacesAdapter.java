package com.example.stranger.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farooq on 1/18/2016.
 */
public class FacesAdapter extends ArrayAdapter<Integer> {
    private ArrayList<Integer> mFaces;
    public FacesAdapter(Context context, int resource, List<Integer> objects) {
        super(context, resource, objects);
        mFaces = (ArrayList<Integer>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
    static class ViewHolder{
        ImageView imageView;
    }
}
