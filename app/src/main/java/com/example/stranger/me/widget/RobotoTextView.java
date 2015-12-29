package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.stranger.me.typeface.Roboto;

/**
 * Created by Stranger on 11/14/2015.
 */
public class RobotoTextView extends TextView {
    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String textStyle = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle");
        Typeface typeface = Roboto.getFont(context, textStyle);
        setTypeface(typeface);
    }
}
