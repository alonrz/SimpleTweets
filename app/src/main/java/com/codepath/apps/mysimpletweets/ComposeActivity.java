package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

public class ComposeActivity extends ActionBarActivity {

    EditText etBody;
    ActionMenuItemView item_count;
    ActionMenuItemView item_send;
    int charCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        etBody = (EditText) findViewById(R.id.etBody);
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(item_count == null)
                    item_count = (ActionMenuItemView) findViewById(R.id.char_count);
                if(item_send == null)
                    item_send = (ActionMenuItemView) findViewById(R.id.action_send);
                charCount = etBody.getText().length();
                item_count.setTitle(String.valueOf(140-charCount));
                if(charCount > 140) { //over 140 char
                    item_count.setTextColor(Color.argb(255, 255, 40, 40));
                    item_send.setEnabled(false);
                }
                else if(charCount == 0)//nothing is written
                    item_send.setEnabled(false);
                else { //regular amount of char. >0 and <=140
                    item_count.setTextColor(Color.argb(255, 255, 255, 255));
                    item_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        SharedPreferences userInfo  = getSharedPreferences("userInfo", 0);
        tvFullName.setText(userInfo.getString("FullName", "Name"));
        tvScreenName.setText(userInfo.getString("ScreenName", "@screen_name"));
        String ProfileImageUrl = userInfo.getString("ProfileImageUrl", null);
        if(ProfileImageUrl != null)
            Picasso.with(this).load(ProfileImageUrl).into(ivProfileImage);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            Intent data = new Intent();
            Toast.makeText(this, etBody.getText(), Toast.LENGTH_SHORT).show();
            data.putExtra("tweet", new Tweet());
            setResult(RESULT_OK, data);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
