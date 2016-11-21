package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class customerWaitscreen extends AppCompatActivity implements ClientListener {

    TextView text,position,Wait_time;
    Button refresh,cancel;
    int wait_time, Position,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_waitscreen);

        Position = getIntent().getExtras().getInt("position");
        Log.d("TAG",Integer.toString(Position));
        wait_time = getIntent().getExtras().getInt("wait_time");
        position = (TextView) findViewById(R.id.textView3);
        Wait_time = (TextView) findViewById(R.id.Wait_time);
        refresh = (Button)findViewById(R.id.refresh);
        cancel = (Button)findViewById(R.id.button4);
        position.setText(Integer.toString(Position));
        Wait_time.setText(Integer.toString(wait_time));

        id = getIntent().getExtras().getInt("id");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.get().leaveQueue();
                Intent intent = new Intent(getApplicationContext(),customer.class);
                intent.putExtra("id",id);
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
    public void onMessage(JSONObject resp) throws JSONException {

    }

    @Override
    public void onError(Exception e) {

    }
}
