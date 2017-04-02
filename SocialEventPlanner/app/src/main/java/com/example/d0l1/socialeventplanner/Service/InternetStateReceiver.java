package com.example.d0l1.socialeventplanner.Service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetStateReceiver extends BroadcastReceiver {

    final String TAG="Fahad";
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE );
        //http://stackoverflow.com/questions/15546712/unfortunately-app-is-getting-stopped-while-checking-for-network/15546897#15546897
        NetworkInfo activeNetInfo =connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();

        if(isConnected)
        {
            // Do your work.
            Log.d("Fahad","Internet is connected");
                if( EventNotificationService.missedLocationChange)
                {
                    Log.d("Fahad","we missed a check for the events due to lack of internet connectivity");
                    //we missed a check for the events due to lack of internet activity
                    EventNotificationService.checkAPI(null);
                }
        }
    }
}