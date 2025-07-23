package co.za.kasi.model;

import java.io.Serializable;

public class Driver extends DataObject implements Serializable {

    private String name;
    private String surname;
    private String  make;
    private int model;
    private String color;
    private String type;
    private String registrationNumber;
    private String username;
    private String driverOid;
    private String vehicleOid;
    private String province;
    private double ratingScore;
    private String retailTag;
    private boolean allowRental;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public String getVehicleOid() {
        return vehicleOid;
    }

    public void setVehicleOid(String vehicleOid) {
        this.vehicleOid = vehicleOid;
    }

    public String getProvince() { return province; }

    public void setProvince(String province) { this.province = province; }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getRetailTag() { return retailTag; }

    public void setRetailTag(String retailTag) { this.retailTag = retailTag; }

    public boolean isAllowRental() {
        return allowRental;
    }

    public void setAllowRental(boolean allowRental) {
        this.allowRental = allowRental;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", make='" + make + '\'' +
                ", model=" + model +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", username='" + username + '\'' +
                ", driverOid='" + driverOid + '\'' +
                ", vehicleOid='" + vehicleOid + '\'' +
                '}';
    }
}
