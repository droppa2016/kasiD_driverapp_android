package co.za.kasi.services;

import java.text.DateFormat;
import java.util.Date;

/**
 *  * Created by alejandro.tkachuk
 *   */

public class GPSPoint {

    private double lat, lon;
    private Date date;
    private String lastUpdate;

    public GPSPoint(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
        this.date = new Date();
        this.lastUpdate = DateFormat.getTimeInstance().format(this.date);
    }

    public GPSPoint(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public double getLatitude() {
        return lat;
    }

    public Date getDate() {
        return date;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public double getLongitude() {

        return lon;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }
}

