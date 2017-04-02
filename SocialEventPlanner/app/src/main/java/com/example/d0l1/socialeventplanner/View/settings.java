package com.example.d0l1.socialeventplanner.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.Service.EventNotificationService;

public class settings extends AppCompatActivity {

    final String TAG="Fahad";
    EditText thresholdEditView;
    EditText waitingTimeEditView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
         thresholdEditView=(EditText) findViewById(R.id.thresholdEditView);
         waitingTimeEditView=(EditText) findViewById(R.id.waitingTimeEditView);

        //Checking user preference
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.d0l1.socialeventplanner", Context.MODE_PRIVATE);

        if (sharedPreferences.getInt("timeToWait", -1) != -1)
        {

            waitingTimeEditView.setText(Integer.toString(sharedPreferences.getInt("timeToWait", -1)));

        }
        if (sharedPreferences.getInt("addExtraMinutes", -1) != -1)
        {
            thresholdEditView.setText(Integer.toString(sharedPreferences.getInt("addExtraMinutes", -1)));
        }
    }

    public  void saveSettings(View view)
    {

        int threshold=0;

        int waitingTime=0;
        try{

            if(thresholdEditView.getText().toString().trim().replace(" ","").length()>0)
            {
                 threshold=Integer.parseInt(thresholdEditView.getText().toString().trim().replace(" ",""));
            }

            if(waitingTimeEditView.getText().toString().trim().replace(" ","").length()>0)
            {
                waitingTime=Integer.parseInt(waitingTimeEditView.getText().toString().trim().replace(" ",""));

            }


        }catch(Exception e)
        {

            Log.d(TAG,e.getMessage());
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        if(waitingTime!=0)
        {
            EventNotificationService.setTimerPeriod(waitingTime);
        }

        {
            EventNotificationService.setAddExtraMinutesPeriod(threshold);
        }
        Toast.makeText(this,"Saving done",Toast.LENGTH_LONG).show();

    }
}
