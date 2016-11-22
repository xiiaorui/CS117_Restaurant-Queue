package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity implements ClientListener{
    private static final String TAG = "login";
    EditText text;
    String Name_rest;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientClass.init(this, false);
        setContentView(R.layout.activity_login);

        text = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);
        clientClass.get().setListener(this);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Name_rest = text.getText().toString();
                if (Name_rest.isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Empty name is not allowed",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    clientClass.get().createRestaruant(Name_rest);
                    button.setEnabled(false);
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
    public void onMessage(JSONObject resp){
        MessageType messageType = clientClass.get().getType(resp);
        if (messageType == MessageType.ACTION_CREATE_RESTAURANT) {
            try {
                int id = resp.getInt("restaurant_id");
                clientClass.get().openRestaruant(id);
            } catch (JSONException e) {
            }
        } else if (messageType == MessageType.ACTION_OPEN_RESTAURANT) {
            try {
                int errorCode = resp.getInt("error");
                if (errorCode == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), DisplayCustomerInfo.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    // error opening restaurant
                }
            } catch (JSONException e) {
            }
        }
    }

    @Override
    public void onError(Exception e) {

    }

}
