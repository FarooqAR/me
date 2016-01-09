package com.example.stranger.me.helper;

import android.view.View;

/**
 * Created by Farooq on 1/4/2016.
 */
public class InputHelper {

    public static boolean isEmailValid(View v, CharSequence email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if(!isValid){
            SnackbarHelper.create(v,"Email is not valid");
        }
        return isValid;
    }
    public static boolean isPasswordValid(View v,String pass){
        if(pass.length()<6){
            SnackbarHelper.create(v, "Invalid Password: Minimum 6 characters allowed");
            return false;
        } else if(pass.length()>16){
            SnackbarHelper.create(v,"Invalid Password: Maximum 16 characters allowed");
            return false;
        } else if (!(pass.matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$"))){
            SnackbarHelper.create(v, "Invalid Password: Must contain alphabets & numbers");
            return false;
        }
        return true;
    }
}
