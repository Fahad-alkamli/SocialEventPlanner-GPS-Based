package com.example.d0l1.socialeventplanner.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.d0l1.socialeventplanner.Controller.edit_event_Controller;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.R;

import java.io.Serializable;

public class edit_event extends AppCompatActivity implements Serializable {

    private Event event;
    private boolean Weekly_View=false;
    private GestureDetectorCompat detectGestures;
    final String TAG="Fahad";
    /*
    edit_event class will handle an edit to an existing event. It will also receive the event id from both the main window
    and the weekly view window
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.create_new_event);
         //   Log.d(TAG, "Create new Event Class");
            try {
                this.event =( Main.Engine.findEvent((String) getIntent().getExtras().get("event")));
                if(getIntent().getExtras().get("Weekly_View") != null)
                {
                   this.Weekly_View=(boolean) getIntent().getExtras().get("Weekly_View");
                }
                if (event != null) {
                    Log.d(TAG,"This is edit event with an id "+event.getID());
                    setValues();
                }else
                {
                    Intent i=new Intent(this,Main.class);
                    startActivity(i);
                }
            }catch(Exception en)
            {
                Log.d(TAG,en.getMessage());
            }

        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        findViewById(R.id.forwardButton).setOnClickListener(new edit_event_Controller(this,Weekly_View,event));

    }

    //This function will set the values back to the specific input fields as this activity is an update
    private void setValues()
    {
        TextView titleText=(TextView) findViewById(R.id.titleText);
        TextView startDateText=(TextView) findViewById(R.id.startDateText);
        TextView endDateText=(TextView) findViewById(R.id.endDateText);
        TextView venueText=(TextView) findViewById(R.id.venueText);
        TextView locationText=(TextView) findViewById(R.id.locationText);
        EditText startTime=(EditText) findViewById(R.id.startTimeTextView);
        EditText endTime=(EditText) findViewById(R.id.endTimeTextView);

        //Set values
        titleText.setText(event.getTitle());
        startDateText.setText(event.getStartDate());
        endDateText.setText(event.getEndDate());
        venueText.setText(event.getVenue());
        locationText.setText(event.getLocation());
         startTime.setText(event.getStartTime());
         endTime.setText(event.getEndTime());

    }


}
