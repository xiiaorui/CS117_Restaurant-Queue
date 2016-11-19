package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
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
                restaurant rest = new restaurant(name,id);
                mRestuarant.add(rest);
                count++;
            }
            restaurantAdapter = new restaurantAdapter(getApplicationContext(),mRestuarant);
            listView.setAdapter(restaurantAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                restaurant rest = (restaurant)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),rest.getName() + " selected", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),customer.class);
                int Id = rest.getId();
                intent.putExtra("id",Id);
                startActivity(intent);
            }
        });


    }
}
