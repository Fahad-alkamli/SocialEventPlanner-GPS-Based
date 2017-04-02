package com.example.d0l1.socialeventplanner;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String desCoordinates=null;
    final String TAG="Fahad";
    String currentCoordinates=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if( getIntent().getExtras() != null)
        {
            desCoordinates=getIntent().getExtras().get("extra").toString().trim();

            currentCoordinates=getIntent().getExtras().get("currentLocation").toString().trim();


            Log.d(TAG,currentCoordinates);
            Log.d(TAG,desCoordinates);

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            if (currentCoordinates == null || desCoordinates==null)
            {
                return;
            }

            LatLng currentLocation = new LatLng(Double.parseDouble(currentCoordinates.trim().split(",")[0]), Double.parseDouble(currentCoordinates.trim().split(",")[1]));
            LatLng destination = new LatLng(Double.parseDouble(desCoordinates.trim().split(",")[0]), Double.parseDouble(desCoordinates.trim().split(",")[1]));

/*
Testing

 */
            mMap.addPolyline(new PolylineOptions()
                    .add(currentLocation, destination)
                    .width(5)
                    .color(Color.RED));


            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are Here"));
            //http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLocation)      // Sets the center of the map to location user
                   .zoom(17)                   // Sets the zoom
                    .bearing(280)                // Sets the orientation of the camera to east
                    .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }catch(Exception e)
        {
            Log.d("Fahad",e.getMessage());
        }
    }
}
