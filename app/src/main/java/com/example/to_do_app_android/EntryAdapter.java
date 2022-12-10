package com.example.to_do_app_android;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntryAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<EntryModel> Entries;
    Context context;
    BaseAdapter instance = this;

    public EntryAdapter(Context applicationContext, ArrayList<EntryModel> entries) {
        inflater = LayoutInflater.from(applicationContext);
        Entries = entries;
        context = applicationContext;
    }

    public void addEntry(EntryModel entryModel) {
        Entries.add(entryModel);
    }

    @Override
    public int getCount() {
        return Entries.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.entry_item_view, null);
        EditText title = view.findViewById(R.id.titleEditTextView);
        ImageView editButton = view.findViewById(R.id.edit_icon);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setEnabled(true);
                editButton.setImageResource(R.drawable.check_svgrepo_com);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONObject data = new JSONObject();
                        try {
                            data.put("Id", Entries.get(i).Id);
                            data.put("Title", title.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        StringRequest jsonObjectRequest = new StringRequest(Request.Method.PATCH, Configuration.IP_ADDRESS + "/entry", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show();
                                editButton.setImageResource(R.drawable.pencil_edit_button_svgrepo_com);
                                title.setEnabled(true);
                                Entries.get(i).Title = title.getText().toString();
                                instance.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse == null) {
                                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                                }
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Authorization", "Bearer " + Configuration.BEARER_TOKEN);

                                return params;
                            }
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return data.toString().getBytes(StandardCharsets.UTF_8);
                            }
                        };
                        Volley.newRequestQueue(context).add(jsonObjectRequest);
                    }
                });
            }
        });
        ImageView deleteButton = view.findViewById(R.id.delete_icon);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, Configuration.IP_ADDRESS + "/entry/" + Entries.get(i).Id, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
                        Entries.remove(i);
                        instance.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, new String(error.networkResponse.data, StandardCharsets.UTF_8), Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "Bearer " + Configuration.BEARER_TOKEN);
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                Volley.newRequestQueue(context).add(jsonObjectRequest);

            }
        });
        title.setText(Entries.get(i).Title);
        return view;
    }
}
