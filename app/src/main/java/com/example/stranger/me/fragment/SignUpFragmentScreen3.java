package com.example.stranger.me.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
    private RobotoEditText mAbout;
    private AutoCompleteTextView mLocation;
    private Button mNextBtn;//reference to next button in main fragment
    private ViewPager mViewPager;//reference to viewpager in main fragment


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.sign_up_fragment_screen3, container, false);
        init(view);
        new AutoCompleteCountriesTask().execute();

        return view;
    }
    public void init(View view){
        mAbout = (RobotoEditText) view.findViewById(R.id.sign_up_about);
        mLocation = (AutoCompleteTextView) view.findViewById(R.id.sign_up_location);
    }
    private boolean isDataValid(View v,String about,String location){
        if(about.equals("") || location.equals("")){
            SnackbarHelper.create(v,"Fields cant be left empty");
        }
        else{
            if(about.length()>4 && about.length()<21) {
                return true;
            }
            else{
                SnackbarHelper.create(v,"Tell about yourself in 5-20 letters");
            }
        }
        return false;
    }
    @Override
    public void onChange(int position) {

    }

    @Override
    public void onNextButtonClick(ViewPager viewPager, Button nextBtn) {
        String about = String.valueOf(mAbout.getText());
        String location = String.valueOf(mLocation.getText());
        mViewPager = viewPager;
        mNextBtn  = nextBtn;
        if(isDataValid(mAbout,about,location)){
            mNextBtn.setEnabled(false);
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
                        mViewPager.setCurrentItem(3);//move to next screen on success
                    }
                    else{
                        SnackbarHelper.create(mAbout,firebaseError.getMessage());
                    }
                    mNextBtn.setEnabled(true);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,strings);
            mLocation.setAdapter(adapter);
        }
    }
}
