package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class MainScreen extends AppCompatActivity {
    Button customer;
    Button resturant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        customer = (Button) findViewById(R.id.customerB);
        resturant = (Button) findViewById(R.id.resturantB);

        /*clientClass connection = new clientClass(uri);
        connection.connect();
        String Json_string = connection.responeMessage();
        if (Json_string == null)
            Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
        else{
            Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
            intent.putExtra("Json data", Json_string);
            startActivity(intent);
        }*/
        Intent intent = new Intent(getApplicationContext(),DisplayListView.class);
        //intent.putExtra("Myclass", (Serializable) connection);
        //startActivity(intent);

        Intent intent2 = new Intent(getApplicationContext(),login.class);
        //intent2.putExtra("Myclass", (Serializable) connection);
        //startActivity(intent2);
        resturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
            }

        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
                startActivity(intent);
            }

        });
    }

}
