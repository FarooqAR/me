package com.example.stranger.me.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cloudinary.utils.ObjectUtils;
import com.example.stranger.me.R;
import com.example.stranger.me.activity.AvatarActivity;
import com.example.stranger.me.activity.HomeActivity;
import com.example.stranger.me.helper.CloudinaryHelper;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.helper.SnackbarHelper;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragmentScreen4 extends Fragment implements SignUpFragmentMain.SignUpPagerChangeListener {

    public static final int REQUEST_AVATAR = 99;
    public static final int REQUEST_IMAGE_CAPTURE = 80;
    public static final int RESULT_BROWSE_IMAGE = 70;
    private static final String TAG = "SignUpFragmentScreen4";
    private Button mAvatarBtn;
    private Button mCaptureBtn;
    private RelativeLayout mRootView;

    private SignUpFragmentMain mMainFragment;
    private ImageView mProfilePic;
    private String mProfilePicLink;
    private Uri mCurrentPhotoPath;//the cached path to cropped image

    public SignUpFragmentScreen4() {
        // Required empty public constructor
    }

    public static SignUpFragmentScreen4 newInstance() {
        SignUpFragmentScreen4 fragment = new SignUpFragmentScreen4();

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

        View view = inflater.inflate(R.layout.sign_up_fragment_screen4, container, false);
        init(view);

       mMainFragment = (SignUpFragmentMain) getParentFragment();
        mAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AvatarActivity.class);
                getParentFragment().startActivityForResult(i, REQUEST_AVATAR);
            }
        });
        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity());
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void init(View view) {
        mAvatarBtn = (Button) view.findViewById(R.id.sign_up_select_avatar);
        mProfilePic = (ImageView) view.findViewById(R.id.sign_up_profile_pic);
        mCaptureBtn = (Button) view.findViewById(R.id.sign_up_capture_image);
        mRootView = (RelativeLayout) view.findViewById(R.id.rootView);
    }

    private void disableButtons() {
        mAvatarBtn.setEnabled(false);
        mCaptureBtn.setEnabled(false);
    }

    private void enableButtons() {
        mAvatarBtn.setEnabled(true);
        mCaptureBtn.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AVATAR && resultCode == Activity.RESULT_OK) {
            String avatar_link = data.getStringExtra(AvatarActivity.AVATAR_LINKS);
            mProfilePicLink = avatar_link;
            if (mProfilePic != null) // it was keep returning null on rotation and couldn't find the workaround
                Picasso.with(getActivity().getApplicationContext())
                        .load(avatar_link).into(mProfilePic);
            mCurrentPhotoPath = null;
        }

        //handle crop
        else if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
            beginCrop(data.getData());

        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);

            mProfilePicLink = null;

        }
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().withMaxSize(512, 512).start(getActivity());
        mCurrentPhotoPath = destination;
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            mProfilePic.setImageURI(Crop.getOutput(result));

        } else if (resultCode == Crop.RESULT_ERROR) {
            SnackbarHelper.create(mRootView, Crop.getError(result).getMessage()).show();
        }
    }
    @Override
    public void onChange(int position) {

    }

    @Override
    public void onNextButtonClick() {
        if(mMainFragment == null)
            mMainFragment = (SignUpFragmentMain) getParentFragment();
        mMainFragment.disableButtons();
        disableButtons();
        if (mProfilePicLink != null && mCurrentPhotoPath == null) {
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("profileImageURL").setValue(mProfilePicLink, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        startActivity(i);
                        getActivity().finish();

                        Log.d(TAG,"Avatar selected for profile, url="+mProfilePicLink);
                    } else {
                        SnackbarHelper.create(mAvatarBtn, firebaseError.getMessage()).show();
                    }
                    mMainFragment.enableButtons();
                    enableButtons();
                }
            });
        } else if (mCurrentPhotoPath != null && mProfilePicLink == null) {
            new ImageUploadTask().execute();
        } else if (mCurrentPhotoPath == null && mProfilePicLink == null) {
            SnackbarHelper.create(mRootView, "Please select a photo").show();
            mMainFragment.enableButtons();
            enableButtons();
        }

    }

    public class ImageUploadTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String url = null;
            try {
                File  file = new File(getActivity().getCacheDir(),"cropped");


                Map uploadResult = CloudinaryHelper.getInstance().uploader().upload(file, ObjectUtils.emptyMap());
                /*
                uploadResult contains following keys
                public_id,version,signature,height,width,format,resource_type,created_at,bytes,type,url,secure_url,etag
                */
                url = (String) uploadResult.get("secure_url");
                Log.d(TAG,"Profile Image Uploaded, url="+url);
                //set current photo url so that picasso can load that on rotation
                mProfilePicLink = url;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            FirebaseHelper.getRoot().child("users").child(FirebaseHelper.getAuthId()).child("profileImageURL").setValue(s, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError == null) {
                        Intent i = new Intent(getActivity(), HomeActivity.class);
                        //startActivity(i);
                    } else {
                        SnackbarHelper.create(mRootView, firebaseError.getMessage()).show();
                    }
                    mMainFragment.enableButtons();
                    enableButtons();
                }
            });
        }
    }

}
