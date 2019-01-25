package com.example.almyk.sogangfootballfield;

import android.support.annotation.Nullable;

import com.github.sundeepk.compactcalendarview.domain.Event;

public class EventForFirebase {
    private int color;
    private long timeInMillis;
    private Booking data;

    public EventForFirebase() {

    }

    public EventForFirebase(Event event) {
        color = event.getColor();
        timeInMillis = event.getTimeInMillis();
        data = (Booking) event.getData();
    }

    public int getColor() {
        return color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    @Nullable
    public Booking getData() {
        return data;
    }

}
