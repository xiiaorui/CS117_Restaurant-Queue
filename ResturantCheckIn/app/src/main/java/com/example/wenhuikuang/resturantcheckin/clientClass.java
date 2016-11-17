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
    private static clientClass sclientclass;
    String Json_string;
    int RequestId = 0;

    public clientClass(URI uri){
        super(uri);
    }
    public static clientClass getInstance(URI uri)
    {
        if (sclientclass == null)
            sclientclass = new clientClass(uri);
        return sclientclass;
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i("Websocket", "Opened");
//        send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public void onMessage(String s) {
        Json_string = s;
//        if (Json_string == null)
//            Toast.makeText(getApplicationContext(),"First Get JSON", Toast.LENGTH_LONG).show();
//        else{
//            Intent intent = new Intent(getApplicationContext(), DisplayListView.class);
//            intent.putExtra("Json data", Json_string);
//            startActivity(intent);
//        }
    }

    public String responeMessage(){
        return Json_string;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        Log.i("Websocket", "Closed " + s);
    }

    @Override
    public void onError(Exception e) {
        Log.i("Websocket", "Error " + e.getMessage());
    }
    public void sendMessage(String name, String size) {
        try{
            JSONObject Obj = new JSONObject();
            Obj.put("Name",name);
            Obj.put("Size",size);
            send(Obj.toString());
        }
        catch (JSONException e){
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
}
