package co.za.kasi.model.accountDTO;

import java.io.Serializable;
import java.util.List;

public class DriverDTO implements Serializable {
    private String oid;
    private String businessKey;
    private LicenseDTO licence;
    private PersonDTO person;
    private String status;
    private VehicleDTO vehicle;
    private String creationStamp;
    private String location;
    private String province;
    private String contractOwnerId;
    private double ratingScore;
   // private List<String> ratings;
    private int deratingScore;
    private List<String> retailOids;
    private String retailTag;
    private boolean allowRental;

    // Add getters and setters here


    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public LicenseDTO getLicence() {
        return licence;
    }

    public void setLicence(LicenseDTO licence) {
        this.licence = licence;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }

    public String getCreationStamp() {
        return creationStamp;
    }

    public void setCreationStamp(String creationStamp) {
        this.creationStamp = creationStamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getContractOwnerId() {
        return contractOwnerId;
    }

    public void setContractOwnerId(String contractOwnerId) {
        this.contractOwnerId = contractOwnerId;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

 /*   public List<String> getRatings() {
        return ratings;
    }

    public void setRatings(List<String> ratings) {
        this.ratings = ratings;
    }*/

    public int getDeratingScore() {
        return deratingScore;
    }

    public void setDeratingScore(int deratingScore) {
        this.deratingScore = deratingScore;
    }

    public List<String> getRetailOids() {
        return retailOids;
    }

    public void setRetailOids(List<String> retailOids) {
        this.retailOids = retailOids;
    }

    public String getRetailTag() {
        return retailTag;
    }

    public void setRetailTag(String retailTag) {
        this.retailTag = retailTag;
    }

    public boolean isAllowRental() {
        return allowRental;
    }

    public void setAllowRental(boolean allowRental) {
        this.allowRental = allowRental;
    }

    @Override
    public String toString() {
        return "DriverDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", licence=" + licence +
                ", person=" + person +
                ", status='" + status + '\'' +
                ", vehicle=" + vehicle +
                ", creationStamp='" + creationStamp + '\'' +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                ", contractOwnerId='" + contractOwnerId + '\'' +
                ", ratingScore=" + ratingScore +
 //               ", ratings=" + ratings +
                ", deratingScore=" + deratingScore +
                ", retailOids=" + retailOids +
                ", retailTag='" + retailTag + '\'' +
                ", allowRental=" + allowRental +
                '}';
    }


}
