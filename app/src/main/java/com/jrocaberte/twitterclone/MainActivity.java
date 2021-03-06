package com.jrocaberte.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    private MenuItem mSignInButton, mSignOutButton, mTweetsButton, mMessagesButton;

    private EditText mSearch;

    private Button mSearchButton;

    private TextView mSearchingFor, mMostFollowed;

    private SharedPreferences userInfo;

    private ProgressDialog progressDialog;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Tweet> tweetList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMostFollowed = (TextView) findViewById(R.id.mostFollowed);

        findMostFollowed();

        userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        mSearch = (EditText)findViewById(R.id.search);

        mSearchingFor = (TextView)findViewById(R.id.searchingFor);

        mSearchButton = (Button)findViewById(R.id.btnSearch);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mList = findViewById(R.id.tweet_list);

                tweetList = new ArrayList<>();
                adapter = new TweetAdapter(getApplicationContext(), tweetList);

                linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

                mList.setHasFixedSize(true);
                mList.setLayoutManager(linearLayoutManager);
                mList.addItemDecoration(dividerItemDecoration);
                mList.setAdapter(adapter);
                performSearch();
            }
        });

    }

    private void findMostFollowed() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Constants.HOME_SERVER + Constants.GET_MOST_FOLLOWED_URL;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response.toString());
                            String success = jsonObject.getString("success");
                            if(success.equals("true")) {
                                String username = jsonObject.getString("username");
                                mMostFollowed.setText("Most followed user: " + username);
                                mMostFollowed.setVisibility(View.VISIBLE);
                            } else {
                                String errorMsg = jsonObject.getString("error");
                                Toast.makeText(getApplicationContext(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d("JSON Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error ", error.toString());
                Toast.makeText(getApplicationContext(), "Connection failure!",
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("getMostFollowed", "true");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void performSearch() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Constants.HOME_SERVER + Constants.GET_TWEETS_URL;
        final String username = mSearch.getText().toString();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response.toString());
                            String success = jsonObject.getString("success");
                            if(success.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("tweets");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject tweets = jsonArray.getJSONObject(i);
                                    String body = tweets.getString("body");
                                    String post_time = tweets.getString("post_time");

                                    progressDialog.dismiss();

                                    mSearchingFor.setText("Showing all tweets from: " + username);
                                    mSearchingFor.setVisibility(View.VISIBLE);

                                    Tweet tweet = new Tweet();
                                    tweet.setTweet(body);
                                    tweet.setPostTime(post_time);
                                    tweetList.add(tweet);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                progressDialog.dismiss();
                                String errorMsg = jsonObject.getString("error");
                                Toast.makeText(getApplicationContext(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            mSearchingFor.setText("No tweets from user: " + username);
                            mSearchingFor.setVisibility(View.VISIBLE);
                            Log.d("JSON Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("Error ", error.toString());
                Toast.makeText(getApplicationContext(), "Connection failure!",
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        if(!username.trim().equals("")) {
            queue.add(stringRequest);
        } else {
            progressDialog.dismiss();
            mSearchingFor.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Please enter a username",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        mSignInButton = menu.findItem(R.id.action_sign_in);
        mSignOutButton = menu.findItem(R.id.action_sign_out);
        mTweetsButton = menu.findItem(R.id.action_tweets);
        mMessagesButton = menu.findItem(R.id.action_messages);
        mSignOutButton.setVisible(false);
        mTweetsButton.setVisible(false);
        mMessagesButton.setVisible(false);

        // Redirect user to login/register page if not logged in
        if(userInfo.contains("uid")) {
            mSignInButton.setVisible(false);
            mSignOutButton.setVisible(true);
            mTweetsButton.setVisible(true);
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
            case R.id.action_tweets:
                Intent tweets_intent = new Intent(MainActivity.this, TweetsActivity.class);
                startActivity(tweets_intent);
                finish();
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
