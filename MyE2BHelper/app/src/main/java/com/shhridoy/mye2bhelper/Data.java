package com.shhridoy.mye2bhelper;

/**
 * Created by Dream Land on 1/28/2018.
 */

public class Data {

    int id;
    String english;
    String bangla;

    public Data(int id, String english, String bangla) {
        this.id = id;
        this.english = english;
        this.bangla = bangla;
    }

    public int getId() {
        return id;
    }

    public String getEnglish() {
        return english;
    }

    public String getBangla() {
        return bangla;
    }
}
