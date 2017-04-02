package com.example.d0l1.socialeventplanner.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.v4.view.GestureDetectorCompat;

import com.example.d0l1.socialeventplanner.Controller.create_new_event_Controller;
import com.example.d0l1.socialeventplanner.R;

public class create_new_event extends AppCompatActivity{


    private GestureDetectorCompat detectGestures;
    final String TAG="Fahad";
    boolean isWeeklyView=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.create_new_event);
            //Handle things coming from the weekly view
            if (getIntent().getExtras() != null && getIntent().getExtras().get("Weekly_View") != null)
            {
                //Get the time and the Date
                String time=(String)getIntent().getExtras().get("time");
                String startDate=(String)getIntent().getExtras().get("startDate");
                String endDate=(String)getIntent().getExtras().get("endDate");
                Log.d(TAG,"The time is: "+time);
                Log.d(TAG,"The date is: "+startDate);
                isWeeklyView=true;
                //Set the time and the date to the fields
                TextView timeTextView=(TextView) findViewById(R.id.startTimeTextView);
                TextView DateTextView=(TextView) findViewById(R.id.startDateText);
                TextView endDateTextView=(TextView) findViewById(R.id.endDateText);
                timeTextView.setText(time);
                DateTextView.setText(startDate);
                endDateTextView.setText(endDate);

            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        findViewById(R.id.forwardButton).setOnClickListener(new create_new_event_Controller(this,isWeeklyView));
    }





}
