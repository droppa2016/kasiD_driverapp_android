package co.za.kasi.model.accountDTO;

public class VehicleDTO {

    private String oid;
    private String businessKey;
    private String registrationNumber;
    private String vinNumber;
    private String make;
    private String color;
    private String type;
    private String model;
    private String driverOid;
    private String ownerOid;
    private String ownerName;
    private boolean hasCanopy;
    private String diskDate;
    private String ownerMobile;

    // Add getters and setters here


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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public String getOwnerOid() {
        return ownerOid;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isHasCanopy() {
        return hasCanopy;
    }

    public void setHasCanopy(boolean hasCanopy) {
        this.hasCanopy = hasCanopy;
    }

    public String getDiskDate() {
        return diskDate;
    }

    public void setDiskDate(String diskDate) {
        this.diskDate = diskDate;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    @Override
    public String toString() {
        return "VehicleDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", vinNumber='" + vinNumber + '\'' +
                ", make='" + make + '\'' +
                ", color='" + color + '\'' +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                ", driverOid='" + driverOid + '\'' +
                ", ownerOid='" + ownerOid + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", hasCanopy=" + hasCanopy +
                ", diskDate='" + diskDate + '\'' +
                ", ownerMobile='" + ownerMobile + '\'' +
                '}';
    }
}
