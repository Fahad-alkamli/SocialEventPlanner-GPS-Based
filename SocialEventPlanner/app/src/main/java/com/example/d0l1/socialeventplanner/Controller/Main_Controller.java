package com.example.d0l1.socialeventplanner.Controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.d0l1.socialeventplanner.Model.Event;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Main;
import com.example.d0l1.socialeventplanner.View.Weekly_View;
import com.example.d0l1.socialeventplanner.View.create_new_event;
import com.example.d0l1.socialeventplanner.View.edit_event;

public class Main_Controller implements View.OnClickListener {
    final static String TAG="Fahad";
    @Override
    public void onClick(View view) {

        switch(view.getId())
        {

            case R.id.weekViewButton:
                //Handles a click on the Weekly view button to launch a weekly view activity
            {
                Intent i = new Intent(view.getContext(), Weekly_View.class);
                view.getContext().startActivity(i);
            }
            break;

            case R.id.newNoteButton:
                //Will launch create new event activity
            {
                Intent i = new Intent(view.getContext(), create_new_event.class);
                view.getContext().startActivity(i);
            }
                break;
            default:
                Log.d(TAG,"Option can't be found. "+view.getId());
        }
    }
}
