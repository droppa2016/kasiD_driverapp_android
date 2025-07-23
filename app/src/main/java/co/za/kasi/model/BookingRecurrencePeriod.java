package co.za.kasi.model;


public enum BookingRecurrencePeriod {
	
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    WEEKDAY("Weekday"),
    YEARLY("Yearly");

    private String description;

    BookingRecurrencePeriod(String description) {
        this.description = description;
    }

    public String description() { return description; }
    
    public static BookingRecurrencePeriod findByName(String name) {
    	;
        for (BookingRecurrencePeriod v : values()) {
            if (v.name().equals(name.toUpperCase())) {
                return v;
            }
        }
        return null;
    }
}
