package com.codepath.apps.mysimpletweets.models;

import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Table(name = "users")
public class User extends Model implements Serializable {

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "unique_id", index=true)
    private long uniqueId;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "followers_count")
    private int followersCount = 0;

    @Column(name = "following_count")
    private int followingCount = 0;

    @Column(name = "tag_line")
    private String tagLine;

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

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public String getTagLine() {
        return tagLine;
    }

    public User() {
    }

    public User(String profileImageUrl, String screenName, String fullName) {
        this.profileImageUrl = profileImageUrl;
        this.screenName = screenName;
        this.fullName = fullName;
    }

    public static User fromJSON(JSONObject json)
    {
        User u = new User();

        try {
            u.fullName = json.getString("name");
            u.uniqueId = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.tagLine = json.getString("description");
            u.followersCount = json.getInt("followers_count");
            u.followingCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    public void dropTable()
    {
        new Delete().from(User.class).execute();
    }


}
