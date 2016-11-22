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
    Button refresh,cancelButton;
    int wait_time, Position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_waitscreen);

        Position = getIntent().getExtras().getInt("position");
        wait_time = getIntent().getExtras().getInt("wait_time");
        position = (TextView) findViewById(R.id.textView3);
        Wait_time = (TextView) findViewById(R.id.Wait_time);
        refresh = (Button)findViewById(R.id.refresh);
        cancelButton = (Button)findViewById(R.id.customer_waitscreen_cancel_button);
        position.setText(Integer.toString(Position));
        Wait_time.setText(Integer.toString(wait_time));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.get().leaveQueue();
                Intent intent = new Intent(getApplicationContext(),customer2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                finish();
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
        MessageType messageType = clientClass.get().getType(resp);
        if (messageType == MessageType.NOTIFY_CALL) {

        } else if (messageType == MessageType.NOTIFY_CLOSE) {

        }
    }

    @Override
    public void onError(Exception e) {

    }
}
