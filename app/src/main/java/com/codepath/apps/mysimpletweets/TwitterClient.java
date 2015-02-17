package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "bMODWdlEh9fRNNOL822p1f5jp";       // Change this
    public static final String REST_CONSUMER_SECRET = "Zqj6EF49PRWxlgzKe80DgmdGbDvDMdrYmlyvOvflZvfHXR568H"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://ARZTweetsApp"; // Change this (here and in manifest)

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // CHANGE THIS
    // DEFINE METHODS for different API endpoints here
    public void getInterestingnessList(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList");
        // Can specify query string params directly or through RequestParams.
        RequestParams params = new RequestParams();
        params.put("format", "json");
        client.get(apiUrl, params, handler);
    }

    //Method == END POINT

    /* Send compose tweet
    POST statuses/update.json
        status = the text of the tweet
    */
    public void postComposedTweet(AsyncHttpResponseHandler handler, String text) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        getClient().post(apiUrl, params, handler);
    }


    /* HomeTimeLine - Gets us the home timeline
    GET statuses/user_timeline.json
            count=25
            since_id=1
            */
    public void getHomeTimeline(AsyncHttpResponseHandler handler, long max_id) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        //spedify params
        RequestParams params = new RequestParams();
        params.put("count", 25);
//        params.put("since_id", 1);
        if (max_id > 10)
            params.put("max_id", max_id - 1); //the -1 is b/c max_id is inclusive.
        //exec the request
        Log.i("CONNECTING", "a call was made to twitter!");
        getClient().get(apiUrl, params, handler);

    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        //spedify params
        RequestParams params = new RequestParams();
        params.put("count", 25);
//        params.put("since_id", 1);
//        if(max_id > 10)
//            params.put("max_id", max_id-1); //the -1 is b/c max_id is inclusive.
        //exec the request
        Log.i("CONNECTING", "a call was made to twitter!");
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("screen_name", screenName);
        getClient().get(apiUrl, params, handler);

    }

    /* getUserInfo - get the user information by his authentication
    // GET account/verify_credentials.json */
    public void getUserProfile(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, handler);
    }

    /**
     * Used to find profile info for OTHER users
     * @param screenName
     * @param handler
     */
    public void getUserProfile(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
//       no params. The default is good.
        getClient().get(apiUrl, params, handler);
    }

}