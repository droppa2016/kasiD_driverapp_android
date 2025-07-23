package co.za.kasi.model;

import java.io.Serializable;

/**
 * Created by Codetribe 014 on 2018/01/25.
 */

public class Drop extends DataObject implements Serializable {

    private String cost;

    private String refNumber;

    private DriverLocationDTO from;

    private DriverLocationDTO to;

    //private LocalDateTime when;
    private String when;

    private String notes;

    private boolean isPayed;

    private String paymentType;

    public String getCost() {return cost;}

    public void setCost(String cost) {this.cost = cost;}

    public String getRefNumber() {return refNumber;}

    public void setRefNumber(String refNumber) {this.refNumber = refNumber;}

    public DriverLocationDTO getFrom() {return from;}

    public void setFrom(DriverLocationDTO from) {this.from = from;}

    public DriverLocationDTO getTo() {return to;}

    public void setTo(DriverLocationDTO to) {this.to = to;}

    public String getWhen() {return when;}

    public void setWhen(String when) {this.when = when;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}

    public boolean isPayed() {
        return isPayed;
    }

    public void setIsPayed(boolean payed) {
        isPayed = payed;
    }

    public String getPaymentType() {return paymentType;}

    public void setPaymentType(String paymentType) {this.paymentType = paymentType;}
}
