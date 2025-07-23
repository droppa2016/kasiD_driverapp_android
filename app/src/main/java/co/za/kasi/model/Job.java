package co.za.kasi.model;

import java.io.Serializable;

public class Job  implements Serializable {
    private String pickUpLocation;
    private String destinationLocation;
    private String contact;
    private String clientID;
    private String distancePickDest;
    private String distanceToDrop;
    private String dropOid;
    private String price;
    private String requestExpress;
    private String requestVehicle;
    private String status;
    private String time;
    private String timePickDest;

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getDistancePickDest() {
        return distancePickDest;
    }

    public void setDistancePickDest(String distancePickDest) {
        this.distancePickDest = distancePickDest;
    }

    public String getDistanceToDrop() {
        return distanceToDrop;
    }

    public void setDistanceToDrop(String distanceToDrop) {
        this.distanceToDrop = distanceToDrop;
    }

    public String getDropOid() {
        return dropOid;
    }

    public void setDropOid(String dropOid) {
        this.dropOid = dropOid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRequestExpress() {
        return requestExpress;
    }

    public void setRequestExpress(String requestExpress) {
        this.requestExpress = requestExpress;
    }

    public String getRequestVehicle() {
        return requestVehicle;
    }

    public void setRequestVehicle(String requestVehicle) {
        this.requestVehicle = requestVehicle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimePickDest() {
        return timePickDest;
    }

    public void setTimePickDest(String timePickDest) {
        this.timePickDest = timePickDest;
    }
}
