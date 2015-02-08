package com.codepath.apps.mysimpletweets;

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

public class ComposeActivity extends ActionBarActivity {

    EditText etBody;
    ActionMenuItemView item_count;

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
                item_count.setTitle(String.valueOf(140-etBody.getText().length()));
                if(etBody.getText().length()>140)
                    item_count.setTextColor(Color.argb(255, 255, 0, 0));
                else
                    item_count.setTextColor(Color.argb(255, 255, 255, 255));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);


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
        if (id == R.id.action_compose) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
