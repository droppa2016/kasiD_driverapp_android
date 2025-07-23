package co.za.kasi.model;

public class Avatar extends DataObject{

    private String personId;

    private String base64Image;

    public Avatar(String personId, String base64Image) {
        this.personId = personId;
        this.base64Image = base64Image;
    }

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
        return "Avatar{" +
                "personId='" + personId + '\'' +
                ", base64Image='" + base64Image + '\'' +
                '}';
    }
}
