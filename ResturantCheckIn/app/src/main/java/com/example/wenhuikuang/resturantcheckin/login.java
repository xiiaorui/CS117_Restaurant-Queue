package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Attributes;

public class login extends AppCompatActivity {
    clientClass temp;
    EditText text;
    String Name_rest;
    Button button;
    URI uri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        try {
            uri = new URI("ws://159.203.248.21/server:80/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Name_rest = text.getText().toString();
                clientClass.getInstance(uri).createRestaruant(Name_rest);
                new myTask().execute(Name_rest);
                Intent intent = new Intent(getApplicationContext(),DisplayCustomerInfo.class);
                startActivity(intent);
            }


        });
    }
    private class myTask extends AsyncTask<String, Integer, String>{


        @Override
        protected String doInBackground(String... params) {
            String response = "";
            int id = 0;
            do {
                response = clientClass.getInstance(uri).responeMessage();
            }while(response == "");
            try {
                JSONObject object = new JSONObject(response);
                id = object.getInt("restaurant_id");
//                Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                clientClass.getInstance(uri).openRestaruant(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return id+"";
        }
        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
        }
    }
}
