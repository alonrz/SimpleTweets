package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*

    [

        {
        "text": "Introducing the Twitter Certified Products Program: https://t.co/MjJ8xAnT",
        "retweet_count": 121,
        "in_reply_to_status_id_str": null,
        "id": 240859602684612608,
        "geo": null,
        "retweeted": false,
        "possibly_sensitive": false,
        "in_reply_to_user_id": null,
        "place": null,
        "user": {
          "profile_sidebar_fill_color": "DDEEF6",
          "profile_sidebar_border_color": "C0DEED",
          "profile_background_tile": false,
          "name": "Twitter API",
          "profile_image_url": "http://a0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
          "created_at": "Wed May 23 06:01:13 +0000 2007",
          "location": "San Francisco, CA",
          "follow_request_sent": false,
          "profile_link_color": "0084B4",
          "is_translator": false,
          "id_str": "6253282",
          "entities": {
            "url": {
              "urls": [
                {
                  "expanded_url": null,
                  "url": "http://dev.twitter.com",
                  "indices": [
                    0,
                    22
                  ]
                }
              ]
            },
            "description": {
              "urls": [

              ]
            }
          },
          "default_profile": true,
          "contributors_enabled": true,
          "favourites_count": 24,
          "url": "http://dev.twitter.com",
          "profile_image_url_https": "https://si0.twimg.com/profile_images/2284174872/7df3h38zabcvjylnyfe3_normal.png",
          "utc_offset": -28800,
          "id": 6253282,
          "profile_use_background_image": true,
          "listed_count": 10775,
          "profile_text_color": "333333",
          "lang": "en",
          "followers_count": 1212864,
          "protected": false,
          "notifications": null,
          "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme1/bg.png",
          "profile_background_color": "C0DEED",
          "verified": true,
          "geo_enabled": true,
          "time_zone": "Pacific Time (US & Canada)",
          "description": "The Real Twitter API. I tweet about API changes, service issues and happily answer questions about Twitter and our API. Don't get an answer? It's on my website.",
          "default_profile_image": false,
          "profile_background_image_url": "http://a0.twimg.com/images/themes/theme1/bg.png",
          "statuses_count": 3333,
          "friends_count": 31,
          "following": null,
          "show_all_inline_media": false,
          "screen_name": "twitterapi"
        },
      },
      {....}
    ]


 */
@Table(name = "tweets")
public class Tweet extends Model implements Serializable {
    //list attributes
    @Column(name = "body")
    private String body;

    @Column(name = "unique_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index=true)
    private long uniqueId; //unique ID for tweet

    @Column(name = "user")
    private User user;

    @Column(name = "created_at")
    private String createdAt;

    public String getBody() {
        return body;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public String getCreatedAt() {
        if(createdAt == null || createdAt.isEmpty())
            return "now";
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Tweet() {
    }

    public Tweet(String body, User user)
    {
        this.body = body;
        this.user = user;
    }
    public static Tweet fromJSON(JSONObject jsonObject)
    {
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uniqueId = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if(tweet != null)
                    tweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
                continue; //if one fails, keep processing others
            }
        }
        return tweets;
    }

    public static void dropTable()
    {
        try {
            List<Tweet> tempList = new Select().from(Tweet.class).execute();
            for (int i = 0; i < tempList.size(); i++) {
                tempList.get(i).delete();
                tempList.get(i).getUser().delete();

            }
        }catch
                (Exception e){
            e.printStackTrace();
        }
    }

    public static List<Tweet> getAllFromDB()
    {
        List<Tweet> tweets = new ArrayList<>();
        try {
            return new Select().from(Tweet.class).execute();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return tweets;
    }
}
