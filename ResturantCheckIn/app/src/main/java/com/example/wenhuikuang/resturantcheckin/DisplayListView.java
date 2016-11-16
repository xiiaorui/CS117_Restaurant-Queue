package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DisplayListView extends AppCompatActivity implements ClientListener{

    private int mOpenRestaurantsID = -1;

    public DisplayListView() {
        if (Client.get() == null)
            Client.init(this, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list_view);

    }

    @Override
    public void onOpen() {
        mOpenRestaurantsID = Client.get().getOpenRestaurants();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(JSONObject resp) {
        int id = -1;
        try {
            id = resp.getInt("id");
        } catch (JSONException e) {
            // TODO
        }
        if (id == mOpenRestaurantsID) {
            // parse the results and then display them
        }
    }

    @Override
    public void onError(Exception e) {

    }
}
