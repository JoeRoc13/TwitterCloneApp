package com.jrocaberte.twitterclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    private MenuItem mSignInButton, mSignOutButton, mMessagesButton;

    private EditText mSearch;

    private SharedPreferences userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        mSearch = (EditText)findViewById(R.id.search);

        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void performSearch() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        mSignInButton = menu.findItem(R.id.action_sign_in);
        mSignOutButton = menu.findItem(R.id.action_sign_out);
        mMessagesButton = menu.findItem(R.id.action_messages);
        mSignOutButton.setVisible(false);
        mMessagesButton.setVisible(false);

        // Redirect user to login/register page if not logged in
        if(userInfo.contains("uid")) {
            mSignInButton.setVisible(false);
            mSignOutButton.setVisible(true);
            mMessagesButton.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_sign_in:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_sign_out:
                this.getSharedPreferences("UserInfo", 0).edit().clear(). apply();
                finish();
                startActivity(getIntent());
                return true;
            case R.id.action_messages:
                Intent messages_intent = new Intent(MainActivity.this, MessagesActivity.class);
                startActivity(messages_intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
