package co.za.kasi.model;

import java.io.Serializable;


public class DimensionObject extends DataObject implements Serializable {

    String parcel_number;
    double parcel_length;
    double parcel_breadth;
    double parcel_height;
    double parcel_mass;
    String noBox;

    public DimensionObject() {
    }

    public String getParcel_number() {
        return parcel_number;
    }

    public void setParcel_number(String parcel_number) {
        this.parcel_number = parcel_number;
    }

    public double getParcel_length() {
        return parcel_length;
    }

    public void setParcel_length(double parcel_length) {
        this.parcel_length = parcel_length;
    }

    public double getParcel_breadth() {
        return parcel_breadth;
    }

    public void setParcel_breadth(double parcel_breadth) {
        this.parcel_breadth = parcel_breadth;
    }

    public double getParcel_height() {
        return parcel_height;
    }

    public void setParcel_height(double parcel_height) {
        this.parcel_height = parcel_height;
    }

    public double getParcel_mass() {
        return parcel_mass;
    }

    public void setParcel_mass(int parcel_mass) {
        this.parcel_mass = parcel_mass;
    }

    public String getNoBox() {
        return noBox;
    }

    public void setNoBox(String noBox) {
        this.noBox = noBox;
    }
}
