package com.example.stranger.me.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.stranger.me.typeface.Roboto;

/**
 * Created by Stranger on 11/15/2015.
 */
public class RobotoEditText extends EditText {
    Typeface roboto;
    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        String textStyle = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textStyle");
        roboto = Roboto.getFont(context,textStyle);
        setTypeface(roboto);
    }
}
