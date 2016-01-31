package com.example.stranger.me.helper;

import android.view.View;

/**
 * Created by Farooq on 1/4/2016.
 */
public class InputHelper {

    public static boolean isEmailValid(View v, CharSequence email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if(!isValid){
            SnackbarHelper.create(v,"Email is not valid").show();
        }
        return isValid;
    }
    public static boolean isPasswordValid(View v,String pass){
        if(pass.length()<6){
            SnackbarHelper.create(v, "Invalid Password: Minimum 6 characters allowed").show();
            return false;
        } else if(pass.length()>16){
            SnackbarHelper.create(v,"Invalid Password: Maximum 16 characters allowed").show();
            return false;
        }
        return true;
    }
    public static boolean isVaildEmailPassword(View v, String e, String p) {
        if (e.equals("") && p.equals("")) {
            SnackbarHelper.create(v, "Email & Password Required").show();
            return false;
        } else if (e.equals("")) {
            SnackbarHelper.create(v, "Email Required").show();
            return false;
        } else if (p.equals("")) {
            SnackbarHelper.create(v, "Password Required").show();
            return false;
        } else if (InputHelper.isEmailValid(v, e) && InputHelper.isPasswordValid(v, p)) {
            return true;

        }
        return false;
    }
}
