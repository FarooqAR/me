package com.example.stranger.me.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen3 extends Fragment implements SignUpFragmentMain.SignUpPagerChangeListener{
    private static final String ABOUT = "about_text";
    private static final String LOCATION = "location_text";
    private RobotoEditText mAbout;
    private AutoCompleteTextView mLocation;
    private RelativeLayout mRootView;
    private SignUpFragmentMain mMainFragment;
    private String mAboutText;
    private String mLocationText;

    public SignUpFragmentScreen3() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen3 newInstance(){
        SignUpFragmentScreen3 fragment = new SignUpFragmentScreen3();

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ABOUT, String.valueOf(mAbout.getText()));
        outState.putString(LOCATION, String.valueOf(mLocation.getText()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.sign_up_fragment_screen3, container, false);
        init(view);
        if(savedInstanceState !=null){
            mAboutText = savedInstanceState.getString(ABOUT);
            mLocationText = savedInstanceState.getString(LOCATION);
        }
        mMainFragment = (SignUpFragmentMain) getParentFragment();
        new AutoCompleteCountriesTask().execute();
        return view;
    }
    public void init(View view){
        mAbout = (RobotoEditText) view.findViewById(R.id.sign_up_about);
        mLocation = (AutoCompleteTextView) view.findViewById(R.id.sign_up_location);
        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
    }
    private boolean isDataValid(View v,String about,String location){
        if(about.equals("") || location.equals("")){
            SnackbarHelper.create(mRootView,"Fields cant be left empty").show();
        }
        else{
            if(about.length()>4 && about.length()<21) {
                if(about.matches("[a-zA-Z ]+")) {
                    return true;
                }
                else{
                    SnackbarHelper.create(mRootView,"Special characters are not allowed").show();
                }
            }
            else{
                SnackbarHelper.create(mRootView,"Tell about yourself in 5-20 letters").show();
            }
        }
        return false;
    }
    @Override
    public void onChange(int position) {

    }

    @Override
    public void onNextButtonClick() {
        String about;
        String location;
        if(mAbout != null) {
            about = String.valueOf(mAbout.getText());
            location = String.valueOf(mLocation.getText());
        }
        else{//get strings when views become null on rotation
            about = mAboutText;
            location = mLocationText;
        }
        if(mMainFragment == null) {//it will be null on rotation
            mMainFragment = (SignUpFragmentMain) getParentFragment();
        }
        if(isDataValid(mAbout,about,location)){
            mMainFragment.disableButtons();
            new Screen3DataPopulateTask().execute(about,location);
        }
    }
    public class Screen3DataPopulateTask extends AsyncTask<String,Void,Map<String,Object>>{

        @Override
        protected Map<String, Object> doInBackground(String... params) {

            String about = params[0];
            String location = params[1];
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("about",about);
            data.put("country",location);

            return data;
        }

        @Override
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).updateChildren(stringObjectMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if(firebaseError == null){
                        mMainFragment.setViewPagerItem(3);//move to next screen on success
                    }
                    else{
                        SnackbarHelper.create(mRootView,firebaseError.getMessage()).show();
                    }
                   mMainFragment.enableButtons();
                }
            });
        }
    }
    public class AutoCompleteCountriesTask extends AsyncTask<Void,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Locale[] locales = Locale.getAvailableLocales();
            ArrayList<String> countries = new ArrayList<String>();
            for (Locale locale : locales) {
                String country = locale.getDisplayCountry();
                if (country.trim().length()>0 && !countries.contains(country)) {
                    countries.add(country);
                }
            }
            Collections.sort(countries);
            return countries;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.autocomplete_item,strings);
            mLocation.setAdapter(adapter);
        }
    }
}
