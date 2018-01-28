package com.shhridoy.cgpacounter;

/**
 * Created by Dream Land on 1/22/2018.
 */

public class Data {

    int id;
    String name;
    String stud_id;
    float cgpa;

    public Data(int id, String name, String stud_id, float cgpa) {
        this.id = id;
        this.name = name;
        this.stud_id = stud_id;
        this.cgpa = cgpa;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStud_id() {
        return stud_id;
    }

    public float getCgpa() {
        return cgpa;
    }
}