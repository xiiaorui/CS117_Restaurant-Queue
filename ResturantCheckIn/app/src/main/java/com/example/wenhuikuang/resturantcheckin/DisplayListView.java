package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URISyntaxException;

public class DisplayListView extends AppCompatActivity {
    String Json_string;
    clientClass temp;
    restaurantAdapter restaurantAdapter;
    ListView listView;
    List<restaurant> mRestuarant;
    String json_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_view);


        listView = (ListView)findViewById(R.id.listview);
        mRestuarant = new ArrayList<>();

        //get restaruant
        URI uri = null;
        try {
            uri = new URI("ws://159.203.248.21/server:80/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        int count = 0;
        json_string = getIntent().getExtras().getString("data");



        try {
            JSONObject object = new JSONObject(json_string);
            JSONArray array = object.getJSONArray("list");
            while (count < array.length()) {
                JSONArray JA = array.getJSONArray(count);
                int id = (int) JA.get(0);
                String name = (String)JA.get(1);
//                Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                restaurant rest = new restaurant(name);
                mRestuarant.add(rest);
                count++;
            }
            restaurantAdapter = new restaurantAdapter(getApplicationContext(),mRestuarant);
            listView.setAdapter(restaurantAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
