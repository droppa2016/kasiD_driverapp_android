package co.za.kasi.model;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking extends DataObject implements Serializable, Comparable<Booking> {

    private String dropOffOid;

    private String pickUpOid;

    private boolean isBucket;

    private String userOid;

    private String vehicleType;

    private String pickUpAddress;

    private String pickUpDate;

    private String pickUpTime;

    private String dropOffAddress;

    private double price;

    private double vatAmount;

    private double droppaFee;

    private double amountDue;

    private String phoneNo;

    private int labour;

    private String comment;

    private String timestamp;

    private String bookingCreatedDate;

    private String status;

    private int pickUpFloors;

    private int dropFloors;

    private String driverOid;

    private String trackNo;

    private DriverLocationDTO pickUpCoordinate;

    private DriverLocationDTO dropOffCoordinate;

    private String contact;

    private String contactDropOff;

    private String customerName;

    private boolean canopy;

    private Contact dropOff;

    private Contact pickUp;

    private int load;

    private String province;

    private String paymentType;

    private String dropOid;

    private String flight;

    private String transportMode;

    private boolean isItemPicked;

    private boolean isExpress;

    private double itemMass;

    private boolean isPayed;

    private String type;

    private double pickUpAmt;

    private double airlineAmt;

    private double dropOffAmt;

    private String destinationProvince;

    private boolean isDashBike;

    private String mainCityOid;

    private BookingFlightInfoDTO flightInfo;

    private List<DimensionObject> parcels;
    private int distance;

    private List<DimensionObject> parcelDimensions;

    private List<DeliveryException> deliveryException;

    public Booking() {}


    public List<DeliveryException> getDeliveryException() {
        return deliveryException;
    }

    public void setDeliveryException(List<DeliveryException> deliveryException) {
        this.deliveryException = deliveryException;
    }

    public boolean isBucket() {
        return isBucket;
    }

    public void setBucket(boolean bucket) {
        isBucket = bucket;
    }

    public boolean isDashBike() {
        return isDashBike;
    }

    public String getBookingCreatedDate() {
        return bookingCreatedDate;
    }

    public void setBookingCreatedDate(String bookingCreatedDate) {
        this.bookingCreatedDate = bookingCreatedDate;
    }

    public void setDashBike(boolean dashBike) {
        isDashBike = dashBike;
    }

    public List<DimensionObject> getParcels() { return parcels; }

    public void setParcels(List<DimensionObject> parcels) { this.parcels = parcels; }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public double getDroppaFee() {
        return droppaFee;
    }

    public void setDroppaFee(double droppaFee) {
        this.droppaFee = droppaFee;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public String getDropOffOid() {return dropOffOid;}

    public void setDropOffOid(String dropOffOid) {this.dropOffOid = dropOffOid;}

    public String getPickUpOid() {return pickUpOid;}

    public void setPickUpOid(String pickUpOid) {this.pickUpOid = pickUpOid;}

    public String getUserOid() {return userOid;}

    public void setUserOid(String userOid) {this.userOid = userOid;}

    public String getVehicleType() {return vehicleType;}

    public void setVehicleType(String vehicleType) {this.vehicleType = vehicleType;}

    public String getPickUpAddress() {return pickUpAddress;}

    public void setPickUpAddress(String pickUpAddress) {this.pickUpAddress = pickUpAddress;}

    public String getPickUpDate() {return pickUpDate;}

    public void setPickUpDate(String pickUpDate) {this.pickUpDate = pickUpDate;}

    public String getPickUpTime() {return pickUpTime;}

    public void setPickUpTime(String pickUpTime) {this.pickUpTime = pickUpTime;}

    public String getDropOffAddress() {return dropOffAddress;}

    public void setDropOffAddress(String dropOffAddress) {this.dropOffAddress = dropOffAddress;}

    public double getPrice() {return price;}

    public void setPrice(double price) {this.price = price;}

    public String getPhoneNo() {return phoneNo;}

    public void setPhoneNo(String phoneNo) {this.phoneNo = phoneNo;}

    public int getLabour() {return labour;}

    public void setLabour(int labour) {this.labour = labour;}

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public String getTimestamp() {return timestamp;}

    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public int getPickUpFloors() {return pickUpFloors;}

    public void setPickUpFloors(int pickUpFloors) {this.pickUpFloors = pickUpFloors;}

    public int getDropFloors() {return dropFloors;}

    public void setDropFloors(int dropFloors) {this.dropFloors = dropFloors;}

    public String getDriverOid() {return driverOid;}

    public void setDriverOid(String driverOid) {this.driverOid = driverOid;}

    public String getTrackNo() {return trackNo;}

    public void setTrackNo(String trackNo) {this.trackNo = trackNo;}

    public DriverLocationDTO getPickUpCoordinate() {return pickUpCoordinate;}

    public void setPickUpCoordinate(DriverLocationDTO pickUpCoordinate) {this.pickUpCoordinate = pickUpCoordinate;}

    public DriverLocationDTO getDropOffCoordinate() {return dropOffCoordinate;}

    public void setDropOffCoordinate(DriverLocationDTO dropOffCoordinate) {this.dropOffCoordinate = dropOffCoordinate;}

    public String getContact() {return contact;}

    public void setContact(String contact) {this.contact = contact;}

    public String getCustomerName() {return customerName;}

    public void setCustomerName(String customerName) {this.customerName = customerName;}

    public String getContactDropOff() {return contactDropOff;}

    public void setContactDropOff(String contactDropOff) {this.contactDropOff = contactDropOff;}

    public boolean isCanopy() { return canopy; }

    public void setCanopy(boolean canopy) { this.canopy = canopy; }

    public int getLoad() { return load; }

    public void setLoad(int load) { this.load = load;}

    public Contact getDropOff() {
        return dropOff;
    }

    public void setDropOff(Contact dropOff) {
        this.dropOff = dropOff;
    }

    public String getProvince() { return province; }

    public void setProvince(String province) { this.province = province; }

    public String getPaymentType() { return paymentType; }

    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getDropOid() { return dropOid; }

    public void setDropOid(String dropOid) { this.dropOid = dropOid; }

    public Contact getPickUp() { return pickUp; }

    public void setPickUp(Contact pickUp) { this.pickUp = pickUp; }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public boolean isItemPicked() { return isItemPicked; }

    public void setItemPicked(boolean itemPicked) { isItemPicked = itemPicked; }

    public boolean isExpress() { return isExpress; }

    public void setExpress(boolean express) { isExpress = express; }

    public double getItemMass() { return itemMass; }

    public void setItemMass(double itemMass) { this.itemMass = itemMass; }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPickUpAmt() {
        return pickUpAmt;
    }

    public void setPickUpAmt(double pickUpAmt) {
        this.pickUpAmt = pickUpAmt;
    }

    public double getAirlineAmt() {
        return airlineAmt;
    }

    public void setAirlineAmt(double airlineAmt) {
        this.airlineAmt = airlineAmt;
    }

    public double getDropOffAmt() {
        return dropOffAmt;
    }

    public void setDropOffAmt(double dropOffAmt) {
        this.dropOffAmt = dropOffAmt;
    }

    public String getDestinationProvince() {
        return destinationProvince;
    }

    public void setDestinationProvince(String destinationProvince) {
        this.destinationProvince = destinationProvince;
    }

    public BookingFlightInfoDTO getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(BookingFlightInfoDTO flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getMainCityOid() {
        return mainCityOid;
    }

    public void setMainCityOid(String mainCityOid) {
        this.mainCityOid = mainCityOid;
    }

    public List<DimensionObject> getParcelDimensions() {
        return parcelDimensions;
    }

    public void setParcelDimensions(List<DimensionObject> parcelDimensions) {
        this.parcelDimensions = parcelDimensions;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "dropOffOid='" + dropOffOid + '\'' +
                ", pickUpOid='" + pickUpOid + '\'' +
                ", isBucket=" + isBucket +
                ", userOid='" + userOid + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", pickUpAddress='" + pickUpAddress + '\'' +
                ", pickUpDate='" + pickUpDate + '\'' +
                ", pickUpTime='" + pickUpTime + '\'' +
                ", dropOffAddress='" + dropOffAddress + '\'' +
                ", price=" + price +
                ", vatAmount=" + vatAmount +
                ", droppaFee=" + droppaFee +
                ", amountDue=" + amountDue +
                ", phoneNo='" + phoneNo + '\'' +
                ", labour=" + labour +
                ", comment='" + comment + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", bookingCreatedDate='" + bookingCreatedDate + '\'' +
                ", status='" + status + '\'' +
                ", pickUpFloors=" + pickUpFloors +
                ", dropFloors=" + dropFloors +
                ", driverOid='" + driverOid + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", pickUpCoordinate=" + pickUpCoordinate +
                ", dropOffCoordinate=" + dropOffCoordinate +
                ", contact='" + contact + '\'' +
                ", contactDropOff='" + contactDropOff + '\'' +
                ", customerName='" + customerName + '\'' +
                ", canopy=" + canopy +
                ", dropOff=" + dropOff +
                ", pickUp=" + pickUp +
                ", load=" + load +
                ", province='" + province + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", dropOid='" + dropOid + '\'' +
                ", flight='" + flight + '\'' +
                ", transportMode='" + transportMode + '\'' +
                ", isItemPicked=" + isItemPicked +
                ", isExpress=" + isExpress +
                ", itemMass=" + itemMass +
                ", isPayed=" + isPayed +
                ", type='" + type + '\'' +
                ", pickUpAmt=" + pickUpAmt +
                ", airlineAmt=" + airlineAmt +
                ", dropOffAmt=" + dropOffAmt +
                ", destinationProvince='" + destinationProvince + '\'' +
                ", isDashBike=" + isDashBike +
                ", mainCityOid='" + mainCityOid + '\'' +
                ", flightInfo=" + flightInfo +
                ", parcels=" + parcels +
                /*", parcelDimensions=" + parcelDimensions +*/
                ", distance=" + distance +
                ", deliveryException=" + deliveryException +
                '}';
    }

    @Override
    public int compareTo(@NonNull Booking booking) {
        if (getPickUpDate() == null || booking.getPickUpDate() == null)
            return 0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm a", Locale.ENGLISH);

        try {
            Date secondDate = formatter.parse(booking.getPickUpDate() + " " +booking.getPickUpTime());
            Date firstDate = formatter.parse(getPickUpDate() +  " "+getPickUpTime());
            return firstDate.compareTo(secondDate);
        } catch (ParseException e) {
            return 0;
        }

    }

}
