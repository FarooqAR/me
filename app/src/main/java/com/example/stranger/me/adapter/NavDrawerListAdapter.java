package com.example.stranger.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stranger.me.R;
import com.example.stranger.me.modal.NavDrawerListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farooq on 12/29/2015.
 */
public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerListItem> {

    private ArrayList<NavDrawerListItem> items;
    private Context context;

    public NavDrawerListAdapter(Context context, int resource, List<NavDrawerListItem> objects) {
        super(context, resource, objects);
        items = (ArrayList<NavDrawerListItem>) objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public NavDrawerListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nav_drawer_listitem, parent, false);

            viewHolder.text = (TextView) convertView.findViewById(R.id.nav_drawer_item_text);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.nav_drawer_item_icon);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NavDrawerListItem item = getItem(position);
        viewHolder.text.setText(item.getmText());
        viewHolder.icon.setImageResource(item.getmId());

        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }
}
