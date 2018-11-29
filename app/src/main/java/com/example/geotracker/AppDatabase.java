package com.example.geotracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Point.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TrackDao getTrackingDao();

}
