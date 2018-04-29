package com.jrocaberte.twitterclone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetsActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    private EditText mNewTweet;

    private Button mPostTweet;

    private ProgressDialog progressDialog;

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Tweet> tweetList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);

        progressDialog = new ProgressDialog(TweetsActivity.this);
        progressDialog.setMessage("Loading tweets...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getSupportActionBar().setTitle("Tweets");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mList = findViewById(R.id.tweet_list);

        tweetList = new ArrayList<>();
        adapter = new TweetAdapter(getApplicationContext(), tweetList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        getTweets();

        mNewTweet = (EditText)findViewById(R.id.newTweet);
        mPostTweet = (Button)findViewById(R.id.btnPostTweet);

        mPostTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postTweet();
            }
        });
    }

    private void postTweet() {
        RequestQueue queue = Volley.newRequestQueue(TweetsActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Constants.HOME_SERVER + Constants.POST_TWEET_URL;
        final String tweet_body = mNewTweet.getText().toString();

        progressDialog = new ProgressDialog(TweetsActivity.this);
        progressDialog.setMessage("Posting...");
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
                                progressDialog.dismiss();
                                String tweet_body = mNewTweet.getText().toString();
                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                String post_time = timestamp.toString().split("\\.")[0];
                                Tweet tweet = new Tweet();
                                tweet.setTweet(tweet_body);
                                tweet.setPostTime(post_time);
                                tweetList.add(0, tweet);
                                adapter.notifyDataSetChanged();
                                mNewTweet.setText("");
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            } else {
                                progressDialog.dismiss();
                                String errorMsg = jsonObject.getString("error");
                                Toast.makeText(getApplicationContext(), errorMsg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
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
                SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                String uid = Integer.toString(userInfo.getInt("uid", 0));
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("tweet", tweet_body);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        if(!tweet_body.trim().equals("")) {
            queue.add(stringRequest);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "You forgot to type a tweet!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getTweets() {
        RequestQueue queue = Volley.newRequestQueue(TweetsActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Constants.HOME_SERVER + Constants.GET_TWEETS_URL;

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
                            Log.d("JSON Error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Connection failure!", Toast.LENGTH_SHORT).show();
                Log.d("Error", error.toString());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                String username = userInfo.getString("username", "");
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
