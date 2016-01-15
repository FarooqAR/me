package com.example.stranger.me.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.stranger.me.R;
import com.example.stranger.me.adapter.ContactListAdapter;
import com.example.stranger.me.helper.FirebaseHelper;
import com.example.stranger.me.modal.User;
import com.example.stranger.me.widget.RobotoEditText;

import java.util.ArrayList;
import java.util.Map;


public class FindContactFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private ArrayList<User> mUsers;
    private ContactListAdapter mAdapter;
    private RobotoEditText mFindContact;
    private ImageButton mFindContactBtn;
    private ProgressBar mProgress;

    public FindContactFragment() {
        // Required empty public constructor
    }


    public static FindContactFragment newInstance() {
        FindContactFragment fragment = new FindContactFragment();

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
        View view = inflater.inflate(R.layout.fragment_find_contact, container, false);

        init(view);
        mUsers = new ArrayList<User>();
        mAdapter = new ContactListAdapter(getActivity(), mUsers);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mFindContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(mFindContact.getText());

                mUsers = new ArrayList<User>();
                mAdapter = new ContactListAdapter(getActivity(), mUsers);
                mRecyclerView.setAdapter(mAdapter);

                if (text != null && !text.equals("")) {
                    mProgress.setVisibility(View.VISIBLE);
                    String[] textSplitted = text.split(" ");
                    if (textSplitted.length > 1) {
                        new ContactAddTask().execute(textSplitted[0], textSplitted[1]);
                    } else {
                        new ContactAddTask().execute(textSplitted[0], "?/");
                    }
                }
            }
        });
        return view;
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.find_contact_recyclerview);
        mFindContact = (RobotoEditText) view.findViewById(R.id.find_contact_edittext);
        mFindContactBtn = (ImageButton) view.findViewById(R.id.action_find_contact);
        mProgress = (ProgressBar) view.findViewById(R.id.find_contact_progress);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //find user and add it to recyclerview
    public class ContactAddTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... text) {
            String text1 = text[0].toLowerCase();
            String text2 = text[1].toLowerCase();
            Map<String, Object> map = (Map<String, Object>) FirebaseHelper.getUsers().getValue();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                User user = null;
                String user_id = entry.getKey();
                Map<String, Object> userData = (Map<String, Object>) entry.getValue();
                String first_name = (userData.get("firstName") != null) ? ((String) userData.get("firstName")) : "";
                String last_name = (userData.get("lastName") != null) ? ((String) userData.get("lastName")) : "";
                String country = (userData.get("country") != null) ? ((String) userData.get("country")) : "";
                String profileImage = (userData.get("profileImageURL") != null) ? ((String) userData.get("profileImageURL")) : "";

                if (first_name.toLowerCase().contains(text1) || last_name.toLowerCase().contains(text1) ||
                        first_name.toLowerCase().contains(text2) || last_name.toLowerCase().contains(text2)) {
                    user = new User(first_name, last_name, country, profileImage);
                    user.setId(user_id);
                    if(!user_id.equals(FirebaseHelper.getAuthId()))//add contacts other than authenticated one
                    mUsers.add(user);
                }
            }
            return mUsers.size();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mAdapter.notifyDataSetChanged();
            mProgress.setVisibility(View.GONE);
        }
    }

    public class ContactClearTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mUsers.clear();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }


}
