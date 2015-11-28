package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Stranger on 11/15/2015.
 */
public class RobotoEditText extends EditText {
    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface roboto= Typeface.createFromAsset(context.getAssets(),"font/Roboto-Light.ttf");
        setTypeface(roboto);
    }
}
