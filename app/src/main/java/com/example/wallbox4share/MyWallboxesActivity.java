package com.example.wallbox4share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MyWallboxesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String s1[], s2[];
    Location userLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallboxes);
        getSupportActionBar().setTitle("My Wallboxes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleView);
        s1 = getResources().getStringArray(R.array.my_wallboxes);
        s2 = getResources().getStringArray(R.array.description);

        MyWallboxesAdapter myWallboxesAdapter = new MyWallboxesAdapter(this, s1, s2);
        recyclerView.setAdapter(myWallboxesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addWallboxButton = findViewById(R.id.addWallboxButton);

        String url = "http://10.0.2.2:8080/wallboxes/add";

        RequestQueue queue = Volley.newRequestQueue(this);
        Gson gson = new Gson();

        SharedPreferences sharedPreferences = getSharedPreferences("id_preferences", Activity.MODE_PRIVATE);
        Long id = sharedPreferences.getLong("id", -1L);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Wallbox wallbox = new Wallbox(id, location.getLatitude(), location.getLongitude(), "phone_number");
        JSONObject wallboxObject = new JSONObject();
        try {
            wallboxObject.put("owner_id", wallbox.getOwner_id());
            wallboxObject.put("latitude", wallbox.getLatitude());
            wallboxObject.put("longitude", wallbox.getLongitude());
            wallboxObject.put("phone_number", wallbox.getPhone_number());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject saveObject = new JSONObject();
        try {
            //TODO: get current user id
            Long userId = 1L;
            saveObject.put("userId", userId);
            saveObject.put("wallbox", wallboxObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addWallboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, saveObject,
                        (Response.Listener<JSONObject>) response -> {

                            //Toast.makeText(this, "entered succesfully!", Toast.LENGTH_SHORT).show();
                        }, (Response.ErrorListener) error -> {
                    Toast.makeText(MyWallboxesActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                });

                queue.add(stringRequest);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openUserMenu();
    }

    private void openUserMenu() {
        Intent intent = new Intent(this, UserMenuCategoriesActivity.class);
        startActivity(intent);
    }
}