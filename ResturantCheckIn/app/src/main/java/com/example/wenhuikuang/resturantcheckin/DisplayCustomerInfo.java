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
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayCustomerInfo extends AppCompatActivity implements ClientListener {
    private final String TAG = "login";
    private SwipeRefreshLayout swipeRefreshLayout;
    customerAdapter customerAdapter;
    ListView listView;
    List<customerInfo> customerInfos;
    List<customerInfo> newCustomerInfos = null;
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
                refreshCustomerInfo();
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
        if (messageType == MessageType.NOTIFY_ENTER_QUEUE) {
            copyNewCustomerInfos();
            try {
                int partyID = resp.getInt("party_id");
                String partyName = resp.getString("party_name");
                int partySize = resp.getInt("party_size");
                customerInfo newCustomer = new customerInfo(partyID, partyName, partySize);
                synchronized (newCustomerInfos) {
                    newCustomerInfos.add(newCustomer);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                "New party",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            } catch (JSONException e) {
            }
        } else if (messageType == MessageType.NOTIFY_LEAVE_QUEUE) {
            copyNewCustomerInfos();
            try {
                int partyID = resp.getInt("party_id");
                // remove party from customerInfos
                synchronized (newCustomerInfos) {
                    Iterator<customerInfo> iter = newCustomerInfos.iterator();
                    while (iter.hasNext()) {
                        customerInfo party = iter.next();
                        if (party.getParty_id() == partyID) {
                            iter.remove();
                            break;
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(
                                getApplicationContext(),
                                "Party has left",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } catch (JSONException e) {
            }
        }
    }

    @Override
    public void onError(Exception e) {

    }

    private void copyNewCustomerInfos() {
        if (newCustomerInfos == null) {
            synchronized (customerInfos) {
                newCustomerInfos = new ArrayList<>(customerInfos);
            }
        }
    }

    private void refreshCustomerInfo(){
        if (newCustomerInfos == null)
            return; // no changes
        swipeRefreshLayout.setRefreshing(true);
        customerAdapter.setList(newCustomerInfos);
        newCustomerInfos = null;
        customerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
