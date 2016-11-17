package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

public class MainScreen extends AppCompatActivity {
    Button customer;
    Button resturant;
    String Json_string;

    URI uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        customer = (Button) findViewById(R.id.customerB);
        resturant = (Button) findViewById(R.id.resturantB);

        try {
            uri = new URI("ws://159.203.248.21/server:80/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        clientClass connection = clientClass.getInstance(uri);
        connection.connect();

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
                clientClass.getInstance(uri).getRestaurant();
                new myTask().execute();

            }

        });

    }
    private class myTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            String json_string;
            do {
                json_string = clientClass.getInstance(uri).responeMessage();
            }while(json_string == null);
            return json_string;
        }

        protected void onPostExecute(String result){
            Json_string = result;
            Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
            intent.putExtra("data",Json_string);
            startActivity(intent);
//            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
        }
    }

}
