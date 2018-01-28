package com.shhridoy.moviereview;

/**
 * Created by Dream Land on 1/22/2018.
 */

public class Data {

    int id;
    String name;
    float rate;

    public Data(int id, String name, float rate) {
        this.id = id;
        this.name = name;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRate() {
        return rate;
    }
}
