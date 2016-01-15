package com.example.stranger.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.stranger.me.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Farooq on 1/9/2016.
 */
public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder>{
    private ArrayList<String> data;
    private Context context;
    private onItemClickListener mListener;
    public AvatarAdapter(Context context,ArrayList<String> data) {
        this.data = data;
        this.context = context;
        mListener = (onItemClickListener) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context).load(data.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   mListener.onItemClick(getAdapterPosition());
                }
            });
        }


    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }
}
