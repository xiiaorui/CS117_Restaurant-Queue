package com.example.wenhuikuang.resturantcheckin;

/**
 * Created by wenhuikuang on 11/18/16.
 */

public class customerInfo {
    private String name;
    private int size;
    private int party_id;

    public customerInfo(int party_id, String name, int size){
        this.party_id = party_id;
        this.name = name;
        this.size = size;
    }

    public int getParty_id() {
        return party_id;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setParty_id(int party_id) {
        this.party_id = party_id;
    }
}
