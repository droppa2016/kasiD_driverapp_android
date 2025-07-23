package co.za.kasi.model;

import java.io.Serializable;

public class Person  extends DataObject implements Serializable {

    private String deviceId;

    //    Pattern pattern = Pattern.compile("0[6-8][1-4][0-9]{7}");

    private String mobile;

    private String email;

    private String firstName;

    private String surname;

    private String rsaId;

    private String retailId;

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getMobile() { return mobile; }

    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

    public String getName() { return getFirstName() + " " + getSurname();}

    public String getRsaId() { return rsaId; }

    public void setRsaId(String rsaId) { this.rsaId = rsaId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRetailId() {return retailId;}

	public void setRetailId(String retailId) {this.retailId = retailId;}

}
