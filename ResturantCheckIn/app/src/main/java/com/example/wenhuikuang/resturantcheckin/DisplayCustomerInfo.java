package com.example.wenhuikuang.resturantcheckin;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
    JSONObject temp;
    private SwipeRefreshLayout swipeRefreshLayout;
    customerAdapter customerAdapter;
    ListView listView;
    List<customerInfo> customerInfos;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_customer_info);

        listView = (ListView)findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_display_customer_info);
        customerInfos = new ArrayList<>();
        clientClass.get().setListener(this);

//        customerInfo customerInfo = new customerInfo(0,"Empty list",0);
//        customerInfo customerInfo1 = new customerInfo(0,"Empty list",0);
//        customerInfos.add(customerInfo);
//        customerInfos.add(customerInfo1);
        customerAdapter = new customerAdapter(getApplicationContext(),customerInfos);
        listView.setAdapter(customerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        fetchCustomerInfo();
                    }
                },3000);
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
            temp = resp;
//        try {
//            JSONArray array = resp.getJSONArray("list");
//            while (count < array.length()) {
//                JSONArray JA = array.getJSONArray(count);
//                int id = (int) JA.get(0);
//                String name = (String)JA.get(1);
//                int size = (int)JA.get(2);
////                Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();
//                customerInfo customer = new customerInfo(id,name,size);
//                Pair<String,customerInfo> pair = new Pair<>(Integer.toString(id),customer);
//                customerInfos.add(pair);
//                count++;
//            }
//            customerAdapter = new customerAdapter(getApplicationContext(),customerInfos);
//            listView.setAdapter(customerAdapter);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onError(Exception e) {

    }

    private void fetchCustomerInfo(){
        swipeRefreshLayout.setRefreshing(true);
        if (temp == null)
            return;
        Log.d(TAG,temp.toString());
        if (temp.has("notification")) {
            try {
                if (temp.getString("notification").equals("enter_queue")) {
                    int id = temp.getInt("party_id");
                    String name = temp.getString("party_name");
                    int size = temp.getInt("party_size");
                    customerInfo customer = new customerInfo(id, name, size);

                    customerInfos.add(customer);
                } else if (temp.getString("notification").equals("leave_queue")) {
                    for (int i = 0; i < customerInfos.size(); i++) {
                        if (customerInfos.get(i).getParty_id() == temp.getInt("party_id")) {
                            customerInfos.remove(i);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            customerAdapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
        customerAdapter = new customerAdapter(getApplicationContext(),customerInfos);
        listView.setAdapter(customerAdapter);
    }
}
