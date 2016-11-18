package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class login extends AppCompatActivity implements ClientListener{
    private final String TAG = "login";
    clientClass temp;
    EditText text;
    String Name_rest;
    Button button;
    URI uri = null;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        text = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        clientClass.get().setListener(this);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Name_rest = text.getText().toString();
                clientClass.get().createRestaruant(Name_rest);
                Toast.makeText(getApplicationContext(),Name_rest + " Created",Toast.LENGTH_LONG).show();
                intent = new Intent(getApplicationContext(),DisplayCustomerInfo.class);
                startActivity(intent);
//                new myTask().execute(Name_rest);
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
    public void onMessage(JSONObject resp){
        Log.d(TAG,resp.toString());
        int id = 0;
        if (!resp.isNull("restaurant_id")) {
            try {
                id = resp.getInt("restaurant_id");
                clientClass.get().openRestaruant(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_LONG).show();


    }

    @Override
    public void onError(Exception e) {

    }

//    private class myTask extends AsyncTask<String, Integer, String>{
//
//
//        @Override
//        protected String doInBackground(String... params) {
//            String response = "";
//            int id = 0;
//            do {
//                response = clientClass.getInstance(uri).responeMessage();
//            }while(response == "");
//            try {
//                JSONObject object = new JSONObject(response);
//                id = object.getInt("restaurant_id");
//                Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
//                clientClass.getInstance(uri).openRestaruant(id);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            return id+"";
//        }
//        @Override
//        protected void onPostExecute(String result) {
////            Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
//        }
//    }
}
