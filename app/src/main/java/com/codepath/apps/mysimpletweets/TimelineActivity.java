package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_COMPOSE = 300;
    private TwitterClient client;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private long max_id=1;
    private  User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        //create arraylist
        tweets = new ArrayList<>();
        //create adapter;
        adapter = new TweetsArrayAdapter(this, tweets);
        //connect listview and adapter
        lvTweets.setAdapter(adapter);
        //Set end-less scrolling here
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreDataFromAPI(page, totalItemsCount);
            }
        });
        //Get the client
        client = TwitterApplication.getRestClient();
        populateTimeline();
        getUserProfile();
    }

    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                User u = User.fromJSON(json);
                //Store in sharedPreferences
                SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
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
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void loadMoreDataFromAPI(int page, int totalItemsCount) {
        populateTimeline();
//        adapter.notifyDataSetChanged();
    }

    private void populateTimeline() {
        //get json
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                //Deserialize json
                adapter.addAll(Tweet.fromJSONArray(json));
                //Get the max_id from the last tweet on the list.
                if(adapter.getCount()-1 >=0 ) {
                    Tweet t = adapter.getItem(adapter.getCount() - 1);
                    max_id = t.getUniqueId();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());

            }
        }, max_id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose) {
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE_COMPOSE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_COMPOSE && resultCode == RESULT_OK)
        {
            String body = data.getStringExtra("body");

            Tweet t = new Tweet(body, user);

            adapter.clear();
            adapter.add(t);//add new tweet to adapter

            this.populateTimeline(); //then load new tweets.
        }
    }
}
