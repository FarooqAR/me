package com.example.stranger.me.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
    private static final String AGE = "age";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private RobotoEditText mSignUpFirstName;
    private RobotoEditText mSignUpLastName;
    private RobotoEditText mSignUpDob;
    private RelativeLayout mRootView;
    private int age = 0;
    private SignUpFragmentMain mMainFragment;
    private String mFirstName;
    private String mLastName;

    public SignUpFragmentScreen2() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen2 newInstance() {
        SignUpFragmentScreen2 fragment = new SignUpFragmentScreen2();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (savedInstanceState != null) {
            age = savedInstanceState.getInt(AGE);
            mFirstName = savedInstanceState.getString(FIRST_NAME);
            mLastName = savedInstanceState.getString(LAST_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.sign_up_fragment_screen2, container, false);
        init(view);

        mMainFragment = (SignUpFragmentMain) getParentFragment();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        String ageText = String.valueOf(mSignUpDob.getText());
        ageText = (ageText.equals("")) ? "0" : ageText;
        outState.putInt(AGE, Integer.parseInt(ageText));
        outState.putString(FIRST_NAME, String.valueOf(mSignUpFirstName.getText()));
        outState.putString(LAST_NAME, String.valueOf(mSignUpLastName.getText()));
    }


    public void init(View view) {
        mSignUpFirstName = (RobotoEditText) view.findViewById(R.id.sign_up_firstname);
        mSignUpLastName = (RobotoEditText) view.findViewById(R.id.sign_up_lastname);
        mSignUpDob = (RobotoEditText) view.findViewById(R.id.sign_up_dob);
        mRootView = (RelativeLayout) view.findViewById(R.id.root_view);
    }

    private boolean isValidData(View view, String firstName, String lastName, int age) {
        if (firstName != null && !firstName.equals("") &&
                lastName != null && !lastName.equals("")
                && age > 0) {
            if (firstName.length() > 3) {
                if (lastName.length() > 3) {
                    if (age >= 5 && age <= 100) {
                        return true;
                    } else {
                        SnackbarHelper.create(mRootView, "Age range allowed is 5-100 years").show();
                    }
                } else {
                    SnackbarHelper.create(mRootView, "Last name must have minimum 4 letters").show();
                }
            } else {
                SnackbarHelper.create(mRootView, "First name must have minimum 4 letters").show();
            }
        } else {
            if (firstName == null || firstName.equals("")) {
                SnackbarHelper.create(mRootView, "First name can't be empty").show();
            } else if (lastName == null || lastName.equals("")) {
                SnackbarHelper.create(mRootView, "Last name can't be empty").show();
            } else {
                SnackbarHelper.create(mRootView, "Invalid age").show();
            }
        }
        return false;
    }


    @Override
    public void onChange(int position) {

    }

    @Override
    public void onNextButtonClick() {
        Log.d(TAG,getView().toString());
        String firstname = null;
        String lastname = null;
        String age = null;
        if (mMainFragment == null) {//it will null if screen is rotated
            mMainFragment = (SignUpFragmentMain) getParentFragment();
        }
        if (mSignUpLastName != null) {
            firstname = String.valueOf(mSignUpFirstName.getText());
            lastname = String.valueOf(mSignUpLastName.getText());
        } else {//that means screen is rotated and views are no longer available
            firstname = mFirstName;
            lastname = mLastName;
        }
        if (mSignUpDob != null) {
            age = String.valueOf(mSignUpDob.getText());
            if (!age.equals(""))
                this.age = Integer.parseInt(age);
        }


        if (isValidData(mSignUpFirstName, firstname, lastname, this.age)) {
            mMainFragment.disableButtons();
            new PopulateDataTask().execute(firstname, lastname);//age is instance variable so can be accessed in task
        } else {
            mMainFragment.enableButtons();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    public class PopulateDataTask extends AsyncTask<String, Void, Map<String, Object>> {

        @Override
        protected Map<String, Object> doInBackground(String... params) {

            String firstname = params[0];
            String lastname = params[1];
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("firstName", firstname);
            data.put("lastName", lastname);
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
                        SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                    } else {
                        mMainFragment.setViewPagerItem(2);

                    }
                    mMainFragment.enableButtons();
                }
            });
        }
    }

}
