package com.sn.stepcounter.stepcounter;

/**
 * Created by LoLJay on 4/9/2018.
 */

public class TraceDialog {
    private String id;
    private long timeStamp;
    private String distance;
    private int steps;


    public TraceDialog(String id, long timeStamp, String distance, int steps) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.distance = distance;
        this.steps = steps;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
