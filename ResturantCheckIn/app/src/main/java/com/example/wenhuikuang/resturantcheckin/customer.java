package com.example.wenhuikuang.resturantcheckin;

import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.wenhuikuang.resturantcheckin.MainScreen;

public class customer extends AppCompatActivity {

    Button checkIn;

    EditText Name, size;
    String Customer_name,Size_of_party;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Name = (EditText)findViewById(R.id.NameOfCustomer);
        size = (EditText)findViewById(R.id.SizeOfParty);
        checkIn = (Button)findViewById(R.id.checkIn);

        checkIn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Customer_name = Name.getText().toString();
                Size_of_party = size.getText().toString();
            }
        });


    }
}
