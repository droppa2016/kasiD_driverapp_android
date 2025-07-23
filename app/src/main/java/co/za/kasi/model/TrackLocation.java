package co.za.kasi.model;

import java.io.Serializable;

public class TrackLocation implements Serializable {

    private double latitude;
    private double longitude;
    private float bearing;

    public TrackLocation(double latitude, double longitude, float bearing){
        this.bearing = bearing;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TrackLocation(){}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

}