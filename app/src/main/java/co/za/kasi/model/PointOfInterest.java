package co.za.kasi.model;

public class PointOfInterest extends DataObject {

    private DriverLocationDTO location;

    private String name;

    private String province;

    private String type;

    public DriverLocationDTO getLocation() {
        return location;
    }

    public void setLocation(DriverLocationDTO location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "location=" + location +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
