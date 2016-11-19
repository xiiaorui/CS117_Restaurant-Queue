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

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainScreen extends AppCompatActivity implements ClientListener {
    Button customer;
    Button resturant;
    Intent intent;
    URI uri = null;
    MainScreen main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        customer = (Button) findViewById(R.id.customerB);
        resturant = (Button) findViewById(R.id.resturantB);

//        clientClass.init(this,true);
        main = this;
        resturant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.init(main,false);
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
            }

        });
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.init(main,true);
//                new myTask().execute();
                intent = new Intent(getApplicationContext(), customer2.class);
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
    public void onMessage(JSONObject resp) {
//        startActivity(intent);

    }

    @Override
    public void onError(Exception e) {

    }

//    private class myTask extends AsyncTask<Void,Void,String>{
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String json_string;
//            do {
//                json_string = clientClass.getInstance(uri).responeMessage();
//            }while(json_string == null);
//            return json_string;
//        }
//
//        protected void onPostExecute(String result){
//            Json_string = result;
//            Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
//            intent.putExtra("data",Json_string);
//            startActivity(intent);
////            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
//        }
//    }

}
