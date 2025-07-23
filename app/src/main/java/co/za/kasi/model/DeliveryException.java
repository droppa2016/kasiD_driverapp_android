package co.za.kasi.model;

import java.io.Serializable;

public class DeliveryException extends DataObject implements Serializable {

    private String type;
    private String reason;
    private String date;
    /*private List<Coordinates> Coordinates;*/
    private String bucketTrackNo;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

/*    public List<co.za.droppa.model.Coordinates> getCoordinates() {
        return Coordinates;
    }

    public void setCoordinates(List<co.za.droppa.model.Coordinates> coordinates) {
        Coordinates = coordinates;
    }*/

    public String getBucketTrackNo() {
        return bucketTrackNo;
    }

    public void setBucketTrackNo(String bucketTrackNo) {
        this.bucketTrackNo = bucketTrackNo;
    }

    @Override
    public String toString() {
        return "DeliveryException{" +
                "type='" + type + '\'' +
                ", reason='" + reason + '\'' +
                ", date='" + date + '\'' +
                ", bucketTrackNo='" + bucketTrackNo + '\'' +
                '}';
    }
}
