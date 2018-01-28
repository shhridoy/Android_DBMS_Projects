package com.shhridoy.simplecontactdirectory;

/**
 * Created by Dream Land on 1/17/2018.
 */

public class ListViewData {

    private int id;
    private String name;
    private String number;

    public ListViewData(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
