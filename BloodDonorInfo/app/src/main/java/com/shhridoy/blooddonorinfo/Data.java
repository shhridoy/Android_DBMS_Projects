package com.shhridoy.blooddonorinfo;

/**
 * Created by Dream Land on 1/21/2018.
 */

public class Data {

    int id;
    String name;
    String group;
    int age;
    String contact;

    public Data(int id, String name, String group, int age, String contact) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.age = age;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public int getAge() {
        return age;
    }

    public String getContact() {
        return contact;
    }
}
