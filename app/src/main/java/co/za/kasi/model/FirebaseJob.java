package co.za.kasi.model;

import java.io.Serializable;


public class FirebaseJob extends DataObject implements Serializable {

    private String clientID;
    private String destinationLocation;
    private String distancePickDest;
    private String distanceToDrop;
    private String dropOid;
    private String pickUpLocation;
    private long price;
    private boolean requestExpress;
    private String requestVehicle;
    private String status;
    private String time;
    private String timePickDest;
    private String key;
    private boolean isStarted;
    private String contact;
    private String customerName;
    private String contactDropOff;
    private String bookingOid;
    private boolean isBooking;


    //@Exclude
    private Coordinates pickupCoordinates;
    //@Exclude
    private Coordinates dropoffCoordinates;

    public String getClientID() {return clientID;}

    public void setClientID(String clientID) {this.clientID = clientID;}

    public String getDestinationLocation() {return destinationLocation;}

    public void setDestinationLocation(String destinationLocation) {this.destinationLocation = destinationLocation;}

    public String getDistancePickDest() {return distancePickDest;}

    public void setDistancePickDest(String distancePickDest) {this.distancePickDest = distancePickDest;}

    public String getDistanceToDrop() {return distanceToDrop;}

    public void setDistanceToDrop(String distanceToDrop) {this.distanceToDrop = distanceToDrop;}

    public String getDropOid() {return dropOid;}

    public void setDropOid(String dropOid) {this.dropOid = dropOid;}

    public String getPickUpLocation() {return pickUpLocation;}

    public void setPickUpLocation(String pickUpLocation) {this.pickUpLocation = pickUpLocation;}

    public long getPrice() {return price;}

    public void setPrice(long price) {this.price = price;}

    public boolean getRequestExpress() {return requestExpress;}

    public void setRequestExpress(boolean requestExpress) {this.requestExpress = requestExpress;}

    public String getRequestVehicle() {return requestVehicle;}

    public void setRequestVehicle(String requestVehicle) {this.requestVehicle = requestVehicle;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public String getTime() {return time;}

    public void setTime(String time) {this.time = time;}

    public String getTimePickDest() {return timePickDest;}

    public void setTimePickDest(String timePickDest) {this.timePickDest = timePickDest;}

    //public boolean isRequestExpress() {return requestExpress;}

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}

    public Coordinates getPickupCoordinates() {
        return pickupCoordinates;
    }

    public void setPickupCoordinates(Coordinates pickupCoordinates) {
        this.pickupCoordinates = pickupCoordinates;
    }

    public Coordinates getDropoffCoordinates() {
        return dropoffCoordinates;
    }

    public void setDropoffCoordinates(Coordinates dropoffCoordinates) {
        this.dropoffCoordinates = dropoffCoordinates;
    }

    public boolean isStarted() {return isStarted;}

    public void setStarted(boolean started) {isStarted = started;}

    public String getContact() {return contact;}

    public void setContact(String contact) {this.contact = contact;}

    public String getCustomerName() {return customerName;}

    public void setCustomerName(String customerName) {this.customerName = customerName;}

    public String getContactDropOff() {return contactDropOff;}

    public void setContactDropOff(String contactDropOff) {this.contactDropOff = contactDropOff;}

    public boolean isBooking() {return isBooking;}

    public void setBooking(boolean booking) {isBooking = booking;}

    public String getBookingOid() {return bookingOid;}

    public void setBookingOid(String bookingOid) {this.bookingOid = bookingOid;}

    @Override
    public String toString() {
        return "FirebaseJob{" +
                "clientID='" + clientID + '\'' +
                ", destinationLocation='" + destinationLocation + '\'' +
                ", distancePickDest='" + distancePickDest + '\'' +
                ", distanceToDrop='" + distanceToDrop + '\'' +
                ", dropOid='" + dropOid + '\'' +
                ", pickUpLocation='" + pickUpLocation + '\'' +
                ", price=" + price +
                ", requestExpress=" + requestExpress +
                ", requestVehicle='" + requestVehicle + '\'' +
                ", status='" + status + '\'' +
                ", time='" + time + '\'' +
                ", timePickDest='" + timePickDest + '\'' +
                '}';
    }
}
