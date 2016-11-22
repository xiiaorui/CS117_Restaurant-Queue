package com.example.wenhuikuang.resturantcheckin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class customerWaitscreen extends AppCompatActivity implements ClientListener {
    private static final String TAG = "customerWaitscreen";
    TextView position,Wait_time;
    Button refresh,cancelButton;
    int wait_time1, Position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_waitscreen);
        clientClass.get().setListener(this);

        Position = getIntent().getExtras().getInt("position");
        wait_time1 = getIntent().getExtras().getInt("wait_time");
        position = (TextView) findViewById(R.id.textView3);
        Wait_time = (TextView) findViewById(R.id.wait_time);
        refresh = (Button)findViewById(R.id.refresh);
        cancelButton = (Button)findViewById(R.id.customer_waitscreen_cancel_button);
        position.setText(Integer.toString(Position));
        Wait_time.setText(Integer.toString(wait_time1));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.get().leaveQueue();
                Intent intent = new Intent(getApplicationContext(),DisplayListView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientClass.get().queue_status();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO update
                    new AlertDialog.Builder(customerWaitscreen.this)
                            .setTitle("Restaurant call.")
                            .setMessage("Press OK to ... ?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);
                                }
                            }).create().show();
                }
            });
        } else if (messageType == MessageType.NOTIFY_CLOSE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(customerWaitscreen.this)
                            .setTitle("Restaurant has closed.")
                            .setMessage("Press OK to return to restaurant listing.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create().show();
                }
            });
        }else{
            wait_time1 = resp.getInt("wait_time");
            Position = resp.getInt("position");
            position.setText(Integer.toString(Position));
            Wait_time.setText(Integer.toString(wait_time1));
        }
    }

    @Override
    public void onError(Exception e) {

    }
}
