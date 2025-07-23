package co.za.kasi.model.driverOwner;

public class TierStructure {
    int maxWaybills;
    int minWaybills;
    double amount;

    public int getMaxWaybills() {
        return maxWaybills;
    }

    public void setMaxWaybills(int maxWaybills) {
        this.maxWaybills = maxWaybills;
    }

    public int getMinWaybills() {
        return minWaybills;
    }

    public void setMinWaybills(int minWaybills) {
        this.minWaybills = minWaybills;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TierStructure{" +
                "maxWaybills=" + maxWaybills +
                ", minWaybills=" + minWaybills +
                ", amount=" + amount +
                '}';
    }
}
