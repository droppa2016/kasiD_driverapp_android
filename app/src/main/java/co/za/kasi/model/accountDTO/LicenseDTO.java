package co.za.kasi.model.accountDTO;

import co.za.kasi.model.DataObject;

public class LicenseDTO extends DataObject {
   /* private String oid;
    private String businessKey;*/
    private String number;
    private String issueDate;
    private String code;
    private String restrictions;
    private String licenceFrontImage;
    private String licenceBackImage;

    public LicenseDTO(String number, String issueDate, String code, String restrictions, String licenceFrontImage, String licenceBackImage) {
        this.number = number;
        this.issueDate = issueDate;
        this.code = code;
        this.restrictions = restrictions;
        this.licenceFrontImage = licenceFrontImage;
        this.licenceBackImage = licenceBackImage;
    }



    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getLicenceFrontImage() {
        return licenceFrontImage;
    }

    public void setLicenceFrontImage(String licenceFrontImage) {
        this.licenceFrontImage = licenceFrontImage;
    }

    public String getLicenceBackImage() {
        return licenceBackImage;
    }

    public void setLicenceBackImage(String licenceBackImage) {
        this.licenceBackImage = licenceBackImage;
    }

    @Override
    public String toString() {
        return "LicenseDTO{" +
                "number='" + number + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", code='" + code + '\'' +
                ", restrictions='" + restrictions + '\'' +
                ", licenceFrontImage='" + licenceFrontImage + '\'' +
                ", licenceBackImage='" + licenceBackImage + '\'' +
                '}';
    }
}
