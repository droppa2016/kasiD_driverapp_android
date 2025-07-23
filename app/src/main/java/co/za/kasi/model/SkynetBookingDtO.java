package co.za.kasi.model;

import java.io.Serializable;

public class SkynetBookingDtO extends DataObject implements Serializable {

    private String oid;
    private String creationStamp;
    private String lastUpdated;
    private String freightType;
    private String vehicleRegistrationNumber;
    private String driverName;
    private String route;
    private String serviceType;
    private String supplierType;
    private String originBranch;
    private String receivingBranch;
    private String fromSuburb;
    private String toSuburb;
    private String waybillDate;
    private String waybillNumber;
    private String customerAccountNumber;
    private String customerName;
    private String podDate;
    private String contractOwnerKey;
    private int numberOfParcels;
    private int numberOfTrips;
    private double distanceKM;
    private double totalRate;
    private double freightRate;
    private double surcharges;
    private double fuelSurcharge;
    private double volumetricWeight;
    private double actualWeight;
    private boolean hasPOD;
    private boolean verified;
    private boolean exception;
    private boolean rts;
    private String businessKey;


    @Override
    public String getOid() {
        return oid;
    }

    @Override
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCreationStamp() {
        return creationStamp;
    }

    public void setCreationStamp(String creationStamp) {
        this.creationStamp = creationStamp;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getFreightType() {
        return freightType;
    }

    public void setFreightType(String freightType) {
        this.freightType = freightType;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(String supplierType) {
        this.supplierType = supplierType;
    }

    public String getOriginBranch() {
        return originBranch;
    }

    public void setOriginBranch(String originBranch) {
        this.originBranch = originBranch;
    }

    public String getReceivingBranch() {
        return receivingBranch;
    }

    public void setReceivingBranch(String receivingBranch) {
        this.receivingBranch = receivingBranch;
    }

    public String getFromSuburb() {
        return fromSuburb;
    }

    public void setFromSuburb(String fromSuburb) {
        this.fromSuburb = fromSuburb;
    }

    public String getToSuburb() {
        return toSuburb;
    }

    public void setToSuburb(String toSuburb) {
        this.toSuburb = toSuburb;
    }

    public String getWaybillDate() {
        return waybillDate;
    }

    public void setWaybillDate(String waybillDate) {
        this.waybillDate = waybillDate;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPodDate() {
        return podDate;
    }

    public void setPodDate(String podDate) {
        this.podDate = podDate;
    }

    public String getContractOwnerKey() {
        return contractOwnerKey;
    }

    public void setContractOwnerKey(String contractOwnerKey) {
        this.contractOwnerKey = contractOwnerKey;
    }

    public int getNumberOfParcels() {
        return numberOfParcels;
    }

    public void setNumberOfParcels(int numberOfParcels) {
        this.numberOfParcels = numberOfParcels;
    }

    public int getNumberOfTrips() {
        return numberOfTrips;
    }

    public void setNumberOfTrips(int numberOfTrips) {
        this.numberOfTrips = numberOfTrips;
    }

    public double getDistanceKM() {
        return distanceKM;
    }

    public void setDistanceKM(double distanceKM) {
        this.distanceKM = distanceKM;
    }

    public double getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(double totalRate) {
        this.totalRate = totalRate;
    }

    public double getFreightRate() {
        return freightRate;
    }

    public void setFreightRate(double freightRate) {
        this.freightRate = freightRate;
    }

    public double getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(double surcharges) {
        this.surcharges = surcharges;
    }

    public double getFuelSurcharge() {
        return fuelSurcharge;
    }

    public void setFuelSurcharge(double fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public double getVolumetricWeight() {
        return volumetricWeight;
    }

    public void setVolumetricWeight(double volumetricWeight) {
        this.volumetricWeight = volumetricWeight;
    }

    public double getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(double actualWeight) {
        this.actualWeight = actualWeight;
    }

    public boolean isHasPOD() {
        return hasPOD;
    }

    public void setHasPOD(boolean hasPOD) {
        this.hasPOD = hasPOD;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }

    public boolean isRts() {
        return rts;
    }

    public void setRts(boolean rts) {
        this.rts = rts;
    }

    public String isBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    @Override
    public String toString() {
        return "SkynetBookingDtO{" +
                "oid='" + oid + '\'' +
                ", creationStamp='" + creationStamp + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", freightType='" + freightType + '\'' +
                ", vehicleRegistrationNumber='" + vehicleRegistrationNumber + '\'' +
                ", driverName='" + driverName + '\'' +
                ", route='" + route + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", supplierType='" + supplierType + '\'' +
                ", originBranch='" + originBranch + '\'' +
                ", receivingBranch='" + receivingBranch + '\'' +
                ", fromSuburb='" + fromSuburb + '\'' +
                ", toSuburb='" + toSuburb + '\'' +
                ", waybillDate='" + waybillDate + '\'' +
                ", waybillNumber='" + waybillNumber + '\'' +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", podDate='" + podDate + '\'' +
                ", contractOwnerKey='" + contractOwnerKey + '\'' +
                ", numberOfParcels=" + numberOfParcels +
                ", numberOfTrips=" + numberOfTrips +
                ", distanceKM=" + distanceKM +
                ", totalRate=" + totalRate +
                ", freightRate=" + freightRate +
                ", surcharges=" + surcharges +
                ", fuelSurcharge=" + fuelSurcharge +
                ", volumetricWeight=" + volumetricWeight +
                ", actualWeight=" + actualWeight +
                ", hasPOD=" + hasPOD +
                ", verified=" + verified +
                ", exception=" + exception +
                ", rts=" + rts +
                ", businessKey='" + businessKey + '\'' +
                '}';
    }
}
