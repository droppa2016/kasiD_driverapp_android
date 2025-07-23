package co.za.kasi.enums;


public enum BookingStatus {
	
    COMPLETE("Booking successfully complete"),
    ACCEPTED("Identifies a booking that is accepted by driver"),
    AWAITING_DRIVER("Awaiting driver to be assigned"),
    IN_TRANSACT("Driver in transaction"),
    INVALID("Awaiting Password Reset"),
	RESERVED("Booking taken already");

    private String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String description() { return description; }
}
