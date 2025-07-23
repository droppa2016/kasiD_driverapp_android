package co.za.kasi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class VehicleType extends DataObject implements Serializable {

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("kilometer")
    private double kilometer;

    @SerializedName("basePrice")
    private double basePrice;

    @SerializedName("capacity")
    private double capacity;

    @SerializedName("description")
    private String description;

    @SerializedName("name")
    private String name;

    public VehicleType(){}

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getKilometer() {
        return kilometer;
    }

    public void setKilometer(double kilometer) {
        this.kilometer = kilometer;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
