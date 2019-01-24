package com.example.almyk.sogangfootballfield;

public class Booking {
    private String mName;
    private String mStartTime;
    private String mEndTime;

    public Booking() {
    }

    public Booking(String name, String startTime, String endTime) {
        mName = name;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }
}
