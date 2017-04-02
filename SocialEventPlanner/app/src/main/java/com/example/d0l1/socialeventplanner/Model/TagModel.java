package com.example.d0l1.socialeventplanner.Model;


public class TagModel {
    private String timeCell;
    private String dateCell;
    private String eventID;
    /*
   This class will handle putting information to a View tag.
    I used it to identify a click on a view inside the weekly view activity.
    It will record important information about the View such as;
    timeCell position in the parent view, datecell position and the event ID
     */
    public TagModel()
    {

    }
    public String getTimeCell()
    {
        return this.timeCell;
    }
    public String getDateCell()
    {
        return this.dateCell;
    }
    public String getEventID()
    {
        return this.eventID;
    }
    public void setTimeCell(String timeCell)
    {
        this.timeCell=timeCell;

    }
    public void setDateCell(String dateCell)
    {
        this.dateCell=dateCell;

    }
    public void setEventIDString(String  eventID)
    {
        this.eventID=eventID;
    }

}
