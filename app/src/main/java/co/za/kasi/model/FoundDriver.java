package co.za.kasi.model;

import java.io.Serializable;


public class FoundDriver extends DataObject implements Serializable {

    private double distance;
    private String key;

    public FoundDriver(){
    }

    public double getDistance() {return distance;}

    public void setDistance(double distance) {this.distance = distance;}

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}

}
