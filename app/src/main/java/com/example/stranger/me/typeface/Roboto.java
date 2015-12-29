package com.example.stranger.me.typeface;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Farooq on 12/29/2015.
 */
public class Roboto {
    private static Typeface robotoLight;
    private static Typeface robotoMedium;
    private static Typeface robotoBold;

    public static Typeface getFont(Context context, String style) {
        if (style != null) {
            switch (style) {
                case "normal":
                    if (robotoMedium == null)
                        robotoMedium = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Medium.ttf");
                    return robotoMedium;
                case "bold":
                    if (robotoBold == null)
                        robotoBold = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Bold.ttf");
                    return robotoBold;
                default:
                    if (robotoLight == null)
                        robotoLight = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf");
                    return robotoLight;
            }
        }
        else {
            if (robotoLight == null)
                robotoLight = Typeface.createFromAsset(context.getAssets(), "font/Roboto-Light.ttf");
            return robotoLight;
        }
    }

}
