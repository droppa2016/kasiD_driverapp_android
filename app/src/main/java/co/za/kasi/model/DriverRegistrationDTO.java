package co.za.kasi.model;

import co.za.kasi.model.accountDTO.LicenseDTO;

public class DriverRegistrationDTO extends DataObject{

    private String peronId;

    private LicenseDTO licence;

    private Avatar avatar;

    private String location;

    private String province;

    public DriverRegistrationDTO(String personId, LicenseDTO license, Avatar avatar, String location, String province) {
        this.peronId = personId;
        this.licence = license;
        this.avatar = avatar;
        this.location = location;
        this.province = province;
    }

    public DriverRegistrationDTO(String personId,LicenseDTO license) {
        this.peronId = personId;
        this.licence = license;
    }

    public DriverRegistrationDTO(String personId, Avatar avatar, String location, String province) {
        this.peronId = personId;
        this.avatar = avatar;
        this.location = location;
        this.province = province;
    }

    public String getPersonId() {
        return peronId;
    }

    public void setPersonId(String personId) {
        this.peronId = personId;
    }

    public LicenseDTO getLicense() {
        return licence;
    }

    public void setLicense(LicenseDTO license) {
        this.licence = license;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
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

    @Override
    public String toString() {
        return "DriverRegistrationDTO{" +
                "peronId='" + peronId + '\'' +
                ", licence=" + licence +
                ", avatar=" + avatar +
                ", location='" + location + '\'' +
                ", province='" + province + '\'' +
                '}';
    }
}
