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
    String Json_string;

    public clientClass(URI uri){
        super(uri);
    }
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        Log.i("Websocket", "Opened");
        send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
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

    public void sendMessage1(String name){
        try {
            JSONObject Obj = new JSONObject();
            Obj.put("Name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
