package com.example.d0l1.socialeventplanner.View;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Controller.Main_Controller;
import com.example.d0l1.socialeventplanner.MapsActivity;
import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.Model.model;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.Service.EventNotificationService;
import com.example.d0l1.socialeventplanner.View.Model.CustomeEventAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class Main extends AppCompatActivity {


    public static model Engine = null;
    final static String TAG = "Fahad";

    Activity activity=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        activity=this;

        //Start the service if it's not runniung already
        if(EventNotificationService.Running==false)
        {
            Log.d(TAG,"Service is being launched");
           Intent i=new Intent(this,EventNotificationService.class);
           startService(i);
        }
        if (Engine == null) {
            Engine = new model(this);
        }
        if (Engine != null) {
            Engine.syncFromDataBase();
        }
        try {
            //try to find if events already exists in the list and if they don show them in the listview
            if (Engine.getEventsList().size() > 0) {
                Log.d(TAG, "Done a restore");
                //Restore Events stored in the list
                TextView statusLabel = (TextView) findViewById(R.id.noNoteTextView);
                statusLabel.setVisibility(View.INVISIBLE);
                Collections.sort(Engine.getEventsList());
                ListAdapter adapt = new CustomeEventAdapter(this, Engine.getEventsList());
                ListView list = (ListView) findViewById(R.id.EventsList);
                list.setAdapter(adapt);
            }
            //Set listeners
            //Will launch create new event activity
            findViewById(R.id.newNoteButton).setOnClickListener(new Main_Controller());
            //Handles a click on the Weekly view button to launch a weekly view activity
            findViewById(R.id.weekViewButton).setOnClickListener(new Main_Controller());
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "onCreate");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  Log.d(TAG,"Testing menu item");
        switch (item.getItemId()) {
            case R.id.ascending: {
                Collections.sort(Engine.getEventsList());
                ListAdapter adapt = new CustomeEventAdapter(this, Engine.getEventsList());
                ListView list = (ListView) findViewById(R.id.EventsList);
                list.setAdapter(adapt);
            }
            break;
            case R.id.descending: {
                Collections.sort(Engine.getEventsList(), Collections.reverseOrder());
                ListAdapter adapt = new CustomeEventAdapter(this, Engine.getEventsList());
                ListView list = (ListView) findViewById(R.id.EventsList);
                list.setAdapter(adapt);
            }
            break;

            case R.id.settings:
            {

                Intent i=new Intent(this,settings.class);
                startActivity(i);
            }
            break;
            default:
                //if none of the options above it's a weekly view option
                Intent i = new Intent(this, Weekly_View.class);
                startActivity(i);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Engine.getEventsList().size() > 0) {
               // Log.d(TAG, "Done a restore");
                //Restore Events stored in the list
                TextView statusLabel = (TextView) findViewById(R.id.noNoteTextView);
                statusLabel.setVisibility(View.INVISIBLE);
                Collections.sort(Engine.getEventsList());
                ListAdapter adapt = new CustomeEventAdapter(this, Engine.getEventsList());
                ListView list = (ListView) findViewById(R.id.EventsList);
                list.setAdapter(adapt);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "onCreate");

        }
    }


    //Handles a click on the item list to make a modification to an event or delete an event
    public void eventClicked(final View view) {


        //Launch a popup menu to choose from edit an event or delete an event

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            TextView ID = (TextView) view.findViewById(R.id.ID);

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.editEventButton:
                        try {
                           // Log.d(TAG, "Event Item clicked");

                            // Log.d(TAG,ID.getText().toString());
                            Event event = Engine.findEvent(ID.getText().toString());
                            if (event != null) {
                              //  Log.d(TAG, event.getTitle());
                                //Here i will take the user to the edit event screen and send it along with the request
                                Intent i = new Intent(getBaseContext(), edit_event.class);
                                i.putExtra("event", event.getID());
                                startActivity(i);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                        break;
                    case R.id.DeleteEventButton:
                        Engine.deleteEvent(ID.getText().toString());
                        //update the GUI
                        ListAdapter adapt = new CustomeEventAdapter(activity, Engine.getEventsList());
                        ListView list = (ListView) findViewById(R.id.EventsList);
                       if(list != null)
                       {
                           list.setAdapter(adapt);
                       }else{
                           Log.d(TAG,"List is null");
                       }

                        break;
                }

                return true;
            }
        });
        popupMenu.inflate(R.menu.event_options_menu);
        popupMenu.show();
    }



}
