package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Stranger on 11/14/2015.
 */
public class RobotoTextView extends TextView {
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
            Typeface typeface=   Typeface.createFromAsset(context.getAssets(),"font/Roboto-Light.ttf");
            setTypeface(typeface);
    }
}
