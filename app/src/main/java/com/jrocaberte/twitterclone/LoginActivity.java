package com.jrocaberte.twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername, mPassword;
    private Button mLogin;
    private TextView mRegister;
    public static final String PREF_FILE_NAME = "UserInfo";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRegister = (TextView) findViewById(R.id.signUp);
        mLogin = (Button) findViewById(R.id.login);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging in...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                //this is the url where you want to send the request
                //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
                String IP = "10.0.2.2";
                String url = "http://" + IP + "/Twitter_Clone/API/login.php";
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();

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
                                        // Get columns from response
                                        Integer uid = Integer.parseInt(jsonObject.getString("uid"));
                                        String username = jsonObject.getString("username");
                                        String email = jsonObject.getString("email");
                                        String location = jsonObject.getString("location");
                                        String regis_date = jsonObject.getString("regis_date");

                                        // Store info in SharedPreferences

                                        SharedPreferences userInfo = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
                                        SharedPreferences.Editor edit = userInfo.edit();
                                        edit.putInt("uid", uid);
                                        edit.putString("username", username.trim());
                                        edit.putString("email", email.trim());
                                        edit.putString("location", location.trim());
                                        edit.putString("regis_date", regis_date.trim());
                                        edit.apply();

                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else {
                                        String errorMsg = jsonObject.getString("error");
                                        Toast.makeText(getApplicationContext(), errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    Log.d("JSON Error", e.toString());
                                }
                                Log.d("response: ",response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.d("error: ", error.toString());
                    }
                }) {
                    //adding parameters to the request
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                if(!username.equals("") && !password.equals("")) {
                    queue.add(stringRequest);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a username and password",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}

