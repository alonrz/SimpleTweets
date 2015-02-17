package com.codepath.apps.mysimpletweets.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment{

    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;
    private long max_id=1;
    private int numberSavedTweets = 0;

    protected ListView lvTweets;
    protected TwitterClient client;
    protected User user;


    //inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);


        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        //connect listview and adapter
        lvTweets.setAdapter(adapter);

        return v;
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                User u = User.fromJSON(json);
                //Store in sharedPreferences
                SharedPreferences userInfo = getActivity().getSharedPreferences("userInfo", 0);
                SharedPreferences.Editor editor = userInfo.edit();
                editor.putString("ScreenName", u.getScreenName());
                editor.putString("FullName", u.getFullName());
                editor.putString("ProfileImageUrl", u.getProfileImageUrl());
                editor.putLong("UniqueId", u.getUniqueId());
                editor.apply();
                user = u;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                else
                    Log.d("DEBUG", "getUserProdile Failes! Status code: " + statusCode);
            }
        });
    }

    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create arraylist
        tweets = new ArrayList<>();
        //create adapter;
        adapter = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public void addAll(List<Tweet> tweets) {
        this.adapter.addAll(tweets);
    }

    public void clear() {
        this.adapter.clear();
    }

    public int getCount() {
        return this.adapter.getCount();
    }

    public Tweet getItem(int i) {
        return this.adapter.getItem(i);
    }

    public long getMax_id() {
        return max_id;
    }
    public void setMax_id(long maxId) {
        max_id = maxId;
    }
}
