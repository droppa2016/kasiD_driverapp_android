package co.za.kasi.model;

public class RequestBodyDTO extends DataObject{

    private String driverOid;
    private String route;
    private String vehicleRegistration;
    private String companyOid;
    private int startMonth;
    private int startYear;
    private int endMonth;
    private int endYear;
    private int numRecords;
    private int startRecord;
    private boolean getNewRecords;

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public String getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(String companyOid) {
        this.companyOid = companyOid;
    }

    public String getDriverOid() {
        return driverOid;
    }

    public void setDriverOid(String driverOid) {
        this.driverOid = driverOid;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void setNumRecords(int numRecords) {
        this.numRecords = numRecords;
    }

    public int getStartRecord() {
        return startRecord;
    }

    public void setStartRecord(int startRecord) {
        this.startRecord = startRecord;
    }

    public boolean getGetNewRecords() {
        return getNewRecords;
    }

    public void setGetNewRecords(boolean getNewRecords) {
        this.getNewRecords = getNewRecords;
    }
}
