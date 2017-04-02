package com.example.d0l1.socialeventplanner.Model;

import java.io.Serializable;

public interface Event_Interface extends Serializable, Comparable<Event>{

    String getVoiceNotePath();

    void setVoiceNotePath(String voiceNotePath);

    String[] getAttendees();

    void setAttendees(String[] attendees);

    String getNote();

    void setNote(String note);

    String getLocation();

    void setLocation(String location);

    String getVenue();

    void setVenue(String venue);

    String getEndDate();

    void setEndDate(String endDate);

    String getStartDate();

    void setStartDate(String startDate);

    String getTitle();

    void setTitle(String title);

    String getID();

    void setID(String ID);

    String getStartTime();

    void setStartTime(String startTime);

    String getEndTime();

    void setEndTime(String endTime);

    @Override
    int compareTo(Event event);

}
