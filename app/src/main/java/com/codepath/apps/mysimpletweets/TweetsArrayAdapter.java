package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//Taking Tweet objects and turning them into views to display in list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the tweet
        Tweet tweet = getItem(position);
        //find or inflate the template
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

        //find the subview to fill data in the templates
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvFullName);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);

        //poopulate data into subviews
        tvBody.setText(tweet.getBody());
        tvUserName.setText(tweet.getUser().getScreenName());
        tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(android.R.color.transparent);//clear
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);
        //return the view to be inserted in the list
        return convertView;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";

        long dateMillis = 0;
        try {
            dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //reformat string and turn "minutes ago" into "m" and "seconds ago" to "s"
        relativeDate = relativeDate.replace(" minutes ago", "m");
        relativeDate = relativeDate.replace(" seconds ago", "s");
       return relativeDate;
    }
}
