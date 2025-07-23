package co.za.kasi.model;


public class Vehicle extends DataObject {

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

    public Vehicle(String registrationNumber, String make, String model, boolean hasCanopy) {
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.hasCanopy = hasCanopy;
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
}
