package com.example.to_do_app_android;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
public EditText UserNameText;
public EditText PasswordText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserNameText = findViewById(R.id.usernameTextView);
        PasswordText = findViewById(R.id.editTextTextPassword);
    }

    public void Login(View view){
        JSONObject data = new JSONObject();
        try {
            data.put("Name",UserNameText.getText().toString().trim());
            data.put("Password",PasswordText.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, Configuration.IP_ADDRESS + "/user/login",
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Configuration.BEARER_TOKEN = response;
                        startActivity(new Intent(MainActivity.this, EntryActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            public byte[] getBody() {
                return data.toString().getBytes();
            }
            public String getBodyContentType() {
                return "application/json";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    public void GoToRegister(View view){
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        finish();
    }
}