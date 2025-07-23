package co.za.kasi.model;

import java.io.Serializable;
import java.util.Arrays;

public class DriverLocationDTO extends DataObject implements Serializable {

    private String[] coordinates;

    private String address;

    public String[] getCoordinates() {return coordinates;}

    public void setCoordinates(String[] coordinates) {this.coordinates = coordinates;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    @Override
    public String toString() {
        return "Location{" +
                "coordinates=" + Arrays.toString(coordinates) +
                ", address='" + address + '\'' +
                '}';
    }
}
