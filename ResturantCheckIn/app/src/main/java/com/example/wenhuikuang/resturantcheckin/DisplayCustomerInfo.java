package com.example.wenhuikuang.resturantcheckin;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_customer_info);

        listView = (ListView)findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_display_customer_info);
        customerInfos = new ArrayList<>();
        clientClass.get().setListener(this);

        customerAdapter = new customerAdapter(getApplicationContext(),customerInfos);
        listView.setAdapter(customerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        refreshCustomerInfo();
                    }
                },2000);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayCustomerInfo.this);
                mBuilder.setMessage("Do you want to notify this party?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                customerInfo customerInfo = (customerInfo)parent.getItemAtPosition(position);
                                customerInfos.remove(customerInfo);// remove from current list view
                                int partyID = customerInfo.getParty_id();
                                // include change in next refresh
                                copyNewCustomerInfos();
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
                                clientClass.get().call_party(partyID);
                                customerAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No",null)
                        .setCancelable(false);
                AlertDialog alert = mBuilder.create();
                alert.show();
            }
        });
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new android.support.v7.app.AlertDialog.Builder(DisplayCustomerInfo.this)
                        .setTitle("Lost connection with server.")
                        .setMessage("Press OK to exit.")
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
                                "New party joins",
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
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        if (newCustomerInfos == null)
            return; // no changes
        // set updated customer infos to current customer infos
        customerInfos = newCustomerInfos;
        // set list to updated customer infos
        customerAdapter.setList(newCustomerInfos);
        // changes included, so new customer infos will be null
        newCustomerInfos = null;
        customerAdapter.notifyDataSetChanged();
    }

    public void onBackPressed() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayCustomerInfo.this);
        mBuilder.setMessage("Do you want to close your restaurant check in list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(false);
        AlertDialog alert = mBuilder.create();
        alert.show();
    }
}
