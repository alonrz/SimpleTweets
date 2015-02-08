package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String fullName;
    private long uniqueId;
    private String screenName;
    private String profileImageUrl;

    public String getFullName() {
        return fullName;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public String getScreenName() {
        return "@"+screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSON(JSONObject json)
    {
        User u = new User();

        try {
            u.fullName = json.getString("name");
            u.uniqueId = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
