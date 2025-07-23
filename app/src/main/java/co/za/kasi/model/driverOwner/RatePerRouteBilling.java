package co.za.kasi.model.driverOwner;

import java.util.List;

public class RatePerRouteBilling {

    String route;
    String vehicleReg;
    String branch;
    String driverName;

    List<ParcelBillingDTO> parcels;
    int totalWaybills;
    double totalDue;
    double totalTiers;
    double fuelCharge;
    double fixedAmount;


    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getTotalWaybills() {
        return totalWaybills;
    }

    public void setTotalWaybills(int totalWaybills) {
        this.totalWaybills = totalWaybills;
    }

    public double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }

    public double getTotalTiers() {
        return totalTiers;
    }

    public void setTotalTiers(double totalTiers) {
        this.totalTiers = totalTiers;
    }

    public double getFuelCharge() {
        return fuelCharge;
    }

    public void setFuelCharge(double fuelCharge) {
        this.fuelCharge = fuelCharge;
    }

    public double getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public List<ParcelBillingDTO> getParcels() {
        return parcels;
    }

    public void setParcels(List<ParcelBillingDTO> parcels) {
        this.parcels = parcels;
    }

    @Override
    public String toString() {
        return "RatePerRouteBilling{" +
                "route='" + route + '\'' +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", branch='" + branch + '\'' +
                ", driverName='" + driverName + '\'' +
                ", totalWaybills=" + totalWaybills +
                ", totalDue=" + totalDue +
                ", totalTiers=" + totalTiers +
                ", fuelCharge=" + fuelCharge +
                ", fixedAmount=" + fixedAmount +
                ", parcels=" + parcels +
                '}';
    }
}

