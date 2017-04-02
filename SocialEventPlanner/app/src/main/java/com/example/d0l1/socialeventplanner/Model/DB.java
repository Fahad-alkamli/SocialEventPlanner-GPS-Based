package com.example.d0l1.socialeventplanner.Model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper{

    final String TAG="Fahad";
    private static final int DATABASE_VERSION=1;
    private static final String DBName="Events.db";
    private static final String DATABASE_CREATE = "create table events(id integer primary key, title text not null, startDate text not null, endDate text not null, venue text not null, location text, note text, startTime text not null, endTime text not null, voiceNotePath text, attendees text);";

    private static final String TABLE="events";

    public DB(Context context) {
        super(context, DBName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS events");
        onCreate(sqLiteDatabase);
    }


    //Insert new event into the database
    public boolean insertNewEvent(Event event)
    {
        try {
            ContentValues values = new ContentValues();
            values.put("id", event.getID());
            values.put("title", event.getTitle());
            values.put("startDate", event.getStartDate());
            values.put("endDate", event.getEndDate());
            values.put("venue", event.getVenue());
            values.put("location", event.getLocation());
            values.put("note", event.getNote());
            values.put("startTime", event.getStartTime());
            values.put("endTime", event.getEndTime());
            values.put("voiceNotePath", event.getVoiceNotePath());
            values.put("attendees", join(event.getAttendees()));
            SQLiteDatabase db = getWritableDatabase();
            db.insert(TABLE, null, values);
            db.close();
            return true;
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        return false;
    }

    //Delete an event from the database
    public boolean deleteEvent(String eventID)
    {
        SQLiteDatabase db = getWritableDatabase();
        if(eventID==null)
        {
            Log.d(TAG,"Event ID == null");
            return false;
        }
        if( eventID.trim().replace(" ","").equals(""))
        {
            Log.d(TAG,"Event ID == nothing");
            return false;
        }

        try{
            int rows=db.delete(TABLE,"id=?", new String[] {eventID.trim()});

            if(rows!=0)
            {
                //Log.d(TAG,Integer.toString(rows));
                return true;
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }finally {
            db.close();
        }
        return false;
    }

    //Update an existing event in the database
    public boolean updateEvent(Event event)
    {
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values=new ContentValues();
            values.put("id", event.getID());
            values.put("title", event.getTitle());
            values.put("startDate", event.getStartDate());
            values.put("endDate", event.getEndDate());
            values.put("venue", event.getVenue());
            values.put("location", event.getLocation());
            values.put("note", event.getNote());
            values.put("startTime", event.getStartTime());
            values.put("endTime", event.getEndTime());
            values.put("voiceNotePath", event.getVoiceNotePath());
            values.put("attendees", join(event.getAttendees()));

           int result= db.update(TABLE,values,"id=?",new String[]{event.getID()});

            if(result != 0)
            {
                return true;
            }
        }catch(Exception e)
        {

        }finally{
            db.close();
        }



        return false;
    }

    public ArrayList<Event> getAllEvents()
    {
        ArrayList<Event> events=new ArrayList<Event>();
        SQLiteDatabase db = getWritableDatabase();
        try{
            Cursor c=db.rawQuery("select * from "+TABLE,null);
            c.moveToFirst();
            while(c.isAfterLast()==false)
            {

                //Log.d(TAG,"Still here");
                Event event=new Event();
                event.setID(c.getString(c.getColumnIndex("id")));
                event.setTitle(c.getString(c.getColumnIndex("title")));
                event.setStartDate(c.getString(c.getColumnIndex("startDate")));
                event.setEndDate(c.getString(c.getColumnIndex("endDate")));
                event.setVenue(c.getString(c.getColumnIndex("venue")));
                event.setLocation(c.getString(c.getColumnIndex("location")));
                event.setNote(c.getString(c.getColumnIndex("note")));
                event.setStartTime(c.getString(c.getColumnIndex("startTime")));
                event.setEndTime(c.getString(c.getColumnIndex("endTime")));
                event.setVoiceNotePath(c.getString(c.getColumnIndex("voiceNotePath")));
                event.setAttendees(split(c.getString(c.getColumnIndex("attendees"))));
                events.add(event);
               // Log.d(TAG,event.getID());
                c.moveToNext();
            }


        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }finally{
            db.close();
        }
        if(events.size()>0)
        {
            //Log.d(TAG,"Here");
            return events;
        }

        return null;
    }


    private String join(String[] array)
    {
        if(array==null || array.length<1)
        {
            return null;
        }
        String output="";
        for(String value:array)
        {

            if(value !=null)
            {
                output+=value.trim()+"|";
            }
        }
        if(output.replace("|","").trim().replace(" ","").equals(""))
        {
            return null;
        }
        Log.d(TAG,"Join: "+output);
        return output;
    }

    private String[] split(String value)
    {
        if(value ==null || value.trim().replace(" ","").equals(""))
        {
            return null;
        }
        try {
            String[] array = value.split("\\|");

            return array;
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        return null;
    }

}
