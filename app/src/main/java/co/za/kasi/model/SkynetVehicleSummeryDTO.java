package co.za.kasi.model;

public class SkynetVehicleSummeryDTO extends DataObject{
    private String vehicleRegistrationNumber;
    private String route;
    private String numberOfTrips;
    private int numberOfWaybills;
    private int numberOfParcels;
    private double totalRate;
    private double totalSurcharges;
    private double totalFreightRate;
    private double totalFuelSurcharge;
    private double totalVolumetricWeight;
    private double totalActualWeight;

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getNumberOfTrips() {
        return numberOfTrips;
    }

    public void setNumberOfTrips(String numberOfTrips) {
        this.numberOfTrips = numberOfTrips;
    }

    public int getNumberOfWaybills() {
        return numberOfWaybills;
    }

    public void setNumberOfWaybills(int numberOfWaybills) {
        this.numberOfWaybills = numberOfWaybills;
    }

    public int getNumberOfParcels() {
        return numberOfParcels;
    }

    public void setNumberOfParcels(int numberOfParcels) {
        this.numberOfParcels = numberOfParcels;
    }

    public double getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(double totalRate) {
        this.totalRate = totalRate;
    }

    public double getTotalSurcharges() {
        return totalSurcharges;
    }

    public void setTotalSurcharges(double totalSurcharges) {
        this.totalSurcharges = totalSurcharges;
    }

    public double getTotalFuelSurcharge() {
        return totalFuelSurcharge;
    }

    public void setTotalFuelSurcharge(double totalFuelSurcharge) {
        this.totalFuelSurcharge = totalFuelSurcharge;
    }

    public double getTotalVolumetricWeight() {
        return totalVolumetricWeight;
    }

    public void setTotalVolumetricWeight(double totalVolumetricWeight) {
        this.totalVolumetricWeight = totalVolumetricWeight;
    }

    public double getTotalActualWeight() {
        return totalActualWeight;
    }

    public void setTotalActualWeight(double totalActualWeight) {
        this.totalActualWeight = totalActualWeight;
    }

    public double getTotalFreightRate() {
        return totalFreightRate;
    }

    public void setTotalFreightRate(double totalFreightRate) {
        this.totalFreightRate = totalFreightRate;
    }

    @Override
    public String toString() {
        return "SkynetVehicleSummeryDTO{" +
                "vehicleRegistrationNumber='" + vehicleRegistrationNumber + '\'' +
                ", route='" + route + '\'' +
                ", numberOfTrips='" + numberOfTrips + '\'' +
                ", numberOfWaybills=" + numberOfWaybills +
                ", numberOfParcels=" + numberOfParcels +
                ", totalRate=" + totalRate +
                ", totalSurcharges=" + totalSurcharges +
                ", totalFreightRate=" + totalFreightRate +
                ", totalFuelSurcharge=" + totalFuelSurcharge +
                ", totalVolumetricWeight=" + totalVolumetricWeight +
                ", totalActualWeight=" + totalActualWeight +
                '}';
    }
}
