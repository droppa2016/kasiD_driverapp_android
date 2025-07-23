package co.za.kasi.model;

public class DriverLocationTracking extends DataObject {

    private String driverOid;

    private String vehicleType;

    private String provice;

    private String lastSeen;

    private DriverLocationDTO location;

    private String driverName;

    private String driverPhone;

    private String oneSignalId;

    private String mainCityOid;

    private String zone;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public DriverLocationDTO getLocation() {
        return location;
    }

    public void setLocation(DriverLocationDTO location) {
        this.location = location;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getOneSignalId() {
        return oneSignalId;
    }

    public void setOneSignalId(String oneSignalId) {
        this.oneSignalId = oneSignalId;
    }

    public String getMainCityOid() {
        return mainCityOid;
    }

    public void setMainCityOid(String mainCityOid) {
        this.mainCityOid = mainCityOid;
    }

    @Override
    public String toString() {
        return "DriverLocationTracking{" +
                "driverOid='" + driverOid + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", provice='" + provice + '\'' +
                ", lastSeen='" + lastSeen + '\'' +
                ", location=" + location +
                ", driverName='" + driverName + '\'' +
                ", driverPhone='" + driverPhone + '\'' +
                ", oneSignalId='" + oneSignalId + '\'' +
                ", mainCityOid='" + mainCityOid + '\'' +
                '}';
    }
}
