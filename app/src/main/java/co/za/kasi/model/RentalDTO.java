package co.za.kasi.model;

import java.io.Serializable;


public class RentalDTO extends DataObject implements Serializable {

    public LocationDTO from;

    public String branchOid;

    public String in;

    public String out;

    public String rateUnit;

    public String category;

    public Integer assistance;

    public Integer includedDistance;

    public String comments;

    public String customerOid;

    public String status;

    public double total;

    public String platform;

    public String startingPoint;

    public LocationDTO getFrom() {
        return from;
    }

    public void setFrom(LocationDTO from) {
        this.from = from;
    }

    public String getBranchOid() {
        return branchOid;
    }

    public void setBranchOid(String branchOid) {
        this.branchOid = branchOid;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public String getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(String rateUnit) {
        this.rateUnit = rateUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getAssistance() {
        return assistance;
    }

    public void setAssistance(Integer assistance) {
        this.assistance = assistance;
    }

    public Integer getIncludedDistance() {
        return includedDistance;
    }

    public void setIncludedDistance(Integer includedDistance) {
        this.includedDistance = includedDistance;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCustomerOid() {
        return customerOid;
    }

    public void setCustomerOid(String customerOid) {
        this.customerOid = customerOid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }
}
