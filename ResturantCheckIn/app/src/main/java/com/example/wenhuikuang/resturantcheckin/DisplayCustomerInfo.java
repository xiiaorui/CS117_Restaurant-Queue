package com.example.wenhuikuang.resturantcheckin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayCustomerInfo extends AppCompatActivity implements ClientListener {
    private final String TAG = "login";
    customerAdapter customerAdapter;
    ListView listView;
    List<customerInfo> customerInfos;
    Button refresh;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = (ListView)findViewById(R.id.listview);
        refresh = (Button)findViewById(R.id.refresh1);
        customerInfos = new ArrayList<>();
        clientClass.get().setListener(this);
        clientClass.get().getParties();
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clientClass.get().getParties();
//                finish();
//                startActivity(getIntent());
//            }
//        });

    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(JSONObject resp) throws JSONException {
        Log.d(TAG,resp.toString());
        try {
            JSONArray array = resp.getJSONArray("list");
            while (count < array.length()) {
                JSONArray JA = array.getJSONArray(count);
                int id = (int) JA.get(0);
                String name = (String)JA.get(1);
                int size = (int)JA.get(2);
//                Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
                customerInfo customer = new customerInfo(id,name,size);
                customerInfos.add(customer);
                count++;
            }
            customerAdapter = new customerAdapter(getApplicationContext(),customerInfos);
            listView.setAdapter(customerAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {

    }
}
