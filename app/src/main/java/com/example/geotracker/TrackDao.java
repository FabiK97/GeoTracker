package com.example.geotracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface TrackDao {

    @Query("SELECT * FROM Point ORDER BY timestamp ASC")
    public List<Point> getAllPoints();

    @Insert()
    public void insertPoint(Point point);

    @Query("DELETE FROM Point")
    public void deletePoints();

}
