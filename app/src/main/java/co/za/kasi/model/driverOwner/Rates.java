package co.za.kasi.model.driverOwner;

import co.za.kasi.model.DataObject;

import java.io.Serializable;

public class Rates extends DataObject implements Serializable {
    String province;
    String branch;
    String name;
    String rateType;
    String ownerId;
    public RateStructure rate;
    String rateTypeObj;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public RateStructure getRates() {
        return rate;
    }

    public void setRates(RateStructure rate) {
        this.rate = rate;
    }

    public String getRateTypeObj() {
        return rateTypeObj;
    }

    public void setRateTypeObj(String rateTypeObj) {
        this.rateTypeObj = rateTypeObj;
    }

    @Override
    public String toString() {
        return "Rates{" +
                "province='" + province + '\'' +
                ", branch='" + branch + '\'' +
                ", name='" + name + '\'' +
                ", rateType='" + rateType + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", rate=" + rate +
                ", rateTypeObj='" + rateTypeObj + '\'' +
                '}';
    }
}
