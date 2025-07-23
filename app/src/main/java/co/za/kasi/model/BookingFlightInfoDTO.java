package co.za.kasi.model;

public class BookingFlightInfoDTO extends DataObject {

    private String bookingOid;

    private String  flightNo;

    private String ewaybill;

    private String departureDate;

    private String arrivalDate;

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getEwaybill() {
        return ewaybill;
    }

    public void setEwaybill(String ewaybill) {
        this.ewaybill = ewaybill;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getBookingOid() {
        return bookingOid;
    }

    public void setBookingOid(String bookingOid) {
        this.bookingOid = bookingOid;
    }
}
