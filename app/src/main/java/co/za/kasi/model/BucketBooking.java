package co.za.kasi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Codetribe 014 on 2018/01/24.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BucketBooking extends DataObject implements Serializable {

    private List<Booking> bookings;

    private Retail retail;

    private double price;

    private String driverOid;

    private String status;

    private String vehicleType;

    private String dropOid;

    private String date;
    //private String date;

    private String time;

    private String trackNo;

    private String comments;

    private String labour;

    private boolean isPayed;

    private String type;

    private boolean isInBound;

    private String failedCollectionType;

    private boolean isExpress;

    private BookingRecurrence recurrence;

    public String getFailedCollectionType() {
        return failedCollectionType;
    }

    public void setFailedCollectionType(String failedCollectionType) {
        this.failedCollectionType = failedCollectionType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Retail getRetail() {
        return retail;
    }

    public void setRetail(Retail retail) {
        this.retail = retail;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDropOid() {
        return dropOid;
    }

    public void setDropOid(String dropOid) {
        this.dropOid = dropOid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLabour() {
        return labour;
    }

    public void setLabour(String labour) {
        this.labour = labour;
    }

    public BookingRecurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(BookingRecurrence recurrence) {
        this.recurrence = recurrence;
    }

    public boolean isExpress() {
        return isExpress;
    }

    public void setExpress(boolean express) {
        isExpress = express;
    }

    public boolean isInBound() {
        return isInBound;
    }

    public void setInBound(boolean inBound) {
        isInBound = inBound;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }

    @Override
    public String toString() {
        return "BucketBooking{" + "  isInBound=" + isInBound +
                ", bookings=" + bookings +
                ", retail=" + retail +
                ", price=" + price +
                ", driverOid='" + driverOid + '\'' +
                ", status='" + status + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", dropOid='" + dropOid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", comments='" + comments + '\'' +
                ", labour='" + labour + '\'' +
                ", type='" + type + '\'' +
                ", failedCollectionType='" + failedCollectionType + '\'' +
                ", isExpress=" + isExpress +
                ", recurrence=" + recurrence +
                '}';
    }

}
