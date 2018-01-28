package com.shhridoy.bustrainschedule;

/**
 * Created by Dream Land on 1/18/2018.
 */

public class ListViewData {

    private int id;
    private String vehicle_name;
    private String date;
    private String time;

    public ListViewData(int id, String vehicle_name, String date, String time) {
        this.id = id;
        this.vehicle_name = vehicle_name;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
