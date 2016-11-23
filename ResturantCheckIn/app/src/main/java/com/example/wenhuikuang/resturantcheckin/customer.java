package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class customer extends AppCompatActivity implements ClientListener{
    Button checkIn;
    int restaurantID, wait_time,position;

    EditText Name, size;
    String Customer_name,Size_of_party;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Name = (EditText)findViewById(R.id.NameOfCustomer);
        size = (EditText)findViewById(R.id.SizeOfParty);
        checkIn = (Button)findViewById(R.id.checkIn);

        clientClass.get().setListener(this);
        restaurantID = getIntent().getExtras().getInt("restaurant_id");

        checkIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Customer_name = Name.getText().toString();
                Size_of_party = size.getText().toString();
                if (Customer_name.isEmpty() || Size_of_party.isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter your name and size of your party",
                            Toast.LENGTH_LONG
                    ).show();
                }
                else{
                    clientClass.get().sendCustomerInfo(
                            Customer_name,
                            Integer.parseInt(Size_of_party),
                            restaurantID
                    );
                }
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DisplayListView.class));
        finish();
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(JSONObject resp) throws JSONException {
        MessageType messageType = clientClass.get().getType(resp);
        if (messageType == MessageType.ACTION_QUEUE) {
            wait_time = resp.getInt("wait_time");
            position = resp.getInt("position");
            Intent intent = new Intent(getApplicationContext(), customerWaitscreen.class);
            // set so customer waitscreen becomes only active activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("wait_time",wait_time);
            intent.putExtra("position",position);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onError(Exception e) {

    }
}
