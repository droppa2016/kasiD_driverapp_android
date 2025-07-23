package co.za.kasi.model.driverOwner;


import java.io.Serializable;
import java.util.List;

import co.za.kasi.model.DataObject;


public class Billing extends DataObject implements Serializable {

    String startDate;
    String endDate;
    double sumTotal;
    List<RatePerRouteBilling> billings;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(double sumTotal) {
        this.sumTotal = sumTotal;
    }

    public List<RatePerRouteBilling> getBillings() {
        return billings;
    }

    public void setBillings(List<RatePerRouteBilling> billings) {
        this.billings = billings;
    }

    @Override
    public String toString() {
        return "Billing{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", sumTotal=" + sumTotal +
                ", billings=" + billings +
                '}';
    }
}
