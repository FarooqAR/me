package com.example.stranger.me.helper;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Farooq on 1/4/2016.
 */
public class SnackbarHelper {
    private static Snackbar snackbar;

    public static Snackbar create(View v, CharSequence text) {
        snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT);
        return snackbar;
    }
}
