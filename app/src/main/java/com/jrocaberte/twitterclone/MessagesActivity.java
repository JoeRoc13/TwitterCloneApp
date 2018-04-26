package com.jrocaberte.twitterclone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    public static final String PREF_FILE_NAME = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getMessages();
    }

    private void getMessages() {
        RequestQueue queue = Volley.newRequestQueue(MessagesActivity.this);
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String IP = "10.253.80.122";
        String url = "http://" + IP + "/Twitter_Clone/API/messages.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
//                                SharedPreferences userInfo = getPreferences(Context.MODE_PRIVATE);
//                                SharedPreferences.Editor edit = userInfo.edit();

                        Log.d("response: ",response);
                        try {
                            JSONObject jsonObject= new JSONObject(response.toString());
                            String success = jsonObject.getString("success");
                            if(success.equals("true")) {
                                // Get columns from response
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
}
