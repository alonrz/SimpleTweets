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
 * Created by alonrz on 2/15/15.
 */
public class MentionsTimelineFragment extends TweetsListFragment{
    private TwitterClient client;
    private boolean firstRun = true;
    private User user;

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

        return v;
    }

    protected void populateTimeline() {
        //get json
        client.getMentionsTimeline(getMax_id(), new JsonHttpResponseHandler() {
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
