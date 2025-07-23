package co.za.kasi.model.driverOwner;

import java.util.List;

public class RateStructure {
    List<TierStructure> tiers;
    double fixedAmount;



    public List<TierStructure> getTiers() {
        return tiers;
    }

    public void setTiers(List<TierStructure> tiers) {
        this.tiers = tiers;
    }


    public double getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    @Override
    public String toString() {
        return "RateStructure{" +
                "tiers=" + tiers +
                ", fixedAmount=" + fixedAmount +
                '}';
    }
}
