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
import com.example.d0l1.socialeventplanner.View.editEventPhase2;
import com.example.d0l1.socialeventplanner.View.pickContactWindow;

import java.util.ArrayList;

public class editEventPhase2_Controller implements View.OnClickListener{
    Activity activity;
    final int requestCode=1;
    ArrayList<String> namesList;
    public editEventPhase2_Controller(Activity activity, ArrayList<String> namesList) {
        this.activity = activity;
        this.namesList=namesList;
    }

    @Override
    public void onClick(View view)
    {
        try {
            switch (view.getId()) {
                //This function will open an activity to pick a contact
                case R.id.addNewAttendessButton:
                    // Log.d(TAG,"OpenContactsList");
                    Intent i = new Intent(this.activity, pickContactWindow.class);
                    // startActivity(i);
                    this.activity.startActivityForResult(i, requestCode);
                    break;

                //Deletes a contact from the attendance for a specific event
                case R.id.deleteItemButton:
                    //http://stackoverflow.com/questions/3675238/androidfindviewbyid-how-can-i-get-view-of-a-textview-through-a-listener-of-a
                    View parentView = (View) view.getParent();
                    TextView name = (TextView) parentView.findViewById(R.id.nameTextView);
                    // Log.d(TAG,"Name clicked: "+name.getText().toString());
                    //Delete the name from the list
                    Log.d("Fahad",Boolean.toString(namesList.remove(name.getText().toString())));
                    for(String temp:namesList)
                    {
                        Log.d("Fahad",temp);
                    }
                    //Update the listview
                    ListAdapter adapt = new CustomContactListAdapter(this.activity,namesList);
                    ListView AttendessListBox = (ListView) this.activity.findViewById(R.id.AttendessListBox);
                    AttendessListBox.setAdapter(adapt);
                    break;
                default:
                    Log.d("Fahad", "This is default option: " + view.getId());

            }
        }catch(Exception e)
        {
            Log.d("Fahad",e.getMessage());
        }
    }
}
