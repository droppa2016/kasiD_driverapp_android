package co.za.kasi.model;

public class PushNotificationDTO {

    private String body;
    private String title;

    public PushNotificationDTO() {
    }

    public PushNotificationDTO(String body, String title) {
        this.body = body;
        this.title = title;
    }
}
