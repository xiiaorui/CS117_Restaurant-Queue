package com.example.wenhuikuang.resturantcheckin;

import android.util.Log;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenhuikuang on 11/11/16.
 */

public class clientClass extends WebSocketClient {
    private final String TAG = "check";
    private static clientClass sClient;
    private ClientListener clientListener;
    String Json_string = "ss";
    private Integer mRequestID = 0;
    private Map<Integer, MessageType> mRequestTypeMap;

    private clientClass(URI uri, ClientListener clientListener){
        super(uri);
        this.clientListener = clientListener;
        mRequestTypeMap = new HashMap<>();
    }

    public synchronized void send(JSONObject obj) {
        Integer id = null;
        MessageType type = null;
        if (obj.has("id")) {
            try {
                id = obj.getInt("id");
            } catch (JSONException e) {
            }
        }
        if (obj.has("action")) {
            try {
                String action = obj.getString("action");
                type = getActionType(action);
            } catch (JSONException e) {
            }
        }
        if ((id != null) && (type != null)) {
            mRequestTypeMap.put(id, type);
        }
        send(obj.toString());
    }

    // get the type of message returned by server
    // resp should be any message/response from server
    // returns null if unable to get type
    // matches id with sent requests
    public synchronized MessageType getType(JSONObject resp) {
        if (resp.has("notification")) {
            String notification = null;
            try {
                notification = resp.getString("notification");
            } catch (JSONException e) {
                // cannot happen...
                e.printStackTrace();
                return null;
            }
            for (MessageType t : MessageType.values()) {
                if (!t.isAction() && t.getValue().equals(notification)) {
                    return t;
                }
            }
            return null;
        } else {
            Integer id = null;
            if (!resp.has("id")) {
                return null;
            }
            try {
                id = resp.getInt("id");
            } catch (JSONException e) {
                // should not happen...
                e.printStackTrace();
                return null;
            }
            MessageType type = mRequestTypeMap.remove(id);
            return type;
        }
    }

    private MessageType getActionType(String action) {
        MessageType type = null;
        for (MessageType t : MessageType.values()) {
            if (t.isAction() && (t.getValue().equals(action))) {
                type = t;
                break;
            }
        }
        return type;
    }

    @Override
    public void send(String message){
        Log.d(TAG, "send() message=" + message);
        super.send(message);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.d(TAG, "onOpen()");
        clientListener.onOpen();
    }

    public void setListener(ClientListener listener) {
        clientListener = listener;
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage() message=" + message);
        JSONObject resp = null;
        try {
            resp = new JSONObject(message);
        } catch (JSONException e) {
            // invalid JSON object, which should never happen...
        }
        try {
            clientListener.onMessage(resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose() code=" + code + " reason=" + reason + " remote=" + remote);
        clientListener.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError() e=" + e);
        clientListener.onError(e);
    }

    public void sendCustomerInfo(String name, int size, int restaurant_id) {
        JSONObject Obj = newRequest("queue");
        try{
            Obj.put("party_name",name);
            Obj.put("restaurant_id",restaurant_id);
            Obj.put("party_size",size);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        send(Obj);
    }
    public void getParties() {
        JSONObject Obj = newRequest("get_parties");
        try {
            Obj.put("num_parties",10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(Obj);
    }

    public void getRestaurant(){
        JSONObject Obj = newRequest("get_restaurants");
        send(Obj);
    }

    public void openRestaruant(int id) {
        JSONObject Obj = newRequest("open_restaurant");
        try {
            Obj.put("restaurant_id",id);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        send(Obj);
    }
    public void createRestaruant(String name) {
        JSONObject Obj = newRequest("create_restaurant");
        try {
            Obj.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        send(Obj);
    }

    public void leaveQueue() {
        JSONObject object = newRequest("leave_queue");
        send(object);
    }

    private JSONObject newRequest(String action) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", getNewRequestID());
            obj.put("action", action);
        } catch (JSONException e) {
            // Won't happen...
        }
        return obj;
    }

    private int getNewRequestID() {
        int id = mRequestID;
        synchronized (mRequestID) {
            mRequestID += 2;
        }
        return id;
    }

    public static void init(ClientListener listener, boolean isCustomer) {
        if (sClient != null) {
            // TODO properly handle this logic error
            throw new RuntimeException("");
        }
        String uriStr = "ws://159.203.248.21/";
        if (isCustomer)
            uriStr = uriStr + "customer";
        else
            uriStr = uriStr + "restaurant";
        uriStr = uriStr + ":80";
        try {
            URI uri = new URI(uriStr);
            sClient = new clientClass(uri, listener);
            sClient.connect();
        } catch (URISyntaxException e) {
            // TODO should never happen...
            e.printStackTrace();
        }
    }

    public static clientClass get() {
        return sClient;
    }

}
