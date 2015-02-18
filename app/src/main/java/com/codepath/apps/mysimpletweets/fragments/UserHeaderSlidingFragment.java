package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TimelineActivity;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

/**
 * Created by alon on 2/17/15.
 */
public class UserHeaderSlidingFragment extends Fragment {
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
        View v = super.onCreateView(inflater, container, savedInstanceState);

        //Get the view pager
        ViewPager vpPager = (ViewPager) v.findViewById(R.id.viewpagerUser);
        //Set the view adapter for the view pager
        vpPager.setAdapter(new HeaderPagerAdapter(getActivity().getSupportFragmentManager()));
        //find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
        //attach the pager tabs to the view pager
        tabStrip.setViewPager(vpPager);

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



    public class HeaderPagerAdapter extends FragmentPagerAdapter {

        public HeaderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
             return new UserHeaderFragment();
            else if (position ==1)
                return new UserHeaderBioFragment();
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


    }
}

