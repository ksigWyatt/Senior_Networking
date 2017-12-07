package com.sn.stepcounter.stepcounter;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by yjj781265 on 11/28/2017.
 */

public class StepData implements Comparable<StepData> {
    private float day,stepsCount;
    private String date;

    // empty constructor is needed for firebase database

    public StepData() {
    }


    public StepData(float day, float stepsCount, String date) {
        this.day = day;
        this.stepsCount = stepsCount;
        this.date = date;
    }

    public float getDay() {
        return day;
    }

    public void setDay(float day) {
        this.day = day;
    }

    public float getStepsCount() {
        return stepsCount;
    }

    public void setStepsCount(float stepsCount) {
        this.stepsCount = stepsCount;
    }

    public String  getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(@NonNull StepData stepData) {
        return Float.compare(day,stepData.getDay());
    }
}
