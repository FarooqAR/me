package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Farooq on 12/29/2015.
 */
public class RobotoTextViewBold extends TextView{
    public RobotoTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface=   Typeface.createFromAsset(context.getAssets(),"font/Roboto-Bold.ttf");
        setTypeface(typeface);
    }
}
