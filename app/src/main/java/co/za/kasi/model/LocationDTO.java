package co.za.kasi.model;

import java.io.Serializable;


public class LocationDTO extends DataObject implements Serializable {

    public String[] coordinates;

    public String address;

    public LocationDTO(){}

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
