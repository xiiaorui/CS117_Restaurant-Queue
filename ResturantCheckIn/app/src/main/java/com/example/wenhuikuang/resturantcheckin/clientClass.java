package com.example.wenhuikuang.resturantcheckin;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.net.URI;
import java.net.URISyntaxException;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wenhuikuang on 11/11/16.
 */

public class clientClass extends WebSocketClient {
    private final String TAG = "check";
    private static clientClass sclientclass;
    private ClientListener clientListener;
    String Json_string = "ss";
    int RequestId = 0;

    private clientClass(URI uri, ClientListener clientListener){
        super(uri);
        this.clientListener = clientListener;
    }
    public static clientClass getInstance(URI uri)
    {
        return sclientclass;
    }
    @Override
    public void send(String message){
        Log.d(TAG,message);
        super.send(message);
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        clientListener.onOpen();
    }
    public void setListener(ClientListener listener) {
        clientListener = listener;
    }
    @Override
    public void onMessage(String message) {
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
        clientListener.onClose(code, reason, remote);
    }

    @Override
    public void onError(Exception e) {
        clientListener.onError(e);
    }
    public void sendCustomerInfo(String name, int size, int restaurant_id) {
        try{
            JSONObject Obj = new JSONObject();
            Obj.put("party_name",name);
            Obj.put("restaurant_id",restaurant_id);
            Obj.put("action","queue");
            Obj.put("party_size",size);
            Obj.put("id",RequestId);
            RequestId += 2;
            send(Obj.toString());
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getParties(){
        try {
            JSONObject Obj = new JSONObject();
            Obj.put("action", "get_parties");
            Obj.put("id",RequestId);
            Obj.put("num_parties",10);
            RequestId += 2;
            send(Obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private int getRequestId(){
        return RequestId;
    }
    public void getRestaurant(){
        try {
            JSONObject Obj = new JSONObject();
            Obj.put("action", "get_restaurants");
            Obj.put("id",RequestId);
            RequestId += 2;
            send(Obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void openRestaruant(int id){
        try{
            JSONObject Obj = new JSONObject();
            Obj.put("restaurant_id",id);
            Obj.put("action","open_restaurant");
            Obj.put("id",RequestId);
            RequestId += 2;
            send(Obj.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void createRestaruant(String name){
        try {
            JSONObject Obj = new JSONObject();
            Obj.put("name", name);
            Obj.put("action","create_restaurant");
            Obj.put("id",RequestId);
            RequestId += 2;
            send(Obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void leaveQueue(){
        JSONObject object = new JSONObject();
        try {
            object.put("action","leave_queue");
            object.put("id",RequestId);
            RequestId += 2;
            send(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void init(ClientListener listener, boolean isCustomer) {
        if (sclientclass != null) {
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
            sclientclass = new clientClass(uri, listener);
            sclientclass.connect();
        } catch (URISyntaxException e) {
            // TODO should never happen...
            e.printStackTrace();
        }
    }
    public static clientClass get()
    {
        return sclientclass;
    }

}
