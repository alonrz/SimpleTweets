package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        final Tweet tweet = getItem(position);
        //find or inflate the template
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

        //find the subview to fill data in the templates
        final ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);

        //poopulate data into subviews
        tvBody.setText(tweet.getBody());
        tvFullName.setText(tweet.getUser().getFullName());
        tvUserName.setText(tweet.getUser().getScreenName());
        if(tweet.getCreatedAt().toLowerCase() == "now")
            tvTime.setText(tweet.getCreatedAt());
        else
            tvTime.setText(getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(android.R.color.transparent);//clear
        ivProfileImage.setTag(tweet.getUser().getScreenName());
        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .into(ivProfileImage);
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(getContext(), "id: " + v.getId() + ", tag: " + v.getTag(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("screenName", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });
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
        relativeDate = relativeDate.replaceAll(" second.* ago", "s");
        relativeDate = relativeDate.replaceAll(" minute.* ago", "m");
        relativeDate = relativeDate.replaceAll(" hour.* ago", "h");
        relativeDate = relativeDate.replaceAll(" day.* ago", "d");
        relativeDate = relativeDate.replaceAll(" week.* ago", "w");
        relativeDate = relativeDate.replaceAll(" month.* ago", "mo");
        return relativeDate;
    }
}
