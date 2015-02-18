package com.codepath.apps.mysimpletweets.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.EndlessScrollListener;
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

public abstract class TweetsListFragment extends Fragment{

    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;
    private long max_id=1;
    private int numberSavedTweets = 0;

    protected ListView lvTweets;
    protected TwitterClient client;
    protected User user;
    protected SwipeRefreshLayout swipeContainer;


    //inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);


        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        //connect listview and adapter
        lvTweets.setAdapter(adapter);

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

    //creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create arraylist
        tweets = new ArrayList<>();
        //create adapter;
        adapter = new TweetsArrayAdapter(getActivity(), tweets);
    }

    protected abstract void populateTimeline();

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
