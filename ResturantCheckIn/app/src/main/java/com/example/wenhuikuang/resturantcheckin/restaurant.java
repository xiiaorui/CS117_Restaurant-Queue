package com.example.wenhuikuang.resturantcheckin;

/**
 * Created by wenhuikuang on 11/16/16.
 */

public class restaurant {
    private String name;
    private int id;

    public restaurant(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
