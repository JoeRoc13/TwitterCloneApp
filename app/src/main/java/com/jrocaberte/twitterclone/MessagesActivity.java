package com.jrocaberte.twitterclone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

public class MessagesActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    private RecyclerView mList;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Message> messageList;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setTitle("Messages");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mList = findViewById(R.id.message_list);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(getApplicationContext(), messageList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);

        getMessages();
    }

    private void getMessages() {
        RequestQueue queue = Volley.newRequestQueue(MessagesActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = Constants.HOME_SERVER + Constants.MESSAGES_URL;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response.toString());
                            String success = jsonObject.getString("success");
                            if(success.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("messages");
                                for(int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject messages = jsonArray.getJSONObject(i);
                                    String sender_name = messages.getString("sender_name");
                                    String message = messages.getString("body");
                                    String send_time = messages.getString("send_time");

                                    Message m = new Message();
                                    m.setSenderName(sender_name);
                                    m.setMessage(message);
                                    m.setSendTime(send_time);
                                    messageList.add(m);
                                }
                                adapter.notifyDataSetChanged();
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
                Log.d("error: ", error.toString());
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                String uid = Integer.toString(userInfo.getInt("uid", 0));
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
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
