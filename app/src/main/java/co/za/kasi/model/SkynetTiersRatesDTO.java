package co.za.kasi.model;

public class SkynetTiersRatesDTO extends DataObject{
    private double minCount;
    private double maxCount;
    private double minimumRate;
    private double perUnitRate;

    public double getMinCount() {
        return minCount;
    }

    public void setMinCount(double minCount) {
        this.minCount = minCount;
    }

    public double getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(double maxCount) {
        this.maxCount = maxCount;
    }

    public double getMinimumRate() {
        return minimumRate;
    }

    public void setMinimumRate(double minimumRate) {
        this.minimumRate = minimumRate;
    }

    public double getPerUnitRate() {
        return perUnitRate;
    }

    public void setPerUnitRate(double perUnitRate) {
        this.perUnitRate = perUnitRate;
    }

    @Override
    public String toString() {
        return "SkynetTiersRatesDTO{" +
                "minCount='" + minCount + '\'' +
                ", maxCount='" + maxCount + '\'' +
                ", minimumRate='" + minimumRate + '\'' +
                ", perUnitRate='" + perUnitRate + '\'' +
                '}';
    }
}
