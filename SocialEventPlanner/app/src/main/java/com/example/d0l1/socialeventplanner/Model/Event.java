package com.example.d0l1.socialeventplanner.Model;


import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Event_Interface {
    private String ID;
    private String title;
    private String startDate;
    private String endDate;
    private String venue;
    private String location;
    private String note;
    private String startTime;
    private String endTime;
    private String voiceNotePath;
    private String[] attendees;


    final String TAG="Fahad";

    @Override
    public String getVoiceNotePath() {
        return voiceNotePath;
    }

    @Override
    public void setVoiceNotePath(String voiceNotePath) {
        this.voiceNotePath = voiceNotePath;
    }

    public Event(String ID,String title,String startDate,String endDate, String venue, String location,String note,String startTime,String endTime, String[] attendees )
    {
        this.ID=ID;
        this.title=title;
        this.startDate=startDate;
        this.endDate=endDate;
        this.venue=venue;
        this.location=location;
        this.note=note;
        this.attendees=attendees;
        this.startTime=startTime;
        this.endTime=endTime;
        //Log.d(TAG,Integer.toString(attendees.length));

    }
    public Event()
    {
        this.ID="";
        this.title="";
        this.startDate="";
        this.endDate="";
        this.venue="";
        this.location="";
        this.note="";
        this.attendees=null;
    }
    @Override
    public String[] getAttendees() {
        return attendees;
    }

    @Override
    public void setAttendees(String[] attendees) {
        this.attendees = attendees;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getVenue() {
        return venue;
    }

    @Override
    public void setVenue(String venue) {
        this.venue = venue;
    }

    @Override
    public String getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }


    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(Event event) {

        DateFormat dateFormat=new SimpleDateFormat("dd/mm/yyyy");
        try {
            Date date1 = dateFormat.parse(getStartDate());
            Date date2 = dateFormat.parse(event.getStartDate());
            return date1.compareTo(date2);
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }

    return 0;
    }

}

