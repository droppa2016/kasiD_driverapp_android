package co.za.kasi.model;

import java.io.Serializable;

public class LicenceImage implements Serializable {

    public String licenceFrontImage; //image of the front of licence

    public String licenceBackImage; // image of the back of licenc

    public String driverId;

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
