package com.example.geotracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Point {

    protected double longidude;
    protected double latitude;

    @PrimaryKey()
    protected long timestamp;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLongidude() {
        return longidude;
    }

    public void setLongidude(double longidude) {
        this.longidude = longidude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
