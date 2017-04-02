package com.example.d0l1.socialeventplanner.Controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Model.Common;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.createEventPhase2;

public class create_new_event_Controller implements View.OnClickListener{

    Activity activity;
    boolean isWeeklyView;

    public create_new_event_Controller(Activity activity, boolean isWeeklyView)
    {
        this.activity = activity;
        this.isWeeklyView=isWeeklyView;
    }

    //This function will go to phase2 to complete the event, it will also validate input fields
    @Override
    public void onClick(View view)
    {
        try {
            //Check to make sure the date is in a valid format
            EditText startDate = (EditText)  this.activity.findViewById(R.id.startDateText);
            EditText endDate = (EditText)  this.activity.findViewById(R.id.endDateText);
            EditText title = (EditText)  this.activity.findViewById(R.id.titleText);
            EditText venue = (EditText)  this.activity.findViewById(R.id.venueText);
            EditText location = (EditText)  this.activity.findViewById(R.id.locationText);
            EditText startTime=(EditText)  this.activity.findViewById(R.id.startTimeTextView);
            EditText endTime=(EditText)  this.activity.findViewById(R.id.endTimeTextView);

            //Validate the formats
            boolean validStartDate = Common.validateDate(startDate.getText().toString());
            boolean ValidateEndDate = Common.validateDate(endDate.getText().toString());
            boolean validateTimes= Common.ValidateTime(startTime.getText().toString(),endTime.getText().toString(),this.activity);

            if (validStartDate == true && ValidateEndDate == true && validateTimes==true)
            {
                // Log.d(TAG,"Here");
                boolean endDateState = Common.compareDates(startDate.getText().toString(), endDate.getText().toString());
                if (endDateState) {

                    Intent i = new Intent( this.activity, createEventPhase2.class);
                    i.putExtra("title", title.getText().toString());
                    i.putExtra("startDate", startDate.getText().toString());
                    i.putExtra("endDate", endDate.getText().toString());
                    i.putExtra("startDate", startDate.getText().toString());
                    i.putExtra("venue", venue.getText().toString());
                    i.putExtra("location", location.getText().toString());


                    i.putExtra("startTime", startTime.getText().toString());
                    i.putExtra("endTime", endTime.getText().toString());
                    i.putExtra("isWeeklyView",isWeeklyView);
                    // Log.d(TAG,"Here 6");
                    this.activity.startActivity(i);
                } else
                {
                    //  Log.d(TAG,"Here 4");
                    //The end date is not before the start date
                    Toast.makeText(this.activity, "Error: The endDate should be after the start date", Toast.LENGTH_LONG).show();
                    return;
                }

            } else
            {
                //Format error
                // Log.d(TAG,"Here 5");
                Toast.makeText(this.activity, "Error: Check the Dates format", Toast.LENGTH_LONG).show();
                return;
            }
        }catch(Exception e)
        {
            Log.d("Fahad",e.getMessage());

        }

    }
}
