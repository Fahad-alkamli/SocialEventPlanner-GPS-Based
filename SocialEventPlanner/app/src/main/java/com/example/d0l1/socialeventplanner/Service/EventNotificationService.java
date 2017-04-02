package com.example.d0l1.socialeventplanner.Service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.util.Log;

import com.example.d0l1.socialeventplanner.MapsActivity;
import com.example.d0l1.socialeventplanner.Model.Common;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.Model.model;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;

public class EventNotificationService extends Service {

    public static boolean Running=false;
    public static boolean HasPermission=false;
    static NotificationCompat.Builder notify;
     static  LocationManager locationManager;
    static  LocationListener locationListener;
    static int uniqueID=0;
    public static boolean missedLocationChange=false;
    static Context serviceContext=null;
    static Service thisService=null;
    static HashMap<String,String> EventsDuration=new LinkedHashMap<String,String>();
    static ArrayList<String> eventsIds=new ArrayList<String>();
    final static String TAG="Fahad";
    AsyncTask asyncTask=null;

    static Location LastKnownLocation=null;
    static ArrayList<String> doneNotifying=new ArrayList<String>();

    //default is 5 minutes 300000
    static int timeToWait=300000;
    static int addExtraMinutes=15;


    /*
    This function will start a function to check events with and without internet connection.
    It will also register a location update to capture changes in the user's location
     */
    @Override
    public void onCreate() {
      //  Log.d(TAG,"onCreate?");
        super.onCreate();
        thisService=this;
        serviceContext=getApplicationContext();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Ask for permission

        try {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setLastKnownLocation(location);
                    Log.d(TAG, "Location update");
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //Send a notification asking for a click that click will launch an activity that activity will ask the permission
                Notify("This service needs permission.", AskPermission.class, "",Integer.toString(uniqueID));

                Log.d(TAG, "launch an activity to ask for permission");

            } else
            {
                HasPermission = true;

                //Set the listner to change the location
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }


            //Checking user preference
            SharedPreferences sharedPreferences = thisService.getSharedPreferences("com.example.d0l1.socialeventplanner", Context.MODE_PRIVATE);

            if (sharedPreferences.getInt("timeToWait", -1) != -1) {
                setTimerPeriod(sharedPreferences.getInt("timeToWait", -1));

            }
            if (sharedPreferences.getInt("addExtraMinutes", -1) != -1) {
                setAddExtraMinutesPeriod(sharedPreferences.getInt("addExtraMinutes", -1));
            }
        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
        }
    }

        /*
         If the service is not running this code will start a service and register a location change listener
            Furthermore, if permissions are not present the app will send a notification asking for permission.
             By clicking on the notification the user will be taken to an activity to request the needed permissions.
             Even thought the user didn't allow the permissions or the permissions are in the process of being granted
             This function will launch a function that will check for upcoming events without an internet connection.
        */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
      // Log.d(TAG,"onStartCommand Test");
        //I just want to make sure that even if the system somehow stopped the task  we can detect this
        if(Running && asyncTask != null &&asyncTask.getStatus()==AsyncTask.Status.RUNNING)
        {
            return Service.START_STICKY;
        }

         asyncTask=  new AsyncTask<Void, Void, Void>()
        {

            @Override
            protected Void doInBackground(Void... voids) {
                do{

                    synchronized (this)
                    {
                        try{
                            //Check the distance from the API
                            if(Main.Engine==null)
                            {
                                Main.Engine = new model(getApplicationContext());
                            }
                           // Log.d(TAG,"Check the distance from the API");
                            if(Main.Engine.getEventsList().size()>0)
                            {
                                try {
                                    if (isNetworkAvailable() && HasPermission && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                    {
                                       // Log.d(TAG,"Here");
                                        //Will check the last known location and get the distance etc.
                                        //Next i need to check if the GPS is enabled or not
                                        if (!checkAPI(null))
                                        {
                                            //we couldn't check online for GPS location and distance matrix
                                            missedLocationChange = true;
                                           Log.d(TAG, "Couldn't check the API, last location couldn't be detected");
                                            CheckEventsWithoutInternetConnection();
                                        }

                                    } else {
                                     //  Log.d(TAG, "checking without internet connection");
                                        //Check if the date is today
                                        //When should i tell him about the event?
                                        //We don't have either permission or internet
                                        missedLocationChange = true;
                                        CheckEventsWithoutInternetConnection();


                                    }
                                }catch(Exception e)
                                {

                                    class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

                                }

                            }


                        }catch(Exception e)
                        {
                            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

                        }
                        try{
                            wait(getTimerPeriod());
                        }catch(Exception e)
                        {

                        }
                    }


                }while(true);

                //The service just stopped for some reason


            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        Running=true;

        return Service.START_STICKY;
    }

    /*
    This function is used to send a notification to the user
     */
    public static void Notify(String Message,Class ResponseClass, String extra,String eventID)
    {
        try {
            //Get the default notification sound from the system
            Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            notify = new NotificationCompat.Builder(thisService);
            //Removes the notification from the list if someone clicks it
            notify.setAutoCancel(true);
            //notify.setTicker("This is the ticker");
            //When did this thing happen ?
            notify.setWhen(System.currentTimeMillis());
            notify.setSmallIcon(R.mipmap.ic_launcher);
            notify.setSound(defaultRingtoneUri);

            notify.setContentTitle("Social Event Planner");
            notify.setContentText(Message);
            Intent intent=null;
            if(ResponseClass==MapsActivity.class)
            {
                 intent = new Intent(thisService, Main.class);

            }else{
                 intent = new Intent(thisService, ResponseClass);
            }

            if(extra != null)
            {
                //Here will go the event location and i will also add the current location

                intent.putExtra("extra",extra);
            }

            PendingIntent pend = PendingIntent.getActivity(thisService.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            /* will display the buttons if the intended class isn't trying to ask for permission
               I don't need to put buttons in case if thie notification is not based on a distance matrix
               because the default click on the notification will take the user to the ResponseClass in this case Main
            */
            if(ResponseClass != AskPermission.class && ResponseClass==MapsActivity.class)
            {
                    Intent mapIntent=new Intent(thisService,MapsActivity.class);
                    if(extra != null)
                    {
                        mapIntent.putExtra("extra", extra);
                        Location tempLocation=getLastKnownLocation();
                        String location=Double.toString(tempLocation.getLatitude())+","+Double.toString(tempLocation.getLongitude());
                        mapIntent.putExtra("currentLocation",location);
                    }
                    PendingIntent pendingMap = PendingIntent.getActivity(thisService.getApplicationContext(), 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notify.addAction(R.mipmap.ic_directions_black_24dp, "Show distance", pendingMap);
                    //Show the two buttons one click on an event and the second will open it in the map
                    notify.addAction(R.drawable.blank_button, "View Event", pend);

            }


            notify.setContentIntent(pend);
            NotificationManager NM = (NotificationManager) thisService.getSystemService(NOTIFICATION_SERVICE);
            StatusBarNotification[] list=  NM.getActiveNotifications();

            //Make sure the notification is dismissed before showing another one
            boolean send=true;
            for(StatusBarNotification i:list)
            {
                if(i.getId()==Integer.parseInt(eventID))
                {
                    //don't send anything
                    send=false;
                    break;
                }
            }
            if(send)
            {
                NM.notify(Integer.parseInt(eventID), notify.build());
            }



        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());
        }

    }


    /*
    This function is responsible for sending request to Distance matrix Api and getting the response back and calling another function
    To calculate TravelTime AndE ventStartTime
    */
   static void checkEventsDistance(String currentLocation)
   {
       //Log.d(TAG,"checkEventsDistance :"+currentLocation);

       HashMap<String,String> trackedEvents=new LinkedHashMap<String,String>();
       //Clear old tracked events
       trackedEvents.clear();
       eventsIds.clear();
       String dateNow="";
       missedLocationChange=false;
       //Loop through the events and try to find events for today
       //After that we will start constructing the request to google API to measure the distance
       for(Event event:Main.Engine.getEventsList())
       {
           try{
                   trackedEvents.put(event.getID(),event.getLocation());
                   eventsIds.add(event.getID());
           }catch(Exception e)
           {
               class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

           }
       }
       //Now that we have all the events Ids in the list we can start
       String RequestReady=constructRequest(currentLocation,trackedEvents);

       if(RequestReady != null)
       {

           try {
               URL url = new URL(RequestReady);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
               try {
                   urlConnection.setDoOutput(true);
                   urlConnection.setChunkedStreamingMode(0);

                   OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                   InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                   BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                   String line="";
                   String response="";
                   while((line=reader.readLine()) != null)
                   {

                       if(line.trim().replace(" ","").length()>0)
                       {
                           response += line + "\n";
                       }
                   }

                 // Log.d(TAG,response);
                   Log.d(TAG,"Got a response from the Server ");
                   JSONObject jsonObject=new JSONObject(response);

                   JSONArray rows=jsonObject.getJSONArray("rows");
                  // Log.d(TAG,"rows: "+Integer.toString(rows.length()));
                   if(!jsonObject.getString("status").toLowerCase().equals("ok"))
                   {
                       Log.d(TAG,"Response is is not okay!");
                       return;
                   }
                  for(int i=0;i<rows.length();i++)
                  {
                      //List of rows objects

                      JSONObject object0=rows.getJSONObject(i);
                      JSONArray elements=object0.getJSONArray("elements");

                     // Log.d(TAG,"elements: "+Integer.toString(elements.length()));
                      for(int b=0;b<elements.length();b++)
                      {
                       try {
                           JSONObject elementsObject = elements.getJSONObject(b);
                           JSONObject duration = elementsObject.getJSONObject("duration");
                           if(eventsIds.get(b) != null && duration != null)
                           {
                               EventsDuration.put(eventsIds.get(b), duration.getString("text"));
                           }
                       }catch(Exception e)
                       {
                           class Local {}; Log.d(TAG,"Class: "+Local.class.getEnclosingClass().getSimpleName()+", Sub: "+Local.class.getEnclosingMethod().getName()+", Error code: "+e.getMessage());

                       }
                      }

                  }


               } finally {
                   urlConnection.disconnect();
               }
           }catch(Exception e)
           {
                        class Local {}; Log.d(TAG,"Class: "+Local.class.getEnclosingClass().getSimpleName()+", Sub: "+Local.class.getEnclosingMethod().getName()+", Error code: "+e.getMessage()); 
           }

       }
       calculateTravelTimeAndEventStartTime();

   }

    /*
    this function will construct the request to google API . and will return a request URL
     */
    static String constructRequest(String currentLocation, HashMap<String,String> trackedEvents)
    {
        String RequestURL="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=currentLocation&destinations=FirstDist";
        if(trackedEvents.size()>0)
        {
            //Replace the current location in the url template
            RequestURL=RequestURL.replace("currentLocation",currentLocation);

            String destinations="";
            for(String location:trackedEvents.values())
            {
                destinations+=location.trim()+"|";
            }
            //clean the string
            destinations=destinations.trim();
            if(destinations.substring(destinations.length()-1,destinations.length()).equals("|"))
            {
                destinations=destinations.substring(0,destinations.length()-1);
            }
            //put the destinations string into the url
            RequestURL=RequestURL.replace("FirstDist",destinations);

            return RequestURL;
        }
        return null;
    }


    /*This function will be launched when the user doesn't have a location or the internet is disabled
     If the event is on the same hour then alert the user.
     Perhaps it would be better if we can notify the user 20 minutes or so before the event and check for minutes
     It is worth mentioning that I actually designed it so if a notification is sent to the user, the user will not
     be notified with that event again but i stopped for testing purposes and i wasn't sure if this is what you actually wanted.
     */
    static void CheckEventsWithoutInternetConnection()
    {
        try {
            Log.d(TAG,"CheckEventsWithoutInternetConnection");
            DateFormat format = new SimpleDateFormat("dd/M/yyyy H:mm");
            DateFormat format2 = new SimpleDateFormat("dd/M/yyyy hh:mm a");
            DateFormat dateFormat=new SimpleDateFormat("dd/M/yyyy H");
            Date temp=Calendar.getInstance().getTime();

            String dateNow=format.format(temp);
           // Log.d(TAG, "DateTime Now: " + dateNow);

            for (Event event : Main.Engine.getEventsList())
            {
                String StartEventTemp = event.getStartDate() + " " + event.getStartTime();
                Date temp2=format2.parse(StartEventTemp);
                String EventDateTime=format.format(temp2);
               // Log.d(TAG,"Check this: "+EventDateTime+ " =="+event.getStartTime());
                if(getAddExtraMinutesPeriod()!=0)
                {
                    Log.d(TAG, "dateNow before adding the extra minutes: " + dateNow);
                    //We add the extra minutes to the time now
                    dateNow=addTime(dateNow,"0 min");
                    Log.d(TAG, "dateNow after adding the extra minutes: " + dateNow);
                }

                //I am trying to compare the date and time in one hit but not the minutes
                if (dateFormat.parse(dateNow.trim()).equals(dateFormat.parse(EventDateTime)))
                {
                    //Now we check the minutes
                    int minutes1=Integer.parseInt(dateNow.split(" ")[1].split("[:]")[1]); //Now minutes
                    int minutes2=Integer.parseInt(EventDateTime.split(" ")[1].split("[:]")[1]); //Event minutes
                     if(minutes1<=minutes2)
                    {
                        //We will be on time
                        Log.d(TAG, "We will be on time");
                       // Log.d(TAG, "Event is coming soon!");
                       // if(!doneNotifying.contains(event.getID()))
                        {
                            Notify("We will be on time for " + event.getTitle() + " event.", Main.class, null,event.getID());
                            doneNotifying.add(event.getID());
                        }
                    }else
                    {
                        Log.d(TAG,"We are running "+Integer.toString((minutes2-minutes1)).replace("-","")+" Minutes late");
                       // Log.d(TAG, "Event is coming soon!");
                       // if(!doneNotifying.contains(event.getID()))
                        {
                            Notify("We are running " +Integer.toString((minutes2-minutes1)).replace("-","")+" Minutes late for "+ event.getTitle(), Main.class, null,event.getID());
                            doneNotifying.add(event.getID());
                        }
                    }

                }
            }

        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());


        }

    }
    /*
    This is the function that will notify the user based on the current time and date and the result coming from the API

     */
    static void calculateTravelTimeAndEventStartTime()
    {
        DateFormat format = new SimpleDateFormat("dd/M/yyyy H:mm");
        DateFormat format2 = new SimpleDateFormat("dd/M/yyyy hh:mm a");
        DateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
        Date temp=Calendar.getInstance().getTime();

        String dateNow=format.format(temp);
        Log.d(TAG, "DateTime Now: " + dateNow);

        for(String eventKey:EventsDuration.keySet())
        {
            //Log.d(TAG,"Key: "+eventKey);
            Event event=null;
            try {
                if((event=Main.Engine.findEvent(eventKey))==null)
                {
                    Log.d(TAG,"This event doesn't exists:"+eventKey);
                    continue;
                }
                String StartEventTemp = event.getStartDate() + " " + event.getStartTime();
              //  Log.d(TAG,"Test");
                Date temp2=format2.parse(StartEventTemp);
                String EventDateTime=format.format(temp2);
              //  Log.d(TAG,"Test2");
                String location=event.getLocation();

                String ArrivalTime=EventsDuration.get(eventKey);
               //Log.d(TAG,"EventTime: "+EventDateTime+" | ArrivalTime: "+ArrivalTime+" | TimeNow: "+dateNow);
                String TimeNowAfterAddingTravelTime=(addTime(dateNow,ArrivalTime));

               //Log.d(TAG,"Time After Adding TravelTime: "+TimeNowAfterAddingTravelTime);
                //First we make sure this event is on the same day then we check the time
                Date dateToday=dateFormat.parse(dateNow);
                Date todayDatePlusTravelTime=dateFormat.parse(TimeNowAfterAddingTravelTime);
                Date eventDate=dateFormat.parse(event.getStartDate().trim());
                //if the event is not today we shouldn't bother checking it and notifying the user
                if(!todayDatePlusTravelTime.equals(eventDate))
                {
                    Log.d(TAG,"Event is not today");
                    Log.d(TAG,"todayDatePlusTravelTime: "+todayDatePlusTravelTime+" eventDate: "+eventDate);
                    continue;
                }
                Log.d(TAG,"Event is today");
                //Log.d(TAG,"todayDatePlusTravelTime: "+todayDatePlusTravelTime+" eventDate: "+eventDate);
              //  Log.d(TAG,"Check this: today: "+dateToday+" | todayPlusTime: "+todayDatePlusTravelTime+" event: "+eventDate);
                //TimeNowAfterAddingTravelTime==Event Start Time
                int hour1=Integer.parseInt(EventDateTime.split(" ")[1].split("[:]")[0]);//Hour now
                int hour2=Integer.parseInt(TimeNowAfterAddingTravelTime.split(" ")[1].split("[:]")[0]);//Hour now +travel time
                //format
               // Log.d(TAG,"Check this: "+TimeNowAfterAddingTravelTime);
                Date temp3=format.parse(TimeNowAfterAddingTravelTime);

                String check=format.format(temp3);
               // Log.d(TAG,"Check this2: "+check);


                Log.d(TAG,"Making sure that the event and now are in the same hour, Hour1: "+Integer.toString(hour1)+" hour2: "+Integer.toString(hour2));
                if(hour1==hour2)
                {
                    //Should i compare the hour only or the hour and minutes ?
                    //Wouldn't it make more sense to compare the hour only?
                   Log.d(TAG,"We have an event expected in the same hour");
                   // Log.d(TAG,"Expected Time of arrival:"+TimeNowAfterAddingTravelTime);
                    //Make sure that we didn't miss the event by checking the minutes
                    int minutes1=Integer.parseInt(TimeNowAfterAddingTravelTime.split(" ")[1].split("[:]")[1]);
                    int minutes2=Integer.parseInt(EventDateTime.split(" ")[1].split("[:]")[1]);

                    //Because we actually added 15 minutes extra we can't say we are late if we were on time, therefor
                    //I added this Or statement to make sure that if we arrive on clock, we don't get a notification that we are running late
                   // Log.d(TAG,"Making sure that the event and now are in the same minute");
                    if(minutes1<=minutes2 )
                    {
                        //We will be on time
                        Log.d(TAG,"We will be on time");
                        //if(!doneNotifying.contains(event.getID()))
                        {
                            Notify("We will be on time for "+event.getTitle()+" event.",MapsActivity.class,location,event.getID());
                            doneNotifying.add(event.getID());
                        }

                    }else{
                        //We are running late
                        //I subtracted 15 minutes because we added them before , i am trying to get as accurate result as possible here
                        Log.d(TAG,"We are running "+Integer.toString((minutes1-minutes2))+" Minutes Late");
                      // if(!doneNotifying.contains(event.getID()))
                        {
                            Notify("We are running "+Integer.toString((minutes1-minutes2))+" minute/s Late for "+event.getTitle()+"",MapsActivity.class,location,event.getID());
                            doneNotifying.add(event.getID());
                        }

                    }
                }

            }catch(Exception e)
            {
                class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

            }

        }

        //Clear the events list
        EventsDuration.clear();

    }

    /*
    Simple function to add a time to an existing date
     */
    //http://stackoverflow.com/questions/9015536/java-how-to-add-10-mins-in-my-time
    static String addTime(String dateTime,String minutesOrHours)
    {
        try {
            DateFormat format = new SimpleDateFormat("dd/M/yyyy H:mm");
            Calendar cal = Calendar.getInstance();
            if (minutesOrHours.toLowerCase().contains("min")) {
                //Add minutes to the time
                minutesOrHours = minutesOrHours.split(" ")[0].trim();

                Date d = format.parse(dateTime);
                cal.setTime(d);
                //Extra 15 minutes
               // Log.d(TAG,"Check this: "+dateTime);
               // Log.d(TAG,"addtime: "+d);
                cal.add(Calendar.MINUTE, Integer.parseInt(minutesOrHours)+getAddExtraMinutesPeriod());
                Log.d(TAG,"Extra minutes: "+getAddExtraMinutesPeriod());
                Log.d(TAG,"travel minutes: "+minutesOrHours);
                //Log.d(TAG,"Math:"+Integer.toString(Integer.parseInt(minutesOrHours)+getAddExtraMinutesPeriod()));
                String newTime = format.format(cal.getTime());
               Log.d(TAG,"Time after adding minutes: "+newTime);
                return newTime;
            } else {
                //Add hours
                minutesOrHours = minutesOrHours.split(" ")[0].trim();
                Date d = format.parse(dateTime);
                cal.setTime(d);
                cal.add(Calendar.HOUR, Integer.parseInt(minutesOrHours));
                //Extra 15 minutes
                cal.add(Calendar.MINUTE, getAddExtraMinutesPeriod());
                String newTime = format.format(cal.getTime());
               // Log.d(TAG,"Time after adding Hours: "+newTime);
                return newTime;
            }


        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Running=false;
        doneNotifying.clear();
    }

    /*
    This function will check for a network activity
     */
    //http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null)
        {
            boolean yes=activeNetworkInfo.isConnected();
            return yes;
        }
        return false;
    }


    /*
        This function will be the Mother of all functions that uses Internet connection.
        I used a main function to take advantage of enabling a direct check for events as soon as the user enables the internet connection .
        This function will be invoked from This service or InternetStateReceiver
     */
   public static boolean checkAPI(String option)
    {
        try {
            if (ActivityCompat.checkSelfPermission(serviceContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(serviceContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //Send a notification asking for a click that click will launch an activity that activity will ask the permission
                Notify("This service needs permission.", AskPermission.class, "CheckAgain",Integer.toString(uniqueID));
                HasPermission=false;
                Log.d(TAG, "launch an activity to ask for permission");
                return false;
            } else {

                if(option != null && option.equals("registerGpsUpdate"))
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }

                //get the last known location and try to make an accurate estimate
                //This will return null if the user had the GPS turned off for a long time
                //Because the last known location is null ? So what can i do now
                //This will only return something if the GPS is currently being used otherwise it will return null
                if(getLastKnownLocation()==null)
                {
                   // Log.d(TAG,"getLastKnownLocation == null");
                    return false;
                }
                final Location location=getLastKnownLocation();

                String currentLocation = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

               // Log.d(TAG,"Last known: "+currentLocation);
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        String currentLocation = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

                        Log.d(TAG,"checkEventsDistance using google API");
                        checkEventsDistance(currentLocation);

                    }
                };
                new Thread(run).start();
                return true;
            }
        }catch(Exception e)
        {
            class Local {}; Log.d(TAG,"Sub: "+Local.class.getEnclosingMethod().getName()+" Error code: "+e.getMessage());

        }

        return false;
    }

    /*
    All the below functions are needed to synchronize threads
     */
    public static  synchronized  void setTimerPeriod(int period)
    {
        timeToWait=period;
        //save them to the user preference
        SharedPreferences sharedPreferences= thisService.getSharedPreferences("com.example.d0l1.socialeventplanner", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putInt("timeToWait",timeToWait);
        edit.commit();
    }
    synchronized  int getTimerPeriod()
    {
       return timeToWait;
    }


    public static  synchronized  void setAddExtraMinutesPeriod(int period)
    {
        addExtraMinutes=period;
        //save them to the user preference
       SharedPreferences sharedPreferences= thisService.getSharedPreferences("com.example.d0l1.socialeventplanner", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putInt("addExtraMinutes",addExtraMinutes);
        edit.commit();
    }
    synchronized static int getAddExtraMinutesPeriod()
    {
        return addExtraMinutes;
    }

    static synchronized void setLastKnownLocation(Location Location)
    {
        LastKnownLocation=Location;
    }
    static synchronized Location getLastKnownLocation()
    {
        return LastKnownLocation;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





}
