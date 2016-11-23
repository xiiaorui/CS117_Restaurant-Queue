package com.example.wenhuikuang.resturantcheckin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class DisplayListView extends AppCompatActivity implements ClientListener {
    restaurantAdapter restaurantAdapter;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<restaurant> mRestuarant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_view);

        if (clientClass.get() == null)
            clientClass.init(this,true);
        else
            clientClass.get().setListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_display_list_view);
        listView = (ListView)findViewById(R.id.listview);
        mRestuarant = new ArrayList<>();
        
        restaurantAdapter = new restaurantAdapter(getApplicationContext(),mRestuarant);
        listView.setAdapter(restaurantAdapter);
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                clientClass.get().getRestaurant();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        refreshRestaurantInfo();
                    }
                },2000);
            }
        });
        //get restaruant
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                restaurant rest = (restaurant)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),rest.getName() + " selected", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),customer.class);
                int Id = rest.getId();
                intent.putExtra("restaurant_id",Id);
                startActivity(intent);
            }
        });
        
        
    }
    
    @Override
    public void onOpen() {
        
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        
    }
    
    @Override
    public void onMessage(JSONObject resp) throws JSONException {
        mRestuarant.clear();
        int count = 0;
        if (resp.has("list")) {
            try {
                JSONArray array = resp.getJSONArray("list");
                while (count < array.length()) {
                    JSONArray JA = array.getJSONArray(count);
                    int id = (int) JA.get(0);
                    String name = (String) JA.get(1);
                    //                Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                    restaurant rest = new restaurant(name, id);
                    mRestuarant.add(rest);
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onError(Exception e) {
        if (!clientClass.get().isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(DisplayListView.this)
                            .setTitle("Unable to connect to server.")
                            .setMessage("Press OK to exit.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);
                                }
                            }).create().show();
                }
            });
        }
    }
    
    private void refreshRestaurantInfo()
    {
        swipeRefreshLayout.setRefreshing(true);
        //update the listview.
        restaurantAdapter.notifyDataSetChanged();
        restaurantAdapter = new restaurantAdapter(getApplicationContext(),mRestuarant);
        listView.setAdapter(restaurantAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    public void onBackPressed() {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(DisplayListView.this);
        mBuilder.setMessage("Do you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(false);
        android.app.AlertDialog alert = mBuilder.create();
        alert.show();
    }
}
