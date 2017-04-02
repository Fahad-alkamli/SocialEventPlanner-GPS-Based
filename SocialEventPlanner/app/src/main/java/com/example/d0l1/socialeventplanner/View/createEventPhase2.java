package com.example.d0l1.socialeventplanner.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Controller.createEventPhase2_Controller;
import com.example.d0l1.socialeventplanner.Model.Common;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Model.CustomContactListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class createEventPhase2 extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE =2;
    final String TAG="Fahad";
    private MediaRecorder mRecorder = null;
    MediaRecorder recorder;
    private String VoiceNotePath;
    MediaPlayer mPlayer=null;
    boolean running=false;
    boolean Valid_Permission=false;
    boolean voiceNoteHasBeenCreated =false;
    boolean eventHasBeenCreated=false;
    Event event=new Event();
    final int requestCode=1;
    ArrayList<String> namesList=new ArrayList<String>();
    boolean isWeeklyView=false;

    /*
    OnCreate function will start by receiving information from the previous activity and storing it to an event object
    It will also start the setup for recording a voice note
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_phase2);
        ListView names=(ListView) findViewById(R.id.AttendessListBox);
        try {
            Bundle sentData = getIntent().getExtras();
            if (sentData != null) {
                event.setTitle(sentData.get("title").toString());
                event.setStartDate(sentData.get("startDate").toString());
                event.setEndDate(sentData.get("endDate").toString());
                event.setVenue(sentData.get("venue").toString());
                event.setLocation(sentData.get("location").toString());
                event.setStartTime(sentData.get("startTime").toString());
                event.setEndTime(sentData.get("endTime").toString());
                isWeeklyView=(boolean)sentData.get("isWeeklyView");
            }else{
                Intent i = new Intent(this, create_new_event.class);
               startActivity(i);

            }

            //Voice note Setup
            VoiceNotePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Random rnd =new Random();
            VoiceNotePath += "/voiceNote"+Integer.toString(rnd.nextInt(100))+Integer.toString(rnd.nextInt(100))+".3gp";
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE);
            FrameLayout RecordButton=(FrameLayout) findViewById(R.id.RecordButton);
            RecordButton.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if( motionEvent.getAction()!=MotionEvent.ACTION_UP)
                    {
                        if(running==false) {
                            Log.d("Fahad", "Button has been touched");
                            running=true;
                            startRecording();
                        }
                    }else{
                        Log.d("Fahad","Button has been released");
                        //Stop the recording
                        stopRecording();
                    }

                    return true;
                }

            });

            //Listeners
            findViewById(R.id.addNewAttendessButton).setOnClickListener(new createEventPhase2_Controller(this));

        } catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }

    }


    //This Function will take a result from openContactsList activity that will include all the picked contacts if any.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                //Remove duplicates
                Set<String> tempSet=new HashSet<String>();
                tempSet.addAll(data.getStringArrayListExtra("namesList"));
                tempSet.addAll(namesList);
                namesList.clear();
                namesList.addAll(tempSet);

                ListView AttendessListBox = (ListView) findViewById(R.id.AttendessListBox);
                ListAdapter adapt = new CustomContactListAdapter(this, namesList);
                AttendessListBox.setAdapter(adapt);

                for (String i : namesList) {
                    //Log.d(TAG,i);
                }
            }

        }catch(Exception e)
        {
           Log.d(TAG,e.getMessage());
            Log.d(TAG,"onActivityResult");
        }
    }

    //This is the final function to create a new event and return the user to the main menu
    public void createEvent(View view)
    {
        try {
            Random rnd = new Random();
            //Create a random ID and set it to the Event ID
            event.setID(Integer.toString(rnd.nextInt(100) + rnd.nextInt(200)));
            //Setup the voiceNote path file
            if(voiceNoteHasBeenCreated==true && (new File(VoiceNotePath).exists())==true)
            {
                event.setVoiceNotePath(VoiceNotePath);
            }else{
                event.setVoiceNotePath(null);
            }
           // Log.d(TAG, "Randomized ID:" + event.getID());
            event.setNote(((TextView) findViewById(R.id.notesTextBox)).getText().toString());
            if(namesList != null)
            {
                String[] temp = namesList.toArray(new String[namesList.size()]);
                event.setAttendees(null);
                event.setAttendees(temp);
            }
            Main.Engine.addEventToTheList(event);
            eventHasBeenCreated=true;
            if(isWeeklyView)
            {
                //Return the user to the weekly view activity
                Intent i = new Intent(this, Weekly_View.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }else
            {
                Intent i = new Intent(this, Main.class);
                //http://stackoverflow.com/questions/14112219/android-remove-activity-from-back-stack
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());

        }

    }

    public void deleteContact(View view)
    {
        //Deletes a contact from the attendance for a specific event
        //http://stackoverflow.com/questions/3675238/androidfindviewbyid-how-can-i-get-view-of-a-textview-through-a-listener-of-a
        View parentView=(View)view.getParent();
        TextView name=(TextView) parentView.findViewById(R.id.nameTextView);
        // Log.d(TAG,"Name clicked: "+name.getText().toString());
        //Delete the name from the list
        Log.d(TAG,Boolean.toString(namesList.remove(name.getText().toString())));

        //Update the listview
        ListAdapter adapt = new CustomContactListAdapter(this, namesList);
        ListView AttendessListBox = (ListView) findViewById(R.id.AttendessListBox);
        AttendessListBox.setAdapter(adapt);
    }
    //All the following is recording a voice note

    //https://developer.android.com/guide/topics/media/audio-capture.html

    //This function will start recording a voice note
    private void startRecording() {
        Log.d(TAG,"StartRecording");
        if(Valid_Permission) {
           // Log.d(TAG, VoiceNotePath);
            Common.BeepSound();
            //If the file exists then delete it before making a new recording
            boolean exists=new File(VoiceNotePath).exists();
            if(exists)
            {
                try{
                    File file=new File(VoiceNotePath);

                   // Log.d(TAG,"Done Deleting an older voiceNote:"+Boolean.toString(file.delete()));
                }catch(Exception e)
                {
                   // Log.d(TAG,e.getMessage());
                }
            }

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(VoiceNotePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception e) {
               // Log.e(TAG, "prepare() failed");
            }


        }else{
            Log.d(TAG,"Request permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE);

        }
    }

    //Stop a voice note and release the object and if a quick touch occur instead of a long touch
    //a Toast will be sent to notify the user to long click the button
    private void stopRecording() {
        try{
        Log.d(TAG,"StopRecording");
          if(mRecorder != null)
          {
              try {
                  mRecorder.stop();
                  voiceNoteHasBeenCreated =true;
              }catch(Exception en)
              {
                  mRecorder.release();
                  voiceNoteHasBeenCreated=false;
                  mRecorder = null;
                 // Log.d(TAG,en.getMessage());
                  Toast.makeText(this,"Long press to Record, Release to stop Recording",Toast.LENGTH_LONG).show();
              }

        running=false;}
        } catch(Exception e)
        {
            //Log.d(TAG,e.getMessage());
            voiceNoteHasBeenCreated=false;
            Toast.makeText(this,"Long press to Record, Release to stop Recording",Toast.LENGTH_LONG).show();

        }
    }


    //This function will play the recently recorded voice note if exists.
    public void playVoiceNote(View view)
    {
        try {

            boolean exists=new File(VoiceNotePath).exists();
            if (Valid_Permission &&exists)
            {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(VoiceNotePath);
                    mPlayer.prepare();
                    mPlayer.start();
                    Log.d(TAG,"playing .");
                } catch (IOException e) {
                   // Log.e(TAG, "prepare() failed");
                }
            }else if(exists==false)
            {
                voiceNoteHasBeenCreated=false;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE);
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }

    }

    //This function will get called when the user allowed  or denied the requested Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    for(String temp:permissions)
                    {
                      //  Log.d(TAG,temp);
                    }
                    Valid_Permission=true;
                   // Log.d("Fahad","permission was granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG,"permission denied");
                    Toast.makeText(this,"This feature need permission, please allow them.",Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    //Make sure that an event that existed without completion will not leave an existing voiceNote file in the system
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        try {
            if (eventHasBeenCreated == false && voiceNoteHasBeenCreated == true) {
                try {
                    Log.d(TAG, "VoiceNote has not been used, therefore has been deleted:" + Boolean.toString(new File(VoiceNotePath).delete()));
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            } else if (voiceNoteHasBeenCreated == false) {
                File file = new File(VoiceNotePath);
                if (file.exists()) {
                    file.delete();
                    Log.d(TAG,"Creating voiceNote wasn't successful therefor delete the file.");
                }

            }
        }catch(Exception en)
        {
            Log.d(TAG, en.getMessage());
        }
    }
}

