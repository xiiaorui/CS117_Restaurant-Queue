package com.example.wenhuikuang.resturantcheckin;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;


public class Client extends WebSocketClient {

    private static Client sClient = null;
    private ClientListener mListener;
    private int mID = 0;

    public void setListener(ClientListener listener) {
        mListener = listener;
    }

    public int getOpenRestaurants() {
        JSONObject req = new JSONObject();
        int id = getNewID();
        try {
            req.put("id", id);
            req.put("action", "get_restaurants");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(req.toString());
        return id;
    }

    private int getNewID() {
        int id = mID;
        mID += 2;
        return id;
    }

    private Client(URI uri, ClientListener listener) {
        super(uri);
        mListener = listener;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        mListener.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception e) {
        mListener.onError(e);
    }

    @Override
    public void onMessage(String message) {
        JSONObject resp = null;
        try {
            resp = new JSONObject(message);
        } catch (JSONException e) {
            // invalid JSON object, which should never happen...
        }
        mListener.onMessage(resp);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        mListener.onOpen();
    }

    public static void init(ClientListener listener, boolean isCustomer) {
        if (sClient != null) {
            // TODO properly handle this logic error
            throw new RuntimeException("");
        }
        String uriStr = "ws://localhost/";
        if (isCustomer)
            uriStr = uriStr + "customer";
        else
            uriStr = uriStr + "restaurant";
        uriStr = uriStr + ":80";
        try {
            URI uri = new URI(uriStr);
            sClient = new Client(uri, listener);
            sClient.connect();
        } catch (URISyntaxException e) {
            // TODO should never happen...
            e.printStackTrace();
        }
    }

    public static Client get() {
        return sClient;
    }
}
