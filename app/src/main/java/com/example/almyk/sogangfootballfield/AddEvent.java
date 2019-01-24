package com.example.almyk.sogangfootballfield;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEvent extends DialogFragment {

    private TimePicker mTimePicker;
    private Button mSubmitButton;
    private Button mCancelButton;
    private AddEventListener mListener;

    public interface AddEventListener {
        void onSubmitEvent(String sTime, String eTime);
    }

    public void setAddEventListener(AddEventListener listener){
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_event, container);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimePicker = view.findViewById(R.id.tp_event_time);
        mSubmitButton = view.findViewById(R.id.btn_submit);
        mCancelButton = view.findViewById(R.id.btn_cancel);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = mTimePicker.getHour();
                int minute = mTimePicker.getMinute();
                String sTime = new String(createTimeString(hour, minute));
                String eTime = new String(createTimeString(hour+2, minute));

                Toast.makeText(view.getContext(), "Request sent!", Toast.LENGTH_SHORT).show();
                mListener.onSubmitEvent(sTime, eTime);
                dismiss();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEvent.this.getDialog().cancel();
            }
        });
    }

    private String createTimeString(int hour, int minute) {
        String string;
        String sHour = Integer.toString(hour);
        String sMinute = Integer.toString(minute);
        if(sHour.length() == 1) {
            sHour = '0' + sHour;
        }
        if(sMinute.length() == 1) {
            if(minute == 0) {
                sMinute = sMinute + '0';
            } else {
                sMinute = '0' + sMinute;
            }
        }
        string = sHour + ":" + sMinute;
        return string;
    }
}
