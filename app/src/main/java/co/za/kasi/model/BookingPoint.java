package co.za.kasi.model;

public class BookingPoint {

    private String status;

    private String bookingOid;

    private boolean isBucket;

    private String pointType;

    private boolean isPicked;

    private double latitude;

    private double longitude;

    private String address;

    private String clientName;

    private String clientPhone;

    private String instructions;

    private String type;

    private String transportType;

    private String trackNo;

    private Contact pickup;

    private Contact dropoff;

    private Booking booking;

    public BookingPoint() { }

    public boolean isBucket() {
        return isBucket;
    }

    public void setBucket(boolean bucket) {
        isBucket = bucket;
    }

    public String getTrackNo() {
        return trackNo;
    }

    public void setTrackNo(String trackNo) {
        this.trackNo = trackNo;
    }

    public String getBookingOid() {
        return bookingOid;
    }

    public void setBookingOid(String bookingOid) {
        this.bookingOid = bookingOid;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getTransportType() { return transportType; }

    public void setTransportType(String transportType) { this.transportType = transportType; }

    public Contact getPickup() {
        return pickup;
    }

    public void setPickup(Contact pickup) {
        this.pickup = pickup;
    }

    public Contact getDropoff() {
        return dropoff;
    }

    public void setDropoff(Contact dropoff) {
        this.dropoff = dropoff;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public String toString() {
        return "BookingPoint{" +
                "status='" + status + '\'' +
                ", bookingOid='" + bookingOid + '\'' +
                ", pointType='" + pointType + '\'' +
                ", isPicked=" + isPicked +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientPhone='" + clientPhone + '\'' +
                ", instructions='" + instructions + '\'' +
                ", type='" + type + '\'' +
                ", transportType='" + transportType + '\'' +
                ", trackNo='" + trackNo + '\'' +
                ", pickup=" + pickup +
                ", dropoff=" + dropoff +
                '}';
    }
}
