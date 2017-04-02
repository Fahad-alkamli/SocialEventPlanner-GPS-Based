package com.example.d0l1.socialeventplanner.Service;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Model.Common;
import com.example.d0l1.socialeventplanner.R;

public class AskPermission extends AppCompatActivity {

    final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_AND_GPS=5;
    /*
    This activity will be launched from the Service  to ask user for permissions
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_permission_activity);
        Log.d("Fahad","AskPermission");

        //This function will start asking for permission and wait for a reply
       Common.requestNetworkAndGPSPermissions(this);

        //finish();

    }
    //This function will get called when the user allowed  or denied the requested Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_AND_GPS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    boolean check1=ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    boolean check2=ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ;
                    boolean check3=ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED ;
                    boolean check4=ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED ;
                    if (! check1 || !check2 || !check3 || !check4)
                    {
                        //A permission is missing
                        Toast.makeText(this,"This service Require all the permissions please allow all of them.",Toast.LENGTH_LONG).show();
                        Common.requestNetworkAndGPSPermissions(this);
                        return;
                    }
                    // permission was granted, yay!

                    Log.d("Fahad","permission was granted");


                    //Check from where did this activity got launched
                    if(getIntent().getExtras() !=null)
                    {
                        switch(getIntent().getExtras().get("extra").toString())
                        {
                            case "CheckAgain":
                                EventNotificationService.checkAPI("registerGpsUpdate");
                                break;
                        }
                    }
                } else {


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Fahad","permission denied");
                  //  Toast.makeText(this,"This feature need permission, please allow them.",Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public void dismiss(View view)
    {

        finish();
    }

    public void requestPermissionAgain(View view)
    {
        Common.requestNetworkAndGPSPermissions(this);

    }
}
