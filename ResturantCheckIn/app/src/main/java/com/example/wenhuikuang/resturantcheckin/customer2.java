package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class customer2 extends AppCompatActivity implements ClientListener {
    Button b1;
    Button b2;
    String json_string = null;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientClass.init(this, true);
        setContentView(R.layout.activity_customer2);

        b1 = (Button)findViewById(R.id.button2);
        b2 = (Button)findViewById(R.id.button3);
        intent = new Intent(getApplicationContext(),DisplayListView.class);
        clientClass.get().setListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.get().getRestaurant();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (json_string == null)
                {
                    Toast.makeText(getApplicationContext(),"Please get restaruant list first", Toast.LENGTH_LONG).show();
                }
                else{
                    json_string = null;
                    startActivity(intent);
                }
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
        json_string = resp.toString();
        intent.putExtra("data",json_string);
    }

    @Override
    public void onError(Exception e) {

    }
}
