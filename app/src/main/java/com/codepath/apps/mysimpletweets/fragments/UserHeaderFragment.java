package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserHeaderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserHeaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserHeaderFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    TwitterClient client;
    User user;
    View v;


//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserHeaderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserHeaderFragment newInstance(User userInfo) {
        UserHeaderFragment fragment = new UserHeaderFragment();
        Bundle args = new Bundle();
//        args.putParcelable("userInfo",userInfo );

        fragment.setArguments(args);
        return fragment;
    }

    public UserHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_user_header, container, false);
        return v;
    }


    public void addUserInfo(User user) {
        this.user = user;


        ImageView ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        TextView tvFullName = (TextView) v.findViewById(R.id.tvFullName);
        TextView tvTagline = (TextView) v.findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) v.findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) v.findViewById(R.id.tvFollowing);

        tvFullName.setText(user.getFullName());
        String ProfileImageUrl = user.getProfileImageUrl();
        if (ProfileImageUrl != null)
            Picasso.with(getActivity()).load(ProfileImageUrl).into(ivProfileImage);

        tvFollowers.setText(user.getFollowersCount() + " " + getString(R.string.label_followers));
        tvFollowing.setText(user.getFollowingCount() + " " + getString(R.string.label_following));
        tvTagline.setText(user.getTagLine());

    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        if (activity instanceof OnFragmentUserInfoAvailableListener)
//            mListener = (OnFragmentUserInfoAvailableListener) activity;
//        else {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentUserInfoAvailableListener {
//        // TODO: Update argument type and name
//        public void onFragmentUserProfileInfoAvailable(User user);
//    }

}
