package com.example.d0l1.socialeventplanner.View;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.d0l1.socialeventplanner.Controller.WeeklyView_Controller;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.Model.TagModel;
import com.example.d0l1.socialeventplanner.R;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Weekly_View extends AppCompatActivity implements Serializable {
    private  ArrayList<Event> eventsList;

    final String TAG="Fahad";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.weekly__view);
            //This function will set the next 7 days to the days header
            setTheNext7Days();
            GridLayout layout = (GridLayout) findViewById(R.id.weeklyViewLayout);
            int ChildrenCount = layout.getChildCount();
            int i = 15;
            int columnID = 0;
            //Loop through all the cells and create onclick event listener and give it a tag that contains the TimeRow and date column
            for (; i < ChildrenCount; i++)
            {
                TextView test = (TextView) layout.getChildAt(i);
                if (test.getText().toString().contains("AM") == false && test.getText().toString().contains("PM") == false)
                {
                    test.setBackgroundResource(R.drawable.cell_style);
                    TagModel tag = new TagModel();
                    tag.setTimeCell(Integer.toString(i));
                    tag.setDateCell(Integer.toString(columnID));
                    test.setTag(tag);

                    test.setOnClickListener(new WeeklyView_Controller((GridLayout) findViewById(R.id.weeklyViewLayout)));
                    columnID++;
                    if (columnID > 6)
                    {
                        columnID = 0;
                    }
                }
            }
            //restore already created events to the weekly view grid
            if ( Main.Engine.getEventsList().size() > 0) {
                eventsList =  Main.Engine.getEventsList();
                boolean foundView = false;
                for (Event event : eventsList) {
                    String time = event.getStartTime();
                    String period = "";
                    if (time.toLowerCase().trim().contains("am")) {
                        period = "AM";
                    } else {
                        period = "PM";
                    }
                    Calendar now = Calendar.getInstance();
                    if ((now.get(Calendar.MONTH) + 1) == Integer.parseInt(event.getStartDate().split("/")[1])  || checkLastMonth(layout)==true) {
                        findViewAndSetText(time.split(":")[0] + " " + period, event.getStartDate().split("/")[0], event.getTitle(), event);
                        foundView = true;
                    }
                }
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
    }

    //This function will try to find the time based on a Tag containing  the position
    private String getTime(int position)
    {
        try {
            if (position >= 15 && position <= 21) {
                return "12 AM";
            } else if (position >= 23 && position <= 29) {
                return "1 AM";
            } else if (position >= 31 && position <= 37) {
                return "2 AM";
            } else if (position >= 39 && position <= 45) {
                return "3 AM";
            } else if (position >= 47 && position <= 53) {
                return "4 AM";
            } else if (position >= 55 && position <= 61) {
                return "5 AM";
            } else if (position >= 63 && position <= 69) {
                return "6 AM";
            } else if (position >= 71 && position <= 77) {
                return "7 AM";
            } else if (position >= 79 && position <= 85) {
                return "8 AM";
            } else if (position >= 87 && position <= 93) {
                return "9 AM";
            } else if (position >= 95 && position <= 101) {
                return "10 AM";
            } else if (position >= 103 && position <= 109) {
                return "11 AM";
            } else if (position >= 111 && position <= 117) {
                return "12 PM";
            } else if (position >= 119 && position <= 125) {
                return "1 PM";
            } else if (position >= 127 && position <= 133) {
                return "2 PM";
            } else if (position >= 135 && position <= 141) {
                return "3 PM";
            } else if (position >= 143 && position <= 149) {
                return "4 PM";
            } else if (position >= 151 && position <= 157) {
                return "5 PM";
            } else if (position >= 159 && position <= 165) {
                return "6 PM";
            } else if (position >= 167 && position <= 173) {
                return "7 PM";
            } else if (position >= 175 && position <= 181) {
                return "8 PM";
            } else if (position >= 183 && position <= 189) {
                return "9 PM";
            } else if (position >= 191 && position <= 197) {
                return "10 PM";
            } else if (position >= 199 && position <= 205) {
                return "11 PM";
                //200
            }
            Log.d(TAG, "Error at getTime: " + Integer.toString(position));
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
return "No Time";

    }
    //This function will try to find the date based on a Tag containing  the position
    private String getDate(int pos)
    {
        try {

            GridLayout layout = (GridLayout) findViewById(R.id.weeklyViewLayout);
            TextView day = (TextView) layout.getChildAt(pos);
            return (day.getText().toString());
        }catch(Exception e)
        {
            Log.d(TAG,"Error at : getDate "+e.getMessage());
        }

        return "";
    }

    //This function will loop through the array of events and find the cell that matches the date and time and change it's title
    private void findViewAndSetText(String time , String date, String eventTitle,Event event)
    {
        try {
            //Just in case the user types the day something like this 02 instead of 2
            date=Integer.toString(Integer.parseInt(date));
            String oldTime=time;
            time=Integer.toString(Integer.parseInt(time.split(" ")[0]));
            time+=" "+oldTime.split(" ")[1];
            //Log.d(TAG,"Check this: "+time);
            GridLayout layout = (GridLayout) findViewById(R.id.weeklyViewLayout);
            int count = layout.getChildCount();
            for (int i = 15; i < count; i++) {
                TextView view = (TextView) layout.getChildAt(i);
                if(view.getTag() != null)
                {
               // String time2 = getTime(Integer.parseInt(view.getTag().toString().split(",")[0]));
                    String time2 = getTime(Integer.parseInt(((TagModel)view.getTag()).getTimeCell()));
                //String date2 = getDate(Integer.parseInt(view.getTag().toString().split(",")[1]));
                    String date2 = getDate(Integer.parseInt(((TagModel)view.getTag()).getDateCell()));
                //Validate the given time is equal to the date and time for the View
                if(time2 != null && date2 != null)
                {
                    if (time.trim().toLowerCase().equals(time2.trim().toLowerCase()) && date.trim().toLowerCase().equals(date2.trim().toLowerCase())) {
                        view.setText(eventTitle);
                        view.setBackgroundColor(getColor(R.color.colorPrimary));
                        view.setTextColor(getColor(R.color.white));
                        view.setGravity(Gravity.CENTER_HORIZONTAL);
                        view.setTypeface(null, Typeface.BOLD);

                        //Get the tag first
                        TagModel tag=(TagModel) view.getTag();
                        tag.setEventIDString(event.getID());
                        view.setTag(tag);
                        Log.d(TAG,"Found the view!");
                        return;
                    }
                }

                }

            }
        }catch(Exception e)
        {
            Log.d(TAG,"Error at findViewAndSetText: " +e.getMessage());
        }
            //Log.d(TAG,"Couldn't find the view with this date and time");
    }

    //This function is simply getting the next day for a given date
    private int getNextDay(Date startDate,int count)
    {
        try {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(startDate);
            cal2.add(Calendar.DATE, count);
            DateFormat dateFormat = new SimpleDateFormat("dd");
            return Integer.parseInt(dateFormat.format(cal2.getTime()));
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        return 0;
    }

    //This function will find the date for this week starting from sunday and ending in saturday and will set the dates accordingly.
    private void setTheNext7Days()
    {
        try {
            ArrayList<Integer> weekDays = new ArrayList<Integer>();
            //http://stackoverflow.com/questions/7645178/getting-the-start-and-the-end-date-of-a-week-using-java-calendar-class
            Calendar cal = Calendar.getInstance();
            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(cal.getTimeInMillis());

            int dayOfWeek = startCal.get(Calendar.DAY_OF_WEEK);
            startCal.set(Calendar.DAY_OF_MONTH, (startCal.get(Calendar.DAY_OF_MONTH) - dayOfWeek) + 1);
            //First include the start day of the week
            DateFormat dateFormat = new SimpleDateFormat("dd");
            weekDays.add(Integer.parseInt(dateFormat.format(startCal.getTime())));
            for (int i = 1; i <= 5; i++) {
                weekDays.add(getNextDay(startCal.getTime(), i));
            }

            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(cal.getTimeInMillis());

            dayOfWeek = endCal.get(Calendar.DAY_OF_WEEK);
            endCal.set(Calendar.DAY_OF_MONTH, endCal.get(Calendar.DAY_OF_MONTH)
                    + (7 - dayOfWeek));

            //include the last day
            weekDays.add(Integer.parseInt(dateFormat.format(endCal.getTime())));


            //print a list of the week
            int b = 0;
            GridLayout layout = (GridLayout) findViewById(R.id.weeklyViewLayout);
            for (int i : weekDays) {
                TextView view = (TextView) layout.getChildAt(b);
                b++;
                view.setText(Integer.toString(i));
                // Log.d(TAG,Integer.toString(i));
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
    }

    //This function will check if the the week contain days from the previous  month
    private boolean checkLastMonth(GridLayout layout)
    {
        //If one of them is bigger than the last day in the week then that thing is part of last month days
        TextView LastDay=(TextView) layout.getChildAt(6);
        for(int i=0;i<6;i++)
        {
            TextView day=(TextView) layout.getChildAt(i);
            if(Integer.parseInt(day.getText().toString())>Integer.parseInt(LastDay.getText().toString()))
            {
                return true;
            }
        }
                return false;
    }

}
