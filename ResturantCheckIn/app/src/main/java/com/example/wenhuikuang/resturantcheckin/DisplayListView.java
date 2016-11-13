package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;

public class DisplayListView extends AppCompatActivity {
    String Json_string;
    clientClass temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_view);
        Json_string = getIntent().getExtras().getString("Json data");

        temp = (clientClass) getIntent().getSerializableExtra("Myclass");

        Intent intent = new Intent(getApplicationContext(),customer.class);
        intent.putExtra("Myclass", (Serializable) temp);
        startActivity(intent);
    }
}
