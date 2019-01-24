package com.example.almyk.sogangfootballfield;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Booking> {
    public EventAdapter(Context context, int resource, List<Booking> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.event_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.tv_name);
        TextView startTime = convertView.findViewById(R.id.tv_start_time);
        TextView endTime = convertView.findViewById(R.id.tv_end_time);

        Booking booking = getItem(position);

        name.setText(booking.getName());
        startTime.setText(booking.getStartTime());
        endTime.setText(booking.getEndTime());

        return convertView;
    }
}
