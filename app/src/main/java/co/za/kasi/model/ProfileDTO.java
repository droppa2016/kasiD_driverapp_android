package co.za.kasi.model;

import java.io.Serializable;

public class ProfileDTO extends DataObject implements Serializable {

    private String personId;
    private String base64Image;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    @Override
    public String toString() {
        return "ProfileDTO{" +
                "personId='" + personId + '\'' +
                ", base64Image='" + base64Image + '\'' +
                '}';
    }
}
