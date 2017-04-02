package com.example.d0l1.socialeventplanner.View;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.d0l1.socialeventplanner.Controller.pickContactWindow_Controller;
import com.example.d0l1.socialeventplanner.R;
import com.example.d0l1.socialeventplanner.View.Model.CustomContactaAdapter;

import java.util.ArrayList;

public class pickContactWindow extends AppCompatActivity {
    final String TAG="Fahad";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
      ArrayList<String> selectedNames=new ArrayList<String>();
    private  ListView contactsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_contact_window);
        contactsListView=(ListView)findViewById(R.id.contactsListView);

        //Request the  android.permission.READ_CONTACTS
        int permissionCheck = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_CONTACTS);

        if (PackageManager.PERMISSION_DENIED == permissionCheck)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else
        {
            displayContactList();
        }

        //Set Listener
        findViewById(R.id.getNamesButton).setOnClickListener(new pickContactWindow_Controller(this));

    }


    //http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
    //This function will query the database to get all the contacts
    private void displayContactList()
    {
        ArrayList<String> names=new ArrayList<String>();
        try {
            ContentResolver ContentRe = getContentResolver();
            Cursor cur = ContentRe.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                       // Log.d(TAG, name);
                        names.add(name);
                    }
                }
               ListAdapter adapt=new CustomContactaAdapter(this,names);

                contactsListView.setAdapter(adapt);
            }
        }catch(Exception e)
        {
            Log.d(TAG,e.getMessage());
        }
    }


    //https://developer.android.com/training/permissions/requesting.html#perm-request
    //Asking for permission to access the contact list
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d(TAG,"permission was granted");
                    displayContactList();
                } else
                {
                    Toast.makeText(this, "Error: This feature needs Access to your Contacts", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"permission denied");
                }
                return;
            }
        }
    }

}
