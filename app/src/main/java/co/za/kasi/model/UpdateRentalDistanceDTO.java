package co.za.kasi.model;

import java.io.Serializable;


public class UpdateRentalDistanceDTO extends DataObject implements Serializable {
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
