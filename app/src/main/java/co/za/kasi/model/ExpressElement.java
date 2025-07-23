package co.za.kasi.model;

public class ExpressElement extends DataObject {

     private String bucketOid;

     private String bookingOid;

    private String status;

    public String getBucketOid() {
        return bucketOid;
    }

    public void setBucketOid(String bucketOid) {
        this.bucketOid = bucketOid;
    }

    public String getBookingOid() {
        return bookingOid;
    }

    public void setBookingOid(String bookingOid) {
        this.bookingOid = bookingOid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
