package com.example.wenhuikuang.resturantcheckin;

import org.json.JSONObject;

public interface ClientListener {

    public void onOpen();
    public void onClose(int code, String reason, boolean remote);
    public void onMessage(JSONObject resp);
    public void onError(Exception e);

}
