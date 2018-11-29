package com.example.geotracker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    MapView map;
    List<Point> track = new ArrayList<Point>();
    List<GeoPoint> geoTrack = new ArrayList<GeoPoint>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //setup MapView
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(17f);
        GeoPoint fhku  = new GeoPoint(47.583821, 12.173667);
        mapController.setCenter(fhku);

        //get Track
        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "TrackDatabase")
                .allowMainThreadQueries()
                .build();

        track = database.getTrackingDao().getAllPoints();

        //convert into GeoPoint List
        GeoPoint geoPoint;
        for(Point point : track){
            geoPoint = new GeoPoint(point.latitude, point.longidude);
            geoTrack.add(geoPoint);
            Log.d("LOCATION_LOG_GEO", "Latitude: " + point.latitude + " Longidude: " + point.longidude);
        }
        //draw Polyline

        Polyline line = new Polyline();   //see note below!
        line.setPoints(geoTrack);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        map.getOverlayManager().add(line);
        mapController.setCenter(geoTrack.get(0));

        //map.getMapCenter(geoTrack.get(0));


    }


}
