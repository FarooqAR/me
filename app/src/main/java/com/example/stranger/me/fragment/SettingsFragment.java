package com.example.stranger.me.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.AvatarActivity;
import com.example.stranger.me.helper.CloudinaryHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.CircleImageView;
import com.example.stranger.me.widget.RobotoEditText;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;


public class SettingsFragment extends Fragment {
    public static final int REQUEST_AVATAR = 200;
    private static final String TAG = "SettingsFragment";
    private Button mChangePassBtn;
    private Button mChangeEmailBtn;
    private Button mChangeProfileImageBtn;
    private RobotoEditText mChangeFirstName;
    private RobotoEditText mChangeLastName;
    private RobotoEditText mChangeAge;
    private RobotoEditText mChangeDescription;
    private ProgressBar mChangePassProgress;
    private ProgressBar mChangeEmailProgress;
    private ProgressBar mChangeProfileImageProgress;
    private AutoCompleteTextView mChangeCountry;
    private ScrollView mRootView;
    private CardView mEmailContainer;
    private CardView mPasswordContainer;
    private CircleImageView mChangeProfileImage;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener mChangePassListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openPasswordDialog();
        }
    };
    private View.OnClickListener mChangeEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openEmailDialog();
        }
    };
    private View.OnFocusChangeListener mChangeFirstNameListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String firstName = String.valueOf(mChangeFirstName.getText());
            if (!hasFocus) {
                changeFirstName(firstName);
            }
        }
    };
    private View.OnFocusChangeListener mChangeLastNameListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String lastName = String.valueOf(mChangeLastName.getText());
            if (!hasFocus) {
                changeLastName(lastName);
            }
        }
    };
    private View.OnFocusChangeListener mChangeCountryListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String country = String.valueOf(mChangeCountry.getText());
            if (!hasFocus) {
                changeCountry(country);
            }
        }
    };
    private View.OnFocusChangeListener mChangeAgeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String age = String.valueOf(mChangeAge.getText());
            if (!hasFocus) {
                changeAge(Integer.parseInt(age));
            }
        }
    };
    private ValueEventListener mCurrentUserListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            new SettingsTask().execute(dataSnapshot);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
    private View.OnFocusChangeListener mChangeDescriptionListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String description = String.valueOf(mChangeDescription.getText());
            if (!hasFocus) {
                changeDescription(description);
            }
        }
    };
    private View.OnClickListener mChangeProfileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            disableChangeImageBtn();
            openImageDialog();
        }
    };

    private void changeDescription(String about) {
        if (!about.equals("") && about.length() > 4 && about.length() < 21) {
            if (about.matches("[a-zA-Z ]+")) {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("about").setValue(about);
            } else {
                SnackbarHelper.create(mRootView, "Description can contain only letters and space").show();
            }
        } else {
            SnackbarHelper.create(mRootView, "Description can have 5-20 letters").show();
        }
    }

    public SettingsFragment() {
        // Required empty public constructor

    }


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        if (FirebaseHelper.getRoot().getAuth() != null &&
                (FirebaseHelper.getRoot().getAuth().getProvider().equals("facebook") ||
                        FirebaseHelper.getRoot().getAuth().getProvider().equals("google"))) {
            mEmailContainer.setVisibility(View.GONE);
            mPasswordContainer.setVisibility(View.GONE);
        }
        mChangeEmailBtn.setOnClickListener(mChangeEmailListener);
        mChangePassBtn.setOnClickListener(mChangePassListener);
        mChangeProfileImageBtn.setOnClickListener(mChangeProfileImageListener);

        mChangeFirstName.setOnFocusChangeListener(mChangeFirstNameListener);
        mChangeLastName.setOnFocusChangeListener(mChangeLastNameListener);
        mChangeCountry.setOnFocusChangeListener(mChangeCountryListener);
        mChangeAge.setOnFocusChangeListener(mChangeAgeListener);
        mChangeDescription.setOnFocusChangeListener(mChangeDescriptionListener);

        new AutoCompleteCountriesTask().execute();
        FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).addValueEventListener(mCurrentUserListener);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init(View view) {
        mChangeEmailBtn = (Button) view.findViewById(R.id.action_change_email);
        mChangePassBtn = (Button) view.findViewById(R.id.action_password_change);
        mChangeProfileImageBtn = (Button) view.findViewById(R.id.action_change_profile_image);
        mChangeFirstName = (RobotoEditText) view.findViewById(R.id.settings_first_name);
        mChangeLastName = (RobotoEditText) view.findViewById(R.id.settings_last_name);
        mChangeCountry = (AutoCompleteTextView) view.findViewById(R.id.settings_country);
        mChangeAge = (RobotoEditText) view.findViewById(R.id.settings_age);
        mChangeDescription = (RobotoEditText) view.findViewById(R.id.settings_about);
        mChangeEmailProgress = (ProgressBar) view.findViewById(R.id.change_email_progress);
        mChangePassProgress = (ProgressBar) view.findViewById(R.id.change_password_progress);
        mChangeProfileImageProgress = (ProgressBar) view.findViewById(R.id.change_profile_image_progress);
        mEmailContainer = (CardView) view.findViewById(R.id.settings_email_container);
        mPasswordContainer = (CardView) view.findViewById(R.id.settings_password_container);
        mChangeProfileImage = (CircleImageView) view.findViewById(R.id.settings_image);
        mRootView = (ScrollView) view.findViewById(R.id.fragment_settings_container);
    }

    private void disableEmailBtn() {
        mChangeEmailBtn.setEnabled(false);
        mChangeEmailBtn.setText("");
        mChangeEmailProgress.setVisibility(View.VISIBLE);
    }

    private void enableEmailBtn() {
        mChangeEmailBtn.setEnabled(true);
        mChangeEmailBtn.setText("Change");
        mChangeEmailProgress.setVisibility(View.GONE);
    }

    private void disablePassBtn() {
        mChangePassBtn.setEnabled(false);
        mChangePassBtn.setText("");
        mChangePassProgress.setVisibility(View.VISIBLE);
    }

    private void enablePassBtn() {
        mChangePassBtn.setEnabled(true);
        mChangePassBtn.setText("Change");
        mChangePassProgress.setVisibility(View.GONE);
    }

    private void disableChangeImageBtn() {
        mChangeProfileImageBtn.setEnabled(false);
        mChangeProfileImageBtn.setText("");
        mChangeProfileImageProgress.setVisibility(View.VISIBLE);
    }

    private void enableChangeImageBtn() {
        mChangeProfileImageBtn.setEnabled(true);
        mChangeProfileImageBtn.setText("Change");
        mChangeProfileImageProgress.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void changeFirstName(String firstName) {
        if (!firstName.equals("")) {
            if (firstName.length() > 3) {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("firstName").setValue(firstName);
            } else {
                SnackbarHelper.create(mRootView, "Minimum 4 letters allowed").show();
            }
        }
    }

    private void changeLastName(String lastName) {
        if (!lastName.equals("")) {
            if (lastName.length() > 3) {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("lastName").setValue(lastName);
            } else {
                SnackbarHelper.create(mRootView, "Minimum 4 letters allowed").show();
            }
        }
    }

    private void changeAge(int age) {
        if (age > 0) {
            if (age >= 5 && age <= 100) {
                FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("age").setValue(age);
            } else {
                SnackbarHelper.create(mRootView, "Age Range 5-100 years").show();
            }
        }
    }

    private void changeCountry(String country) {
        if (!country.equals("")) {
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("country").setValue(country);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(512, 512).start(getActivity());
    }


    private void openEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_email_dialog, null, false);
        builder.setTitle("Change Your Email")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText old_email = (EditText) view.findViewById(R.id.old_email);
                        EditText new_email = (EditText) view.findViewById(R.id.new_email);
                        EditText password = (EditText) view.findViewById(R.id.password_for_change_email);
                        String oldEmail = String.valueOf(old_email.getText());
                        String newEmail = String.valueOf(new_email.getText());
                        String pass = String.valueOf(password.getText());
                        changeEmail(oldEmail, newEmail, pass);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.change_password_dialog, null, false);
        builder.setTitle("Change Your Email")
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText email = (EditText) view.findViewById(R.id.email_for_change_password);
                        EditText old_pass = (EditText) view.findViewById(R.id.old_password);
                        EditText new_pass = (EditText) view.findViewById(R.id.new_password);
                        String _email = String.valueOf(email.getText());
                        String oldPass = String.valueOf(old_pass.getText());
                        String newPass = String.valueOf(new_pass.getText());
                        changePassword(_email, oldPass, newPass);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openImageDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Your Profile Image")
                .setPositiveButton("Pick", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Crop.pickImage(getActivity());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Choose Avatar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), AvatarActivity.class);
                        startActivityForResult(i, REQUEST_AVATAR);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        enableChangeImageBtn();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changePassword(String email, String old_pass, String newPass) {
        disablePassBtn();
        FirebaseHelper.getRoot().changePassword(email, old_pass, newPass, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                enablePassBtn();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                enablePassBtn();
            }
        });
    }

    private void changeEmail(String oldEmail, String newEmail, String pass) {
        disableEmailBtn();
        FirebaseHelper.getRoot().changeEmail(oldEmail, pass, newEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                enableEmailBtn();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                enablePassBtn();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //this task will get user data and update ui, it is executed everytime the user data changes
    private class SettingsTask extends AsyncTask<DataSnapshot, Void, User> {
        @Override
        protected User doInBackground(DataSnapshot... params) {
            User user = params[0].getValue(User.class);
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user!=null) {//user can be null if someone creates an account and close the app without going to set firstname ....
                if (user.getFirstName() != null)
                    mChangeFirstName.setText(user.getFirstName());
                if (user.getLastName() != null)
                    mChangeLastName.setText(user.getLastName());
                if (user.getCountry() != null)
                    mChangeCountry.setText(user.getCountry());
                if (user.getAge() != 0)
                    mChangeAge.setText(String.valueOf(user.getAge()));
                if (user.getAbout() != null) {
                    mChangeDescription.setText(user.getAbout());
                }
                if (user.getProfileImageURL() != null) {
                    Picasso.with(getActivity()).load(user.getProfileImageURL()).into(mChangeProfileImage);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AVATAR && resultCode == Activity.RESULT_OK) {
            String avatar_link = data.getStringExtra(AvatarActivity.AVATAR_LINKS);
            Picasso.with(getActivity().getApplicationContext())
                    .load(avatar_link).into(mChangeProfileImage);
            changeProfileImage(avatar_link);
        }
        //handle crop
        else if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            //the image has been cropped and ready to upload
            new ImageUploadTask().execute();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            enableChangeImageBtn();
        }
    }

    private void changeProfileImage(final String link) {
        FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("profileImageURL").setValue(link, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                enableChangeImageBtn();
                Picasso.with(getActivity()).load(link).into(mChangeProfileImage);
            }
        });
    }

    public class AutoCompleteCountriesTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Locale[] locales = Locale.getAvailableLocales();
            ArrayList<String> countries = new ArrayList<String>();
            for (Locale locale : locales) {
                String country = locale.getDisplayCountry();
                if (country.trim().length() > 0 && !countries.contains(country)) {
                    countries.add(country);
                }
            }
            Collections.sort(countries);
            return countries;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.autocomplete_item, strings);
            mChangeCountry.setAdapter(adapter);
        }
    }

    public class ImageUploadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = null;
            try {
                File file = new File(getActivity().getCacheDir(), "cropped");
                Map uploadResult = CloudinaryHelper.getInstance().uploader().upload(file, ObjectUtils.emptyMap());
                /*
                uploadResult contains following keys
                public_id,version,signature,height,width,format,resource_type,created_at,bytes,type,url,secure_url,etag
                */
                url = (String) uploadResult.get("secure_url");

                url = CloudinaryHelper.getInstance().url().transformation(new Transformation().width(144).height(144)).generate(String.valueOf(uploadResult.get("public_id")));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            changeProfileImage(s);
        }
    }

}
