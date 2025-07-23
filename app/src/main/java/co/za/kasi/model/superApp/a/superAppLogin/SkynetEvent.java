package co.za.kasi.model.superApp.a.superAppLogin;

public class SkynetEvent {

    private String status;
    private String description;
    private String dateTime;

    public SkynetEvent(String status, String description, String dateTime) {
        this.status = status;
        this.description = description;
        this.dateTime = dateTime;
    }

    public SkynetEvent() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "SkynetEvent{" +
                "status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
