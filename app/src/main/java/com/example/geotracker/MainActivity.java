package com.example.geotracker;

import android.Manifest;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button tracker;
    Button trackAnzeigen;
    TextView trackView;
    Boolean trackingStatus;
    AppDatabase database;
    LocationManager lm;
    LocationListener listener;

    List<Point> Track = new ArrayList<Point>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tracker = findViewById(R.id.tracker);
        trackAnzeigen = findViewById(R.id.trackAnzeigen);
        trackView = findViewById(R.id.trackView);

        tracker.setOnClickListener(this);
        trackAnzeigen.setOnClickListener(this);
        //trackAnzeigen.setEnabled(false);


        String[] permissionRequest = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissionRequest, 101);


        trackingStatus = false;

        database = Room.databaseBuilder(this, AppDatabase.class, "TrackDatabase")
                .allowMainThreadQueries()
                .build();

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new MyLocationListener();

    }

    public void updateWithNewLocation(Location loc){
        if (loc != null) {
            Log.i("LOCATION_LOG", "updateWithNewLocation called with loc: " + loc.getLatitude() + ", " + loc.getLongitude());
            trackView.setText("Location: " + loc.getLatitude() + ", " + loc.getLongitude());
        } else {
            Log.i("LOCATION_LOG", "Location was null...");
        }
    }

    public void addLocationToDataBase(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Date date = new Date();
        long timestamp = date.getTime();

        Point point = new Point();
        point.setLatitude(latitude);
        point.setLongidude(longitude);
        point.setTimestamp(timestamp);

        database.getTrackingDao().insertPoint(point);

    }

    @Override
    public void onClick(View v) {

        if (v == this.tracker) {
            this.trackingStatus = !this.trackingStatus;
            if (this.trackingStatus) {
                this.tracker.setText("Tracking stoppen");

                database.getTrackingDao().deletePoints();

                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location last = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateWithNewLocation(last);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
                }



            } else {
                this.tracker.setText("Tracking starten");
                lm.removeUpdates(listener);
            }


        }

        if (v == this.trackAnzeigen) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }


    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            addLocationToDataBase(location);
            updateWithNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            switch (status) {
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.AVAILABLE:
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            trackingStatus = false;
            tracker.setText("Tracking starten");
            Toast.makeText(MainActivity.this, "Schalten Sie GPS wieder ein!", Toast.LENGTH_SHORT).show();
        }
    }
}
