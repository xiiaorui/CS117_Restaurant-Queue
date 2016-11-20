package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
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
                    Toast.makeText(
                            getApplicationContext(),
                            Name_rest + " Created",
                            Toast.LENGTH_LONG
                    ).show();
                    Intent intent = new Intent(getApplicationContext(), DisplayCustomerInfo.class);
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

}
