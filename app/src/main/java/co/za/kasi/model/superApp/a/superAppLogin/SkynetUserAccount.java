package co.za.kasi.model.superApp.a.superAppLogin;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.za.kasi.model.DataObject;

public class SkynetUserAccount extends DataObject {

    private String createdAt;
    private String updatedAt;
    @SerializedName("id")
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;

    private ArrayList<RolesAndPermissions> rolesAndPermissions;

    private String status;

    public SkynetUserAccount(String createdAt, String updatedAt, String email, String firstName, String lastName, String mobileNumber, ArrayList<RolesAndPermissions> rolesAndPermissions, String status) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.rolesAndPermissions = rolesAndPermissions;
        this.status = status;
    }

    public SkynetUserAccount() {
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<RolesAndPermissions> getRolesAndPermissions() {
        return rolesAndPermissions;
    }

    public void setRolesAndPermissions(ArrayList<RolesAndPermissions> rolesAndPermissions) {
        this.rolesAndPermissions = rolesAndPermissions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SkynetUserAccount{" +
                "createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", rolesAndPermissions=" + rolesAndPermissions +
                ", status='" + status + '\'' +
                '}';
    }
}
