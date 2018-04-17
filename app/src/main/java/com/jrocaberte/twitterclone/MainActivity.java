package com.jrocaberte.twitterclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        if(!userInfo.contains("uid")) {
            Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    public void logoutUser(View view) {
        this.getSharedPreferences("UserInfo", 0).edit().clear(). apply();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
