package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    clientClass temp;
    EditText text;
    String Name_rest;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        temp = (clientClass)getIntent().getSerializableExtra("Myclass");
        text = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Name_rest = text.getText().toString();
                temp.sendMessage1(Name_rest);
                //Intent intent = new Intent(getApplicationContext(), .class);
                //startActivity(intent);
            }
        });
    }
}
