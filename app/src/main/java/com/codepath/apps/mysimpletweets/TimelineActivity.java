package com.codepath.apps.mysimpletweets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_COMPOSE = 300;
    private TwitterClient client;
    private TweetsArrayAdapter adapter;
    private ArrayList<Tweet> tweets;
    private ListView lvTweets;
    private long max_id=1;
    private int numberSavedTweets = 0;
    private  User user;
    private boolean firstRun = true;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                max_id=1;
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
                if (errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                else
                    Log.d("DEBUG", "getUserProdile Failes! Status code: " + statusCode);
            }
        });
    }

    private void loadMoreDataFromAPI(int page, int totalItemsCount) {
        populateTimeline();
    }

    private void populateTimeline() {

        if(firstRun)
        {
            adapter.addAll(Tweet.getAllFromDB());
        }
        //get json
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                ArrayList<Tweet> tweetsFromJson = Tweet.fromJSONArray(json);

                /*
                 * if this is the first load - save tweets to DB. Dont save more than the
                 * first batch of 25
                 */
                if(firstRun) { //Load from DB first. Jump over for endless scroll loadings.
                    firstRun = false;
                    adapter.clear();

                    try {
                        Tweet.dropTable();
                        List<Tweet> tempListTweets = new Select().from(Tweet.class).execute();//DEBUG
                        List<Tweet> tempListUsers = new Select().from(User.class).execute();//DEBUG
                        Log.d("DEBUG" , "Num of items in table (tweets/users): " + tempListTweets.size() + "/" + tempListUsers.size());//DEBUG

                        /*
                         * Save in one transaction the tweets to DB
                         */
                        ActiveAndroid.beginTransaction();
                        for (int i = 0; i < tweetsFromJson.size(); i++) {
                            tweetsFromJson.get(i).getUser().save();
                            tweetsFromJson.get(i).save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }
                }

                adapter.addAll(tweetsFromJson);
                swipeContainer.setRefreshing(false);
                //Get the max_id from the last tweet on the list.
                if(adapter.getCount()-1 >=0 ) {
                    Tweet t = adapter.getItem(adapter.getCount() - 1);
                    max_id = t.getUniqueId();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if(errorResponse != null)
                    Log.d("DEBUG", errorResponse.toString());
                else {
                    Log.d("DEBUG", "populateTimeline Failed! Status code: " + statusCode);
                }

                new AlertDialog.Builder(TimelineActivity.this)
                        .setTitle("Something Wrong...")
                        .setMessage("Internet down?! \nWe could not fetch new tweets.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                adapter.clear();
                adapter.addAll(Tweet.getAllFromDB());
                swipeContainer.setRefreshing(false);
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
//            String body = data.getStringExtra("body");

//            Tweet t = new Tweet(body, user);

            adapter.clear();
            max_id=1; //make new posts come back on reload.

//            adapter.add(t);//add new tweet to adapter

            this.populateTimeline(); //then load new tweets.
        }
    }
}
