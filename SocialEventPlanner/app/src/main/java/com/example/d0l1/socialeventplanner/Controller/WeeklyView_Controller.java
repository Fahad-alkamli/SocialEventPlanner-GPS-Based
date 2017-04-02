package com.example.d0l1.socialeventplanner.Controller;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.Model.TagModel;
import com.example.d0l1.socialeventplanner.View.Main;
import com.example.d0l1.socialeventplanner.View.create_new_event;
import com.example.d0l1.socialeventplanner.View.edit_event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
public class WeeklyView_Controller implements View.OnClickListener {
    String TAG="Fahad";
    View view;
    GridLayout weeklyGridLayout;

    public WeeklyView_Controller(GridLayout weeklyGridLayout)
    {
        this.weeklyGridLayout = weeklyGridLayout;
    }


    @Override
    public void onClick(View view) {
        TextView eventTextView = (TextView) view;
        handleOnClickView(eventTextView);
        this.view=view;
    }

    //This function will handle a click on the cell in the weekly view to create or edit an event on the specific date and time
    private void handleOnClickView(TextView eventTextView)
    {
        Calendar now = Calendar.getInstance();
        if (eventTextView.getText().toString().trim().replace(" ", "").length() != 0) {
            handleUpdateAnEvent(eventTextView);

        } else {
            Intent i = new Intent(eventTextView.getContext(), create_new_event.class);
            i.putExtra("Weekly_View", true);
            //String time = getTime(Integer.parseInt(eventTextView.getTag().toString()));
            String time = getTime(Integer.parseInt(((TagModel) eventTextView.getTag()).getTimeCell().toString()));
            time = time.split(" ")[0] + ":00" + " " + time.split(" ")[1];
            i.putExtra("time", time);

            String startDate = getDate(Integer.parseInt(((TagModel) eventTextView.getTag()).getDateCell())) + "/" +
                    Integer.toString((now.get(Calendar.MONTH) + 1)) + "/" + Integer.toString(now.get(Calendar.YEAR));

            String endDate = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

            Calendar cal2 = Calendar.getInstance();
            try {
                cal2.setTime(dateFormat.parse(startDate));
                cal2.add(Calendar.DATE, 1);
                endDate = dateFormat.format(cal2.getTime());
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            if (Integer.parseInt(startDate.split("/")[0]) > Integer.parseInt(endDate.split("/")[0])) {
                //This is a problem the start date is the month -1
                int day = Integer.parseInt(startDate.split("/")[0]);
                int month = Integer.parseInt(startDate.split("/")[1]);
                int year = Integer.parseInt(startDate.split("/")[2]);
                if ((month - 1) == 0) {
                    //- year
                    year = year - 1;
                } else {
                    month = month - 1;
                }
                //let's put the start date back together
                startDate = Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
            }
            i.putExtra("startDate", startDate);
            i.putExtra("endDate", endDate);
            eventTextView.getContext().startActivity(i);
        }

    }

    //As the name describes this function will handle an update to an event existing in the weekly view
    private void handleUpdateAnEvent(TextView event)
    {
        try {

            if(event.getTag() != null)
            {
                String eventID = ((TagModel) event.getTag()).getEventID();
                Log.d(TAG, eventID);

                for(Event tempEvent: Main.Engine.getEventsList())
                {
                    if(tempEvent.getID().equals(eventID))
                    {
                        Intent i=new Intent(event.getContext(),edit_event.class);
                        i.putExtra("event",eventID);
                        i.putExtra("Weekly_View",true);
                        event.getContext().startActivity(i);
                    }
                }
            }

            //try to find the event ID
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
            GridLayout layout = weeklyGridLayout;
            TextView day = (TextView) layout.getChildAt(pos);
            return (day.getText().toString());
        }catch(Exception e)
        {
            Log.d(TAG,"Error at : getDate,  "+e.getMessage());
        }

        return "";
    }


}
