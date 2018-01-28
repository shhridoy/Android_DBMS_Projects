package com.shhridoy.agecalculator;

/**
 * Created by Dream Land on 1/22/2018.
 */

public class Data {

    int id;
    String name;
    String dofbirth;
    int age;

    public Data(int id, String name, String dofbirth, int age) {
        this.id = id;
        this.name = name;
        this.dofbirth = dofbirth;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDofbirth() {
        return dofbirth;
    }

    public int getAge() {
        return age;
    }
}