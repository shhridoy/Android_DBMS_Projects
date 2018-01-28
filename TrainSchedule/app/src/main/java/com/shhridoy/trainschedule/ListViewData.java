package com.shhridoy.trainschedule;

/**
 * Created by Dream Land on 1/18/2018.
 */

public class ListViewData {

    private int id;
    private String train_name;
    private String out_time;
    private String in_time;
    private int gate_no;

    public ListViewData(int id, String train_name, String out_time, String in_time, int gate_no) {
        this.id = id;
        this.train_name = train_name;
        this.out_time = out_time;
        this.in_time = in_time;
        this.gate_no = gate_no;
    }

    public int getId() {
        return id;
    }

    public String getTrain_name() {
        return train_name;
    }

    public String getOut_time() {
        return out_time;
    }

    public String getIn_time() {
        return in_time;
    }

    public int getGate_no() {
        return gate_no;
    }
}
