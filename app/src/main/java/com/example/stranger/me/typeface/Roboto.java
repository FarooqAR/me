package com.example.stranger.me.typeface;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Farooq on 12/29/2015.
 */
public class Roboto {
    private static Typeface roboto;

    public static Typeface getFont(Context context, String style) {
        roboto = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Regular.ttf");
        return roboto;
    }

}
