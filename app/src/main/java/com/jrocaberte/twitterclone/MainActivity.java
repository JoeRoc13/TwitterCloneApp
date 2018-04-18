package com.jrocaberte.twitterclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        // Redirect user to login/register page if not logged in
        if(!userInfo.contains("uid")) {
            Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Log.d("uid", Integer.toString(userInfo.getInt("uid", 0)));
        Log.d("username", userInfo.getString("username", ""));
        Log.d("email", userInfo.getString("email", ""));
        Log.d("location", userInfo.getString("location", ""));
        Log.d("regis_date", userInfo.getString("regis_date", ""));
    }

    public void logoutUser(View view) {
        this.getSharedPreferences("UserInfo", 0).edit().clear(). apply();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
