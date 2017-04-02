package com.example.d0l1.socialeventplanner.Model;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Common {
    MediaRecorder mRecorder;


    static String TAG="Fahad";


    //This function will make a beep sound to notify the user that a recording session has been started.
    public static void BeepSound()
    {
        try {
            //http://stackoverflow.com/questions/6462105/how-do-i-access-androids-default-beep-sound
            final ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            beep.startTone(ToneGenerator.TONE_PROP_BEEP);
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }


    }

    //http://www.mkyong.com/regular-expressions/how-to-validate-date-with-regular-expression/
    //This function will validate the .s to be in a correct format
    public static boolean validateDate(String date)
    {
        try{
            String SimpleDatePattern = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
            Pattern pattern = Pattern.compile(SimpleDatePattern);
            Matcher matched=pattern.matcher(date);
            if(matched.find())
            {
                return true;
            }

        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        return false;
    }
    //Validate the time format
    public static boolean ValidateTime(String startTime,String endTime,Context context)
    {

        try{
            DateFormat TimeFormat=new SimpleDateFormat("hh:mm a");
            Pattern Time12=Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)(?i)(am|pm)");


            Matcher time1=Time12.matcher(startTime);
            Matcher time2=Time12.matcher(endTime);
            if(time1.matches()==true && time2.matches()==true) {

                //Log.d(TAG, "Validating the time");
                return true;
            }else
            {
                if(time1.matches()==false)
                {
                    Toast.makeText(context, "Error: Check the start time format", Toast.LENGTH_LONG).show();
                }else
                {
                    Toast.makeText(context, "Error: Check the end time format", Toast.LENGTH_LONG).show();
                }

            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getLocalizedMessage());

        }

        return false;
    }

    //Compare two dates and make sure that the startdate comes after the enddate
    public static boolean compareDates(String startDate,String endDate)
    {
        int day=Integer.parseInt(startDate.split("/")[0]);
        int month=Integer.parseInt(startDate.split("/")[1]);
        int year=Integer.parseInt(startDate.split("/")[2]);

        int day2=Integer.parseInt(endDate.split("/")[0]);
        int month2=Integer.parseInt(endDate.split("/")[1]);
        int year2=Integer.parseInt(endDate.split("/")[2]);
        if(year<year2 || year==year2)
        {
            //Check the month
            if(month<month2 || month==month2)
            {
                if(month<month2)
                {
                    return true;
                }
                //Check the day
                if(day<day2)
                {
                    return true;
                }
            }
        }

        return false;
    }



    public static String from12To24(String h12)
    {
        try {
            //Log.d(TAG,h12);
            DateFormat format2 = new SimpleDateFormat("hh:mm a");
            DateFormat format = new SimpleDateFormat("HH:mm");
            Date temp = format2.parse(h12);

            return format.format(temp);

        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }

        return null;
    }
    public static void requestNetworkAndGPSPermissions(Activity activity)
    {
        Toast.makeText(activity,"This Service needs your Permission, if not given this service will not work.",Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE}, 5);
    }
}
