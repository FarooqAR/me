package com.example.stranger.me.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stranger.me.R;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen2 extends Fragment implements SignUpFragmentMain.SignUpPagerChangeListener {

    private static final String TAG = "SignUpFragmentScreen2";
    private RobotoEditText mSignUpFirstName;
    private RobotoEditText mSignUpLastName;
    public RobotoEditText mSignUpDob;
    private Button mSignUpDatePickBtn;
    private ViewPager mSignUpMainViewPager;//reference to main viewpager
    private Button mNextBtn;//reference to next button in main fragment;
    private int age = 0;

    public SignUpFragmentScreen2() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen2 newInstance() {
        SignUpFragmentScreen2 fragment = new SignUpFragmentScreen2();

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sign_up_fragment_screen2, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        mSignUpFirstName = (RobotoEditText) view.findViewById(R.id.sign_up_firstname);
        mSignUpLastName = (RobotoEditText) view.findViewById(R.id.sign_up_lastname);
        mSignUpDob = (RobotoEditText) view.findViewById(R.id.sign_up_dob);

    }

    private boolean isValidData(View view, String firstName, String lastName, int age) {
        if (!firstName.equals("") && !lastName.equals("") && age > 0) {
            if (firstName.length() > 3) {
                if (lastName.length() > 3) {
                    if (age >= 5) {
                        return true;
                    } else {
                        SnackbarHelper.create(view, "Minimum age allowed is 5");
                    }
                } else {
                    SnackbarHelper.create(view, "Last name must have minimum 4 letters");
                }
            } else {
                SnackbarHelper.create(view, "First name must have minimum 4 letters");
            }
        } else {
            if (firstName.equals("")) {
                SnackbarHelper.create(view, "First name can't be empty");
            } else if (lastName.equals("")) {
                SnackbarHelper.create(view, "Last name can't be empty");
            } else {
                SnackbarHelper.create(view, "Invalid age");
            }
        }
        return false;
    }


    @Override
    public void onChange(int position) {

    }

    @Override
    public void onNextButtonClick(final ViewPager viewPager,Button nextBtn) {
        mSignUpMainViewPager = viewPager;
        mNextBtn = nextBtn;

        if (viewPager.getCurrentItem() == 1) { //means its screen 2
            String firstname = String.valueOf(mSignUpFirstName.getText());
            String lastname = String.valueOf(mSignUpLastName.getText());
            String age = String.valueOf(mSignUpDob.getText());
            if(!age.equals("")){
                this.age = Integer.parseInt(age);
            }
            if (isValidData(mSignUpFirstName, firstname, lastname, this.age)) {
                mNextBtn.setEnabled(false);
                new PopulateDataTask().execute(firstname,lastname);//age is instance variable so can be accessed in task
            }
            else{
                mNextBtn.setEnabled(true);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSignUpMainViewPager = null;
    }

    public class PopulateDataTask extends AsyncTask<String,Void,Map<String,Object>>{

        @Override
        protected Map<String, Object> doInBackground(String... params) {

            String firstname  = params[0];
            String lastname = params[1];
            Map<String,Object> data = new HashMap<String,Object>();
            data.put("firstName",firstname);
            data.put("lastName",lastname);
            data.put("age", age);


            return data;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Map<String, Object> stringObjectMap) {
            super.onPostExecute(stringObjectMap);
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).updateChildren(stringObjectMap, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        SnackbarHelper.create(mSignUpFirstName, firebaseError.getMessage());
                    } else {
                        mSignUpMainViewPager.setCurrentItem(2);

                    }
                    mNextBtn.setEnabled(true);
                }
            });
        }
    }

}
