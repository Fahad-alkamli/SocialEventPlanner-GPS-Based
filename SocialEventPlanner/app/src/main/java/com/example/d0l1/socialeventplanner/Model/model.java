package com.example.d0l1.socialeventplanner.Model;

import android.content.Context;
import android.util.Log;

import com.example.d0l1.socialeventplanner.View.Main;

import java.util.ArrayList;


public class model {

    final static String TAG="Fahad";
    private static ArrayList<Event> eventsList = new ArrayList<Event>();

    DB dbHandler;

    public model(Context context) {
        dbHandler=new DB(context);
    }

    //Search the event list and try to find the event with the given ID
    public  Event findEvent(String ID)
    {
        try {
            for (Event event : eventsList) {
                if (event.getID().equals(ID)) {
                    return event;
                }
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getLocalizedMessage());
        }
        return null;
    }

    //Get the events list
    public  ArrayList<Event> getEventsList()
    {

        if(eventsList==null)
        {
          eventsList = new ArrayList<Event>();
            syncFromDataBase();
            return eventsList;
        }
        if(eventsList.size()<1)
        {
            syncFromDataBase();
        }
        return eventsList;
    }

    //Add an event to the list or update an existing event
    public  void addEventToTheList(final Event event)
    {
        //Create an event
        if(findEvent(event.getID().trim()) ==null)
        {
            eventsList.add(event);
            if(dbHandler != null)
            {
                Runnable run=new Runnable()
                {
                    @Override
                    public void run() {
                        boolean result= dbHandler.insertNewEvent(event);
                        Log.d(TAG,"Insert into database: "+Boolean.toString(result));

                    }
                };

                new Thread(run).start();
            }

        }else
        {
            Log.d(TAG,"This is an update for the events in the list");
            //Remove the old object
            eventsList.remove(findEvent(event.getID()));
            //Add the new object
            eventsList.add(event);
            if(dbHandler != null)
            {
                Runnable run=new Runnable()
                {
                    @Override
                    public void run() {
                        boolean result= dbHandler.updateEvent(event);
                        Log.d(TAG," update for the events in the list: "+Boolean.toString(result));
                    }
                };

                new Thread(run).start();
            }
        }
    }


    public boolean deleteEvent(String EventID)
    {
        if(dbHandler==null)
        {
            return false;
        }
        boolean result=dbHandler.deleteEvent(EventID);
        Event tempEvent=findEvent(EventID);
        eventsList.remove(tempEvent);
        if(result) {
            Log.d(TAG, "Done deleting an event");
        }else{
            Log.d(TAG, "couldn't  delete an event");
        }

        return result;
    }

    public  void syncFromDataBase()
    {
        if(eventsList.size()<1)
        {
            //Sync
            ArrayList<Event> tempEventsArray= dbHandler.getAllEvents();
            if(tempEventsArray != null && tempEventsArray.size()>0)
            {
                eventsList.addAll(tempEventsArray);
                //Log.d(TAG,"Events array in memory has been synced with the database");
                //Log.d(TAG,Integer.toString(tempEventsArray.size()));
            }
        }else{
            //No need to sync
            Log.d(TAG,"No need to sync");
        }

    }
}
