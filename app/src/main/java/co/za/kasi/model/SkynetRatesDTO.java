package co.za.kasi.model;

public class SkynetRatesDTO extends DataObject{
    private String oid;
    private double fixCost;
    private double fuelSurcharge;
    private double sdxrate;
    private double paxiRate;
    private double earRate;
    private String route;
    private SkynetTiersRatesDTO tierRates;

    @Override
    public String getOid() {
        return oid;
    }

    @Override
    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public double getFixCost() {
        return fixCost;
    }

    public void setFixCost(double fixCost) {
        this.fixCost = fixCost;
    }

    public double getFuelSurcharge() {
        return fuelSurcharge;
    }

    public void setFuelSurcharge(double fuelSurcharge) {
        this.fuelSurcharge = fuelSurcharge;
    }

    public double getSdxrate() {
        return sdxrate;
    }

    public void setSdxrate(double sdxrate) {
        this.sdxrate = sdxrate;
    }

    public double getPaxiRate() {
        return paxiRate;
    }

    public void setPaxiRate(double paxiRate) {
        this.paxiRate = paxiRate;
    }

    public double getEarRate() {
        return earRate;
    }

    public void setEarRate(double earRate) {
        this.earRate = earRate;
    }

    public SkynetTiersRatesDTO getTierRates() {
        return tierRates;
    }

    public void setTierRates(SkynetTiersRatesDTO tierRates) {
        this.tierRates = tierRates;
    }

    @Override
    public String toString() {
        return "SkynetRatesDtO{" +
                "oid='" + oid + '\'' +
                ", fixCost='" + fixCost + '\'' +
                ", fuelSurcharge='" + fuelSurcharge + '\'' +
                ", sdxrate='" + sdxrate + '\'' +
                ", paxiRate='" + paxiRate + '\'' +
                ", earRate='" + earRate + '\'' +
                ", tierRates=" + tierRates +
                '}';
    }
}
