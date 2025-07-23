package co.za.kasi.model;

import java.util.List;

public class CompleteBookingDTO extends DataObject {

    private List<Booking> bookings;

    private List<BucketBooking> bucketBookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<BucketBooking> getBucketBookings() {
        return bucketBookings;
    }

    public void setBucketBookings(List<BucketBooking> bucketBookings) {
        this.bucketBookings = bucketBookings;
    }
}
