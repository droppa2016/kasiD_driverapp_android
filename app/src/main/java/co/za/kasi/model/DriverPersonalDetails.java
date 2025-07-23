package co.za.kasi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverPersonalDetails extends DataObject{
    public String deviceId;
    public String mobile;
    public String firstName;
    public String surname;
    public String rsaId;

    public String email;
    public String retailId;

    public DriverPersonalDetails( String deviceId, String mobile, String firstName, String surname, String rsaId, String email) {
        this.deviceId = deviceId;
        this.mobile = mobile;
        this.firstName = firstName;
        this.surname = surname;
        this.rsaId = rsaId;
        this.email = email;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRsaId() {
        return rsaId;
    }

    public void setRsaId(String rsaId) {
        this.rsaId = rsaId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
