package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.wenhuikuang.resturantcheckin.MainScreen;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class customer extends AppCompatActivity implements ClientListener{
    private final String TAG = "ssss";
    Button checkIn;
    int id, wait_time,position;
    Intent intent;
    Button check_position;
    boolean checkInBefore = false;

    EditText Name, size;
    String Customer_name,Size_of_party;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Name = (EditText)findViewById(R.id.NameOfCustomer);
        size = (EditText)findViewById(R.id.SizeOfParty);
        checkIn = (Button)findViewById(R.id.checkIn);
        check_position = (Button)findViewById(R.id.look_at);

        clientClass.get().setListener(this);
        id = getIntent().getExtras().getInt("id");

        checkIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Customer_name = Name.getText().toString();
                Size_of_party = size.getText().toString();
                if (Customer_name.equals("") || Size_of_party.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please enter your name and size of your party",Toast.LENGTH_LONG).show();
                }
                else{
                    clientClass.get().sendCustomerInfo(Customer_name,Integer.parseInt(Size_of_party),id);
                    intent = new Intent(getApplicationContext(),customerWaitscreen.class);
                    intent.putExtra("id",id);
                    checkInBefore = true;
                }
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable(){
//                    public void run(){
//                        startActivity(intent);
//                    }
//                }, 2000);
            }
        });
        check_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInBefore)
                {
                    checkInBefore = false;
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Check in before Checking position",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), customer2.class));
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
        Log.d("TAG",resp.toString());
        wait_time = resp.getInt("wait_time");
        position = resp.getInt("position");
        intent.putExtra("wait_time",wait_time);
        intent.putExtra("position",position);
    }

    @Override
    public void onError(Exception e) {

    }
}
