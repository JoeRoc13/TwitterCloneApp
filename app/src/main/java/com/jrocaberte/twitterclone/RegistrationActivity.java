package com.jrocaberte.twitterclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText mUsername, mEmail, mPassword, mLocation;
    private Button mRegister;
    public static final String PREF_FILE_NAME = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmail = (EditText) findViewById(R.id.email);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLocation = (EditText) findViewById(R.id.location);

        mRegister = (Button) findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(RegistrationActivity.this);
                //this is the url where you want to send the request
                String url = "http://10.0.0.33/Twitter_Clone/API/signup.php";
                final String email = mEmail.getText().toString();
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                final String location = mLocation.getText().toString();

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the response string.
//                                SharedPreferences userInfo = getPreferences(Context.MODE_PRIVATE);
//                                SharedPreferences.Editor edit = userInfo.edit();
                                try {
                                    JSONObject jsonObject= new JSONObject(response.toString());
                                    String success = jsonObject.getString("success");
                                    if(success.equals("true")) {
                                        SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor edit = userInfo.edit();
                                        edit.putInt("uid", jsonObject.getInt("uid"));
                                        edit.putString("username", username.trim());
                                        edit.putString("email", email.trim());
                                        edit.putString("location", location.trim());
                                        edit.putString("regis_date", jsonObject.getString("regis_date"));
                                        edit.apply();

                                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else {
                                        String errorMsg = jsonObject.getString("error");
                                        Toast.makeText(getApplicationContext(), errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Log.d("JSON Error", e.toString());
                                }
                                Log.d("Response",response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                }) {
                    //adding parameters to the request
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("username", username);
                        params.put("password", password);
                        params.put("location", location);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                if(!email.equals("") && !username.equals("") && !password.equals("") && !location.equals("")) {
                    queue.add(stringRequest);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill out all fields!",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
