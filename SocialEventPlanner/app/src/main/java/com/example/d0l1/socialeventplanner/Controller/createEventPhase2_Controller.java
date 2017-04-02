package com.example.d0l1.socialeventplanner.Controller;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Model.CustomContactListAdapter;
import com.example.d0l1.socialeventplanner.View.createEventPhase2;
import com.example.d0l1.socialeventplanner.View.pickContactWindow;

public class createEventPhase2_Controller implements View.OnClickListener
{
    Activity activity;

    final int requestCode=1;
    public createEventPhase2_Controller(Activity activity)
    {
        this.activity = activity;
    }

    final String TAG="Fahad";
    @Override
    public void onClick(View view)
    {

        switch(view.getId())
        {
            //This function will open an activity to pick a contact
            case R.id.addNewAttendessButton:
                // Log.d(TAG,"OpenContactsList");
                Intent i=new Intent(this.activity,pickContactWindow.class);
                // startActivity(i);
                this.activity.startActivityForResult(i, requestCode);
                break;


            default:
                    Log.d(TAG,"This is default option: "+view.getId());

        }
    }

}
