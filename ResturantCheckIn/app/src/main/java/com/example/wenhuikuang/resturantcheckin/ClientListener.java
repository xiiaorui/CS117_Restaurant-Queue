package com.example.wenhuikuang.resturantcheckin;

/**
 * Created by wenhuikuang on 11/17/16.
 */

import org.json.JSONException;
import org.json.JSONObject;

public interface ClientListener {

    public void onOpen();
    public void onClose(int code, String reason, boolean remote);
    public void onMessage(JSONObject resp) throws JSONException;
    public void onError(Exception e);

}
