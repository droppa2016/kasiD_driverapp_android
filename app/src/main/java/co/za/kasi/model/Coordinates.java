package co.za.kasi.model;

import java.io.Serializable;

public class Coordinates extends DataObject implements Serializable {
    private String lat;
    private String lng;

    public Coordinates(){}

    public String getLat() {return lat;}

    public void setLat(String lat) {this.lat = lat;}

    public String getLng() {return lng;}

    public void setLng(String lng) {this.lng = lng;}


    @Override
    public String toString() {
        return "Coordinates{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
