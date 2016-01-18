package com.example.stranger.me.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by Farooq on 1/17/2016.
 */
public class SharedPreferenceHelper {
    public static SharedPreferences PREFERENCES= null;
    public static SharedPreferences.Editor EDITOR = null;
    public static SharedPreferenceHelper HELPER = null;
    public static Activity CONTEXT;

    public static void setContext(Activity CONTEXT) {
        SharedPreferenceHelper.CONTEXT = CONTEXT;
    }

    public static SharedPreferenceHelper getInstance(){
        if(PREFERENCES == null){
            HELPER = new SharedPreferenceHelper();
            PREFERENCES =CONTEXT.getPreferences(Context.MODE_PRIVATE);
            EDITOR = PREFERENCES.edit();
            EDITOR.apply();
        }
        return HELPER;
    }
    public static SharedPreferences getPreferences(){
        return PREFERENCES;
    }
    public static SharedPreferences.Editor getEditor(){
        return EDITOR;
    }
    public String getString(String key){
        String value = null;
        try {
            value = new StringGetTask().execute(key).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return value;
    }
    public int getInt(String key){
        int value = 0;
        try {
            value = new IntGetTask().execute(key).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return  value;
    }
    public void putString(String key,String value){
        new StringAddTask().execute(key,value);
    }
    public void putInt(final String key, final int value){
        new Runnable(){
            @Override
            public void run() {
                getEditor().putInt(key,value);
                getEditor().apply();
            }
        }.run();
    }
    private class StringAddTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            String key = params[0];
            String value = params[1];
            getEditor().putString(key, value);
            getEditor().apply();
            return null;
        }
    }

    private class StringGetTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            return getPreferences().getString(params[0],null);
        }
    }

    private class IntGetTask extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... params) {

            return getPreferences().getInt(params[0],0);
        }
    }
}
