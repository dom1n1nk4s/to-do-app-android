package com.example.to_do_app_android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntryActivity extends Activity {
    ListView listView;
    TextView emptyTextView;
    ArrayList<EntryModel> entries;
    EntryAdapter entryAdapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_page);
        listView = findViewById(R.id.listView);
        emptyTextView = findViewById(R.id.emptyTextView);
        entries = GetAllEntries();
        entryAdapter = new EntryAdapter(getApplicationContext(), entries);
        listView.setAdapter(entryAdapter);
    }

    public void AddEntry(View view){
        EntryModel newEntry = new EntryModel(null, "Sample entry");
        JSONObject data = new JSONObject();
        try {
            data.put("Title",newEntry.Title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Configuration.IP_ADDRESS + "/entry", data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), "Successfully created!", Toast.LENGTH_SHORT).show();
                try {
                    newEntry.Id = response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                entryAdapter.addEntry(newEntry);
                entryAdapter.notifyDataSetChanged();
                emptyTextView.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ Configuration.BEARER_TOKEN);

                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

    }
    private ArrayList<EntryModel> GetAllEntries(){
        ArrayList<EntryModel> result = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Configuration.IP_ADDRESS + "/entry", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() == 0){
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < response.length(); i ++) {
                    try {
                        JSONObject entry = response.getJSONObject(i);
                        EntryModel newEntryModel = new EntryModel((String) entry.get("id"), (String) entry.get("title"));
                        result.add(newEntryModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                entryAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse == null) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+ Configuration.BEARER_TOKEN);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
        return result;
    }
}
