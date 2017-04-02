package com.example.d0l1.socialeventplanner.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Controller.editEventPhase2_Controller;
import com.example.d0l1.socialeventplanner.Model.Common;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Model.CustomContactListAdapter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class editEventPhase2 extends AppCompatActivity implements Serializable{
final String TAG="Fahad";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE =2;
    private MediaRecorder mRecorder = null;
    MediaRecorder recorder;
    private String VoiceNotePath;
    MediaPlayer mPlayer=null;
    boolean running=false;
    boolean Valid_Permission=false;
    boolean noteHasBeenCreated=false;
    boolean voiceNoteHasBeenCreated =false;
    private Event event;
    final int requestCode=1;
    private boolean Weekly_View=false;
    ArrayList<String> namesList =new ArrayList<String>();
    /*
    The onCreate function will receive an event object from the previous activity to start editing it with
    further details.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_phase2);
        try {
            this.event =(Event) getIntent().getExtras().getSerializable("event");
            if(getIntent().getExtras().get("Weekly_View") != null)
            {
                Weekly_View=(boolean) getIntent().getExtras().get("Weekly_View");
            }
            if (event != null) {
                setValues();
            }else{
                Intent i=new Intent(this,Main.class);
                startActivity(i);

            }
            //Voice note Setup
            if(event.getVoiceNotePath()==null) {
                VoiceNotePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Random rnd = new Random();
                VoiceNotePath += "/voiceNote" + Integer.toString(rnd.nextInt(100)) + Integer.toString(rnd.nextInt(100)) + ".3gp";
            }else{
                voiceNoteHasBeenCreated=true;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_ACCESS_STORAGE_AND_WRITE_STOAGE);
            FrameLayout RecordButton=(FrameLayout) findViewById(R.id.RecordButton);
            RecordButton.setOnTouchListener(new View.OnTouchListener(){
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



        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
        findViewById(R.id.addNewAttendessButton).setOnClickListener(new editEventPhase2_Controller(this,this.namesList));


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

    //This Function will take a result from openContactsList activity that will include all the picked contacts if any.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK)
        {
            Set<String> tempSet=new HashSet<String>();
            tempSet.addAll(data.getStringArrayListExtra("namesList"));
            tempSet.addAll(namesList);
            namesList.clear();
            namesList.addAll(tempSet);
            tempSet.clear();
            data.getStringArrayListExtra("namesList").clear();
            ListView AttendessListBox=(ListView) findViewById(R.id.AttendessListBox);
            ListAdapter adapt = new CustomContactListAdapter(this, namesList);
            AttendessListBox.setAdapter(adapt);
            for(String i:namesList)
            {
               Log.d(TAG,i);

            }
        }
    }

    //This is the final function to update event and return the user to the main window or the weekly view
    public void createEvent(View view)
    {
        try {
            event.setNote(((TextView) findViewById(R.id.notesTextBox)).getText().toString());
            if(namesList != null)
            {
                String[] temp = namesList.toArray(new String[namesList.size()]);
                event.setAttendees(null);
                event.setAttendees(temp);
            }
            if(voiceNoteHasBeenCreated==true && (new File(VoiceNotePath).exists())==true)
            {
                event.setVoiceNotePath(VoiceNotePath);
            }else{
                event.setVoiceNotePath(null);
            }

            Main.Engine.addEventToTheList(event);
            if(Weekly_View)
            {
                Intent i = new Intent(this, Weekly_View.class);
                //http://stackoverflow.com/questions/14112219/android-remove-activity-from-back-stack
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

    //This function will set the values by getting it from the event and putting it into the textBoxes
    private void setValues()
    {
        EditText note=(EditText) findViewById(R.id.notesTextBox);
        note.setText(event.getNote());
        if(event.getAttendees() !=null)
        {
            ListView AttendessListBox=(ListView) findViewById(R.id.AttendessListBox);
           // ArrayAdapter adapt=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,event.getAttendees());
            //The old participants are not in the list yet so i have to add them
            for(String user:event.getAttendees())
            {
                namesList.add(user);
                Log.d(TAG,user);
            }
            ListAdapter adapt = new CustomContactListAdapter(this, namesList);
            AttendessListBox.setAdapter(adapt);
            if(event.getVoiceNotePath() != null)
            {
                VoiceNotePath=event.getVoiceNotePath();
            }

        }
    }



    //All the following is recording a voice note

    //https://developer.android.com/guide/topics/media/audio-capture.html

    //This function will start recording a voice note
    private void startRecording() {
        Log.d(TAG,"StartRecording");
        if(Valid_Permission) {
            Log.d(TAG, VoiceNotePath);
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
                  //  Log.d(TAG,e.getMessage());
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

    //Stop a voice note and release the object and if a quick touch occut instead of a long touch to record a voicenote
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
                    mRecorder = null;
                   // Log.d(TAG,en.getMessage());
                    voiceNoteHasBeenCreated=false;
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

    //This function will play the recently recorded voicenote if any.
    public void playVoiceNote(View view)
    {
        try {
            //Log.e(TAG, "Here");

            boolean exists=new File(VoiceNotePath).exists();
            if (Valid_Permission &&exists)
            {

                if(mPlayer != null)
                {
                    try{
                    mPlayer.release();
                    mPlayer=null;}catch(Exception e)
                    {
                        Log.d(TAG,e.getMessage());
                    }
                }
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(VoiceNotePath);
                    mPlayer.prepare();
                    mPlayer.start();
                    Log.d(TAG,"playing .");
                } catch (IOException e) {
                    //Log.e(TAG, "prepare() failed");
                }
            }else if(exists==false)
            {
              //  Log.e(TAG, "File doesn't exist");
               // Log.d(TAG,Boolean.toString(voiceNoteHasBeenCreated)+":"+VoiceNotePath);
               // VoiceNotePath="";
                //Log.d(TAG,event.getVoiceNotePath());
                voiceNoteHasBeenCreated=false;
            }else{
                Log.e(TAG, "Here2");
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
                       // Log.d(TAG,temp);
                    }
                    Valid_Permission=true;
                    Log.d("Fahad","permission was granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Fahad","permission denied");
                    Toast.makeText(this,"This feature need permission, please allow them.",Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}

