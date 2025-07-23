package co.za.kasi.model.superApp.a.superAppLogin;

import com.google.gson.annotations.SerializedName;

import co.za.kasi.model.DataObject;

public class SkynetDriver extends DataObject {

    private String id;

    private String createdAt;
    private String updatedAt;

    private SkynetUserAccount userAccount;

    private String province;
    private String suburb;
    private String county;
    private String identityNo;

    private String status;

    private String permanentRoute;
    @SerializedName("app_version_code")
    private String appCode;

    @SerializedName("hasLoggedInBefore")
    private boolean hasLoggedInBefore;

    public SkynetDriver() {
    }

    public SkynetDriver(String id, String createdAt, String updatedAt, SkynetUserAccount userAccount, String province, String suburb, String county, String identityNo) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userAccount = userAccount;
        this.province = province;
        this.suburb = suburb;
        this.county = county;
        this.identityNo = identityNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public SkynetUserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(SkynetUserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPermanentRoute() {
        return permanentRoute;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public boolean getHasLoggedInBefore() {
        return hasLoggedInBefore;
    }

    public void setHasLoggedInBefore(boolean hasLoggedInBefore) {
        this.hasLoggedInBefore = hasLoggedInBefore;
    }

    public void setPermanentRoute(String permanentRoute) {
        this.permanentRoute = permanentRoute;
    }

    @Override
    public String toString() {
        return "SkynetDriver{" +
                "id='" + id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", userAccount=" + userAccount +
                ", province='" + province + '\'' +
                ", suburb='" + suburb + '\'' +
                ", county='" + county + '\'' +
                ", identityNo='" + identityNo + '\'' +
                '}';
    }
}
