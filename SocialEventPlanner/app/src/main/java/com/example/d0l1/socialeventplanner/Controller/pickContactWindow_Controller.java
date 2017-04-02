package com.example.d0l1.socialeventplanner.Controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.createEventPhase2;
import com.example.d0l1.socialeventplanner.View.pickContactWindow;

import java.util.ArrayList;

public class pickContactWindow_Controller implements View.OnClickListener {
    static ArrayList<String> selectedNames=new ArrayList<String>();
    String TAG="Fahad";
    Activity activity;

    public pickContactWindow_Controller(Activity activity)
    {
        this.activity=activity;
        //this.selectedNames=selectedNames;
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.nameCheckBox:
                CheckBox selectedName=(CheckBox) view;
                if(selectedName.isChecked())
                {
                    //selectedNames
                    selectedNames.add(selectedName.getText().toString());
                   Log.d(TAG,"Name has been added to the list");
                   // Log.d(TAG,selectedName.getText().toString());

                }else{
                    selectedNames.remove(selectedName.getText().toString());
                    Log.d(TAG,"Name has been removed from the list");
                }
                break;

            //This function will handle a on click button to send the picked contacts back to the event phase2
            case R.id.getNamesButton:

                Intent intent = new Intent(view.getContext(), createEventPhase2.class);
                try {

                    if (selectedNames.size() < 0)
                    {
                       this.activity.setResult(this.activity.RESULT_CANCELED, intent);

                        this.activity.finish();
                    }
                    intent.putStringArrayListExtra("namesList", selectedNames);

                    this.activity.setResult(this.activity.RESULT_OK, intent);
                    this.activity.finish();
                    selectedNames.clear();
                }catch(Exception e)
                {
                    Log.d(TAG,e.getMessage());
                }
                this.activity.setResult(this.activity.RESULT_CANCELED, intent);
                this.activity.finish();
                break;

            default:
                Log.d("Fahad","Default option: "+view.getId());
        }

    }
}
