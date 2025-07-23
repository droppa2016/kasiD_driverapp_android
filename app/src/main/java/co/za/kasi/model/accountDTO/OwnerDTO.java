package co.za.kasi.model.accountDTO;

import java.io.Serializable;

public class OwnerDTO implements Serializable {
    private String oid;
    private String businessKey;
    private String deviceId;
    private String mobile;
    private boolean mobileVerified;
    private String firstName;
    private String surname;
    private String rsaId;
    private String email;
    private boolean emailVerified;
    private String documentOid;
    private String creationStamp;
    private String retailId;
    private String userAccountOid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
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

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getDocumentOid() {
        return documentOid;
    }

    public void setDocumentOid(String documentOid) {
        this.documentOid = documentOid;
    }

    public String getCreationStamp() {
        return creationStamp;
    }

    public void setCreationStamp(String creationStamp) {
        this.creationStamp = creationStamp;
    }

    public String getRetailId() {
        return retailId;
    }

    public void setRetailId(String retailId) {
        this.retailId = retailId;
    }

    public String getUserAccountOid() {
        return userAccountOid;
    }

    public void setUserAccountOid(String userAccountOid) {
        this.userAccountOid = userAccountOid;
    }

    @Override
    public String toString() {
        return "OwnerDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobileVerified=" + mobileVerified +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", rsaId='" + rsaId + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", documentOid='" + documentOid + '\'' +
                ", creationStamp='" + creationStamp + '\'' +
                ", retailId='" + retailId + '\'' +
                ", userAccountOid='" + userAccountOid + '\'' +
                '}';
    }
}
