package com.example.almyk.sogangfootballfield;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    // views
    private CompactCalendarView mCompactCalendarView;
    private TextView mTitleTextView;

    private FloatingActionButton mAddEventFAB;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private Date mDateClicked = new Date();

    // firebase members
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        mCompactCalendarView = findViewById(R.id.ccv_calendar);
        mTitleTextView = findViewById(R.id.tv_title);

        mAddEventFAB = findViewById(R.id.fab_add_event);

        // Firebase init
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseRef = mFirebaseDatabase.getReference("events");

        mTitleTextView.setText(dateFormatForMonth.format(mCompactCalendarView.getFirstDayOfCurrentMonth()));
        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                mTitleTextView.setText(dateFormatForMonth.format(dateClicked));
                mDateClicked = dateClicked;
                Log.v(TAG, "Date clicked: " + dateFormat.format(dateClicked));
                Toast.makeText(getApplicationContext(), "Clicked on: " + dateFormat.format(dateClicked), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mTitleTextView.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        mAddEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = mEventsDatabaseRef.child(dateFormat.format(mDateClicked));
                Toast.makeText(getApplicationContext(), databaseReference.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
