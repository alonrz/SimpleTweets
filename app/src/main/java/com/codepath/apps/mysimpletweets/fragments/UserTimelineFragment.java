package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alon on 2/16/15.
 */
public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private boolean firstRun = true;
    private User user;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the client
        client = TwitterApplication.getRestClient();
        populateTimeline();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                clear();
                setMax_id(1);
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Set end-less scrolling here
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });

        return v;
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    private void populateTimeline() {
        String screenName = getArguments().getString("screenName");
        //get json
        client.getUserTimeline(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> tweetsFromJson = Tweet.fromJSONArray(json);

                /*
                 * if this is the first load - save tweets to DB. Dont save more than the
                 * first batch of 25
                 */
                if (firstRun) { //Load from DB first. Jump over for endless scroll loadings.
                    firstRun = false;
                    clear();

                    try {
                        Tweet.dropTable();
                        List<Tweet> tempListTweets = new Select().from(Tweet.class).execute();//DEBUG
                        List<Tweet> tempListUsers = new Select().from(User.class).execute();//DEBUG
                        Log.d("DEBUG", "Num of items in table (tweets/users): " + tempListTweets.size() + "/" + tempListUsers.size());//DEBUG

                        /*
                         * Save in one transaction the tweets to DB
                         */
                        ActiveAndroid.beginTransaction();
                        for (int i = 0; i < tweetsFromJson.size(); i++) {
                            tweetsFromJson.get(i).getUser().save();
                            tweetsFromJson.get(i).save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                }

                addAll(tweetsFromJson);
                swipeContainer.setRefreshing(false);
                //Get the max_id from the last tweet on the list.
                if (getCount() - 1 >= 0) {
                    Tweet t = getItem(getCount() - 1);
                    setMax_id(t.getUniqueId());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                else {
                    Log.d("DEBUG", "populateTimeline Failed! Status code: " + statusCode);
                }
            }
        });
    }
}
