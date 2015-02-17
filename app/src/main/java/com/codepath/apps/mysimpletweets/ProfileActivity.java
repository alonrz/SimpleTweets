package com.codepath.apps.mysimpletweets;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.fragments.UserHeaderFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity {

    TwitterClient client;
    User user;
    UserTimelineFragment fragmentUserTimeline;
    UserHeaderFragment fragmentUserHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApplication.getRestClient();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //get Screen name from the activity
        String screenName = getIntent().getStringExtra("screenName");

        //get account info
        client.getUserProfile(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                //current user account info
                getSupportActionBar().setTitle(user.getScreenName());
                populateProfileHeader(user);
            }
        });

        if(savedInstanceState == null) {
            //Create user time line fragment
            fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
            fragmentUserHeader = UserHeaderFragment.newInstance(user);
            //display user frag within the activity 0 here it is dynamic
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flListContainer, fragmentUserTimeline);
            ft.replace(R.id.flHeaderContainer, fragmentUserHeader);

            ft.commit();
        }




    }

    private void populateProfileHeader(User user) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
