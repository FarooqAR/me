package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Farooq on 12/29/2015.
 */
public class RobotoTextViewMedium extends TextView {

    public RobotoTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface=   Typeface.createFromAsset(context.getAssets(),"font/Roboto-Medium.ttf");
        setTypeface(typeface);
    }

}
