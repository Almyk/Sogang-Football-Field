package com.example.almyk.sogangfootballfield;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    // views
    private CompactCalendarView mCompactCalendarView;
    private TextView mTitleTextView;
    private ListView mEventListView;
    private ConstraintLayout mNamesTimesLayout;

    private FloatingActionButton mAddEventFAB;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private Date mDateClicked = new Date();

    // firebase members
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEventsDatabaseRef;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Booking> mEventList = new ArrayList<>();
    private EventAdapter mEventAdapter;

    private String mUsername;

    private final static String DEFAULT_NAME = "Anon";
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = DEFAULT_NAME;

        // find views
        mCompactCalendarView = findViewById(R.id.ccv_calendar);
        mTitleTextView = findViewById(R.id.tv_title);
        mEventListView = findViewById(R.id.lv_events);
        mNamesTimesLayout = findViewById(R.id.cl_names_n_times);

        mAddEventFAB = findViewById(R.id.fab_add_event);

        mEventAdapter = new EventAdapter(this, R.layout.event_item, mEventList);

        // Firebase init
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEventsDatabaseRef = mFirebaseDatabase.getReference("events");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mEventListView.setAdapter(mEventAdapter);

        mTitleTextView.setText(dateFormatForMonth.format(mCompactCalendarView.getFirstDayOfCurrentMonth()));

        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                mTitleTextView.setText(dateFormatForMonth.format(dateClicked));
                mDateClicked = dateClicked;
                Log.v(TAG, "Date clicked: " + dateFormat.format(dateClicked));
                updateEventListView();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mTitleTextView.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        mAddEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // is signed in
                    signedInInit(user.getDisplayName());
                } else {
                    // not signed in
                    signedOutCleanup();
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
                }
            }
        };
    }

    private void signedInInit(String displayName) {
        mUsername = displayName;
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    EventForFirebase eventForFirebase = dataSnapshot.getValue(EventForFirebase.class);
                    Event event = new Event(eventForFirebase.getColor(), eventForFirebase.getTimeInMillis(), eventForFirebase.getData());
                    mCompactCalendarView.addEvent(event);
                    updateEventListView();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mEventsDatabaseRef.addChildEventListener(mChildEventListener);
        }
    }

    private void signedOutCleanup() {
        mUsername = DEFAULT_NAME;
        mEventAdapter.clear();
        if(mChildEventListener != null){
            mEventsDatabaseRef.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO : attach authStateListener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void updateEventListView() {
        List<Event> eventList = mCompactCalendarView.getEvents(mDateClicked);
        mEventList.clear();
        if(eventList.isEmpty()){
            mNamesTimesLayout.setVisibility(View.GONE);
        }
        else {
            mNamesTimesLayout.setVisibility(View.VISIBLE);
        }
        for(Event event : eventList){
            mEventAdapter.add((Booking) event.getData());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mChildEventListener != null){
            mEventsDatabaseRef.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void showAddEventDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEvent addEvent = new AddEvent();
        addEvent.setAddEventListener(new AddEvent.AddEventListener() {
            @Override
            public void onSubmitEvent(String sTime, String eTime) {
                Booking booking = new Booking(mUsername, sTime, eTime);
                Event event = new Event(Color.CYAN, mDateClicked.getTime(), booking);
                EventForFirebase eventForFirebase = new EventForFirebase(event);
                mEventsDatabaseRef.push().setValue(eventForFirebase);
                mNamesTimesLayout.setVisibility(View.VISIBLE);
            }
        });
        addEvent.show(fragmentManager, "dialog_add_new_event");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RC_SIGN_IN:
                if(resultCode == RESULT_OK) {
                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                } else if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Sign in Cancelled.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
