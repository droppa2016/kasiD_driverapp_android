package co.za.kasi.model;

import java.io.Serializable;

public class DriverTracking implements Serializable {

    private String time;

    private String distance;

    private double lat;

    private double lng;

    private float bearing;

    private boolean isTripStarted;

    public DriverTracking(String time, String distance, double lat, double lng, float bearing, boolean isTripStarted) {
        this.time = time;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
        this.isTripStarted = isTripStarted;
    }

    public DriverTracking() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public boolean isTripStarted() {
        return isTripStarted;
    }

    public void setTripStarted(boolean tripStarted) {
        isTripStarted = tripStarted;
    }

    @Override
    public String toString() {
        return "DriverTracking{" +
                "time='" + time + '\'' +
                ", distance='" + distance + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", bearing=" + bearing +
                '}';
    }
}
